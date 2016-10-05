/*
 * SOFTWARE USE PERMISSION
 *
 * By downloading and accessing this software and associated documentation files ("Software") you are granted the
 * unrestricted right to deal in the Software, including, without limitation the right to use, copy, modify, publish,
 * sublicense and grant such rights to third parties, subject to the following conditions:
 *
 * The following copyright notice and this permission notice shall be included in all copies, modifications or
 * substantial portions of this Software: Copyright © 2016 GSM Association.
 *
 * THE SOFTWARE IS PROVIDED "AS IS," WITHOUT WARRANTY OF ANY KIND, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. YOU AGREE TO
 * INDEMNIFY AND HOLD HARMLESS THE AUTHORS AND COPYRIGHT HOLDERS FROM AND AGAINST ANY SUCH LIABILITY.
 */
package com.gsma.mobileconnect.r2.authentication;

import com.gsma.mobileconnect.r2.ErrorResponse;
import com.gsma.mobileconnect.r2.InvalidResponseException;
import com.gsma.mobileconnect.r2.constants.DefaultOptions;
import com.gsma.mobileconnect.r2.constants.Parameters;
import com.gsma.mobileconnect.r2.constants.Scope;
import com.gsma.mobileconnect.r2.constants.Scopes;
import com.gsma.mobileconnect.r2.discovery.SupportedVersions;
import com.gsma.mobileconnect.r2.encoding.DefaultEncodeDecoder;
import com.gsma.mobileconnect.r2.encoding.IMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JsonSerializationException;
import com.gsma.mobileconnect.r2.rest.IRestClient;
import com.gsma.mobileconnect.r2.rest.RequestFailedException;
import com.gsma.mobileconnect.r2.rest.RestAuthentication;
import com.gsma.mobileconnect.r2.rest.RestResponse;
import com.gsma.mobileconnect.r2.utils.*;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Concrete implementation of {@link IAuthenticationService}
 *
 * @since 2.0
 */
public class AuthenticationService implements IAuthenticationService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);
    public static final String REVOKE_TOKEN_SUCCESS = "Revoke token successful";
    public static final String UNSUPPORTED_TOKEN_TYPE_ERROR = "Unsupported token type";

    private final IJsonService jsonService;
    private final ExecutorService executorService;
    private final IRestClient restClient;
    private final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder;

    private AuthenticationService(final Builder builder)
    {
        this.jsonService = builder.jsonService;
        this.executorService = builder.executorService;
        this.restClient = builder.restClient;
        this.iMobileConnectEncodeDecoder = builder.iMobileConnectEncodeDecoder;

        LOGGER.info("New instance of AuthenticationService created");
    }

    @Override
    public StartAuthenticationResponse startAuthentication(final String clientId,
        final URI authorizeUrl, final URI redirectUrl, final String state, final String nonce,
        final String encryptedMSISDN, final SupportedVersions versions,
        final AuthenticationOptions options)
    {
        String loginHint = null;
        if (options != null)
        {
            if (options.getLoginHint() != null)
            {
                loginHint = options.getLoginHint();
            }
            else if (encryptedMSISDN != null)
            {
                loginHint = String.format("ENCR_MSISDN:%s", encryptedMSISDN);
            }
        }

        final AuthenticationOptions.Builder optionsBuilder =
            new AuthenticationOptions.Builder(options)
                .withState(StringUtils.requireNonEmpty(state, "state"))
                .withNonce(StringUtils.requireNonEmpty(nonce, "nonce"))
                .withLoginHint(loginHint)
                .withRedirectUrl(ObjectUtils.requireNonNull(redirectUrl, "redirectUrl"))
                .withClientId(StringUtils.requireNonEmpty(clientId, "clientId"));

        final String scope;
        final String context;
        if (options == null)
        {
            scope = "";
            context = "";
        }
        else
        {
            scope = ObjectUtils.defaultIfNull(options.getScope(), "").toLowerCase();
            context = options.getContext();
        }

        final boolean useAuthorize = this.shouldUseAuthorize(scope, context);

        if (useAuthorize)
        {
            StringUtils.requireNonEmpty(options == null ? null : options.getContext(), "context");
            StringUtils.requireNonEmpty(options == null ? null : options.getClientName(),
                "clientName");
        }

        final String version =
            this.coerceAuthenticationScope(scope, optionsBuilder, versions, useAuthorize);

        try
        {
            final URI uri = new URIBuilder(ObjectUtils.requireNonNull(authorizeUrl, "authorizeUrl"))
                .addParameters(
                    this.getAuthenticationQueryParams(optionsBuilder.build(), useAuthorize,
                        version))
                .build();

            return new StartAuthenticationResponse(uri);
        }
        catch (final URISyntaxException use)
        {
            LOGGER.warn("Failed to construct uri for startAuthentication", use);
            throw new IllegalArgumentException("Failed to construct uri for startAuthentication",
                use);
        }
    }

    private boolean shouldUseAuthorize(final String scope, final String context)
    {
        final int authnIndex = scope.indexOf(Scope.AUTHN.toLowerCase());
        final boolean authnRequested = authnIndex > -1;
        final boolean mcProductRequested =
            scope.lastIndexOf(Scope.MCPREFIX.toLowerCase()) != authnIndex;

        return mcProductRequested || (!authnRequested && !StringUtils.isNullOrEmpty(context));
    }

    /**
     * Fetches the version required and modifies the scope based upon it.  mc_authn may be added or
     * removed from the scopes depending on the version required.
     *
     * @param scope          specified in the original request.
     * @param optionsBuilder to store the modified scopes to.
     * @param versions       specified in the original request.
     * @param useAuthorize   should mc_authz be used over mc_authn?
     * @return the version of the scope to use.  The modified scope will be stored to the
     * optionsBuilder.
     */
    private String coerceAuthenticationScope(final String scope,
        final AuthenticationOptions.Builder optionsBuilder, final SupportedVersions versions,
        final boolean useAuthorize)
    {
        final String requiredScope =
            useAuthorize ? Scopes.MOBILECONNECTAUTHORIZATION : Scopes.MOBILECONNECTAUTHENTICATION;
        final String disallowedScope = useAuthorize ? Scope.AUTHN : Scope.AUTHZ;

        final String version =
            new SupportedVersions.Builder(versions).build().getSupportedVersion(requiredScope);

        final List<String> scopes =
            Scopes.coerceOpenIdScope(Arrays.asList(scope.split("\\s")), requiredScope);

        ListUtils.removeIgnoreCase(scopes, disallowedScope);

        if (!useAuthorize && DefaultOptions.VERSION_MOBILECONNECTAUTHN.equals(version))
        {
            ListUtils.removeIgnoreCase(scopes, Scope.AUTHN);
        }

        optionsBuilder.withScope(StringUtils.join(scopes, " "));

        return version;
    }

    private List<NameValuePair> getAuthenticationQueryParams(final AuthenticationOptions options,
        final boolean useAuthorize, final String version)
    {
        String claimsJson = options.getClaimsJson();
        if (StringUtils.isNullOrEmpty(claimsJson) && options.getClaims() != null)
        {
            try
            {
                claimsJson = this.jsonService.serialize(options.getClaims());
            }
            catch (final JsonSerializationException jse)
            {
                LOGGER.warn(
                    "Failed to serialize claims into JSON for authentication query parameters",
                    jse);
                throw new IllegalArgumentException(
                    "Failed to serialize claims into JSON for authentication query parameters",
                    jse);
            }
        }

        final KeyValuePair.ListBuilder builder = new KeyValuePair.ListBuilder()
            .add(Parameters.AUTHENTICATION_REDIRECT_URI, options.getRedirectUrl().toString())
            .add(Parameters.CLIENT_ID, options.getClientId())
            .add(Parameters.RESPONSE_TYPE, DefaultOptions.AUTHENTICATION_RESPONSE_TYPE)
            .add(Parameters.SCOPE, options.getScope())
            .add(Parameters.ACR_VALUES, options.getAcrValues())
            .add(Parameters.STATE, options.getState())
            .add(Parameters.NONCE, options.getNonce())
            .add(Parameters.DISPLAY, options.getDisplay())
            .add(Parameters.PROMPT, options.getPrompt())
            .add(Parameters.MAX_AGE, String.valueOf(options.getMaxAge()))
            .add(Parameters.UI_LOCALES, options.getUiLocales())
            .add(Parameters.CLAIMS_LOCALES, options.getClaimsLocales())
            .add(Parameters.ID_TOKEN_HINT, options.getIdTokenHint())
            .add(Parameters.LOGIN_HINT, options.getLoginHint())
            .add(Parameters.DTBS, options.getDbts())
            .add(Parameters.CLAIMS, claimsJson)
            .add(Parameters.VERSION, version);

        if (useAuthorize)
        {
            builder
                .add(Parameters.CLIENT_NAME, options.getClientName())
                .add(Parameters.CONTEXT, options.getContext())
                .add(Parameters.BINDING_MESSAGE, options.getBindingMessage());
        }

        return builder.buildAsNameValuePairList();
    }

    @Override
    public Future<RequestTokenResponse> requestHeadlessAuthentication(final String clientId,
        final String clientSecret, final URI authorizationUrl, final URI requestTokenUrl,
        final URI redirectUrl, final String state, final String nonce, final String encryptedMsisdn,
        final SupportedVersions versions, final AuthenticationOptions options)
        throws RequestFailedException
    {
        final String scope;
        final String context;
        final AuthenticationOptions.Builder optionsBuilder;
        if (options == null)
        {
            optionsBuilder = new AuthenticationOptions.Builder();
            scope = "";
            context = "";
        }
        else
        {
            optionsBuilder = new AuthenticationOptions.Builder(options);
            scope = ObjectUtils.defaultIfNull(options.getScope(), "").toLowerCase();
            context = options.getContext();
        }

        if (this.shouldUseAuthorize(scope, context))
        {
            optionsBuilder.withPrompt(DefaultOptions.PROMPT);
        }

        StartAuthenticationResponse startAuthenticationResponse =
            startAuthentication(clientId, authorizationUrl, redirectUrl, state, nonce,
                encryptedMsisdn, versions, optionsBuilder.build());
        final RestAuthentication authentication =
            RestAuthentication.basic(clientId, clientSecret, iMobileConnectEncodeDecoder);

        URI authUrl = startAuthenticationResponse.getUrl();
        URI finalRedirectUrl = restClient.getFinalRedirect(authUrl, redirectUrl, authentication);

        final String code = HttpUtils.extractQueryValue(finalRedirectUrl, "code");

        return this.executorService.submit(new Callable<RequestTokenResponse>()
        {
            @Override
            public RequestTokenResponse call() throws Exception
            {
                return AuthenticationService.this.requestToken(clientId, clientSecret,
                    requestTokenUrl, redirectUrl, code);
            }
        });
    }

    @Override
    public RequestTokenResponse refreshToken(final String clientId, final String clientSecret,
        final URI refreshTokenUrl, final String refreshToken, final URI redirectUrl)
        throws RequestFailedException, InvalidResponseException
    {
        final List<KeyValuePair> formData = new KeyValuePair.ListBuilder()
            .add(Parameters.AUTHENTICATION_REDIRECT_URI,
                ObjectUtils.requireNonNull(redirectUrl, "redirectUrl").toString())
            .add(Parameters.REFRESH_TOKEN,
                StringUtils.requireNonEmpty(refreshToken, "refreshToken"))
            .add(Parameters.GRANT_TYPE, DefaultOptions.GRANT_TYPE_REFRESH_TOKEN)
            .build();

        final RestAuthentication authentication =
            RestAuthentication.basic(clientId, clientSecret, this.iMobileConnectEncodeDecoder);
        final RestResponse restResponse =
            this.restClient.postFormData(refreshTokenUrl, authentication, formData, null, null);

        return RequestTokenResponse.fromRestResponse(restResponse, this.jsonService,
            this.iMobileConnectEncodeDecoder);
    }

    @Override
    public String revokeToken(final String clientId, final String clientSecret,
        final URI refreshTokenUrl, final String token, final String tokenTypeHint,
        final URI redirectUrl) throws RequestFailedException, InvalidResponseException
    {
        final List<KeyValuePair> formData = new KeyValuePair.ListBuilder()
            .add(Parameters.AUTHENTICATION_REDIRECT_URI,
                ObjectUtils.requireNonNull(redirectUrl, "redirectUrl").toString())
            .add(Parameters.TOKEN, StringUtils.requireNonEmpty(token, "token"))
            .add(Parameters.TOKEN_TYPE_HINT, tokenTypeHint)
            .build();

        final RestAuthentication authentication =
            RestAuthentication.basic(clientId, clientSecret, this.iMobileConnectEncodeDecoder);
        final RestResponse restResponse =
            this.restClient.postFormData(refreshTokenUrl, authentication, formData, null, null);

        // As per the OAuth2 spec an error (non-200 response code) should only be returned by the
        // endpoint for the error code unsupported_token_type
        return restResponse.getStatusCode() == 200
               ? REVOKE_TOKEN_SUCCESS
               : UNSUPPORTED_TOKEN_TYPE_ERROR;
    }

    @Override
    public RequestTokenResponse requestToken(final String clientId, final String clientSecret,
        final URI requestTokenUrl, final URI redirectUrl, final String code)
        throws RequestFailedException, InvalidResponseException
    {
        final List<KeyValuePair> formData = new KeyValuePair.ListBuilder()
            .add(Parameters.AUTHENTICATION_REDIRECT_URI,
                ObjectUtils.requireNonNull(redirectUrl, "redirectUrl").toString())
            .add(Parameters.CODE, StringUtils.requireNonEmpty(code, "code"))
            .add(Parameters.GRANT_TYPE, DefaultOptions.GRANT_TYPE_AUTH_CODE)
            .build();

        final RestAuthentication authentication =
            RestAuthentication.basic(clientId, clientSecret, this.iMobileConnectEncodeDecoder);
        final RestResponse restResponse =
            this.restClient.postFormData(requestTokenUrl, authentication, formData, null, null);

        return RequestTokenResponse.fromRestResponse(restResponse, this.jsonService,
            this.iMobileConnectEncodeDecoder);
    }

    @Override
    public Future<RequestTokenResponse> requestTokenAsync(final String clientId,
        final String clientSecret, final URI requestTokenUrl, final URI redirectUrl,
        final String code)
    {
        return this.executorService.submit(new Callable<RequestTokenResponse>()
        {
            @Override
            public RequestTokenResponse call() throws Exception
            {
                return AuthenticationService.this.requestToken(clientId, clientSecret,
                    requestTokenUrl, redirectUrl, code);
            }
        });
    }

    public static final class Builder implements IBuilder<AuthenticationService>
    {
        private IJsonService jsonService;
        private ExecutorService executorService;
        private IRestClient restClient;
        private IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder;

        public Builder withJsonService(final IJsonService val)
        {
            this.jsonService = val;
            return this;
        }

        public Builder withExecutorService(final ExecutorService val)
        {
            this.executorService = val;
            return this;
        }

        public Builder withRestClient(final IRestClient val)
        {
            this.restClient = val;
            return this;
        }

        public Builder withIMobileConnectEncodeDecoder(final IMobileConnectEncodeDecoder val)
        {
            this.iMobileConnectEncodeDecoder = val;
            return this;
        }

        @Override
        public AuthenticationService build()
        {
            ObjectUtils.requireNonNull(this.jsonService, "jsonService");
            ObjectUtils.requireNonNull(this.executorService, "executorService");
            ObjectUtils.requireNonNull(this.restClient, "restClient");
            if (this.iMobileConnectEncodeDecoder == null)
            {
                this.iMobileConnectEncodeDecoder = new DefaultEncodeDecoder();
            }

            return new AuthenticationService(this);
        }
    }
}
