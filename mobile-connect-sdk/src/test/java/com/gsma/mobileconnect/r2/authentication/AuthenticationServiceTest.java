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

import com.gsma.mobileconnect.r2.InvalidArgumentException;
import com.gsma.mobileconnect.r2.InvalidResponseException;
import com.gsma.mobileconnect.r2.MobileConnectConfig;
import com.gsma.mobileconnect.r2.claims.Claims;
import com.gsma.mobileconnect.r2.claims.ClaimsParameter;
import com.gsma.mobileconnect.r2.discovery.SupportedVersions;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.json.JsonSerializationException;
import com.gsma.mobileconnect.r2.rest.IRestClient;
import com.gsma.mobileconnect.r2.rest.RequestFailedException;
import com.gsma.mobileconnect.r2.rest.RestAuthentication;
import com.gsma.mobileconnect.r2.rest.RestClient;
import com.gsma.mobileconnect.r2.utils.HttpUtils;
import com.gsma.mobileconnect.r2.utils.KeyValuePair;
import com.gsma.mobileconnect.r2.utils.TestUtils;
import org.apache.http.HttpStatus;
import org.mockito.Mockito;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * Tests {@link AuthenticationService}
 *
 * @since 2.0
 */
public class AuthenticationServiceTest
{
    private final static URI REDIRECT_URL = URI.create("http://localhost:8080/");
    private final static URI AUTHORIZE_URL = URI.create("http://localhost:8080/authorize");
    private final static URI TOKEN_URL = URI.create("http://localhost:8080/token");

    private final IJsonService jsonService = new JacksonJsonService();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final IRestClient restClient = Mockito.mock(RestClient.class);

    private final SupportedVersions defaultVersions =
        new SupportedVersions.Builder().addSupportedVersion("openid", "mc_v1.2").build();

    private final IAuthenticationService authentication = new AuthenticationService.Builder()
        .withRestClient(this.restClient)
        .withJsonService(this.jsonService)
        .withExecutorService(this.executorService)
        .build();

    private final MobileConnectConfig config = new MobileConnectConfig.Builder()
        .withClientId("1234567890")
        .withClientSecret("1234567890")
        .withDiscoveryUrl(URI.create("http://localhost:8080/v2/discovery/"))
        .withRedirectUrl(REDIRECT_URL)
        .build();

    @Test
    public void startAuthenticationReturnsUrlWhenArgumentsValid()
    {
        final StartAuthenticationResponse response =
            this.authentication.startAuthentication(this.config.getClientId(), AUTHORIZE_URL,
                REDIRECT_URL, "state", "nonce", null, null, null);

        assertNotNull(response);
        assertTrue(response.getUrl().toString().contains(AUTHORIZE_URL.toString()));
    }

    @DataProvider
    public Object[][] startAuthenticationCoercesScopeData()
    {
        return new Object[][] {{"mc_v1.1", "openid mc_authn", "openid"},
                               {"mc_v1.2", "openid mc_authn", "openid mc_authn"},
                               {"mc_v1.2", "openid", "openid mc_authn"}};
    }

    @Test(dataProvider = "startAuthenticationCoercesScopeData")
    public void startAuthenticationCoercesScope(final String version, final String initialScope,
        final String expectedScope)
    {
        final SupportedVersions versions =
            new SupportedVersions.Builder().addSupportedVersion("openid", version).build();
        final AuthenticationOptions options =
            new AuthenticationOptions.Builder().withScope(initialScope).build();

        final StartAuthenticationResponse response =
            this.authentication.startAuthentication(this.config.getClientId(), AUTHORIZE_URL,
                REDIRECT_URL, "state", "nonce", null, versions, options);
        final String actualScope = HttpUtils.extractQueryValue(response.getUrl(), "scope");

        assertNotNull(actualScope);
        assertEqualsNoOrder(actualScope.split("\\s"), expectedScope.split("\\s"));
    }

    @Test
    public void startAuthenticationWithMc_AuthzScopeShouldAddAuthorizationArguments()
    {
        final AuthenticationOptions options = new AuthenticationOptions.Builder()
            .withScope("openid mc_authz")
            .withClientName("test")
            .withContext("context-val")
            .withBindingMessage("binding-val")
            .build();

        final StartAuthenticationResponse response =
            this.authentication.startAuthentication(this.config.getClientId(), AUTHORIZE_URL,
                REDIRECT_URL, "state", "nonce", null, this.defaultVersions, options);

        assertEquals(HttpUtils.extractQueryValue(response.getUrl(), "context"), "context-val");
        assertEquals(HttpUtils.extractQueryValue(response.getUrl(), "client_name"), "test");
        assertEquals(HttpUtils.extractQueryValue(response.getUrl(), "binding_message"),
            "binding-val");
    }

    @Test
    public void startAuthenticationWithContextShouldUseAuthorizationScope()
    {
        final String initialScope = "openid";
        final String expectedScope = "openid mc_authz";
        final AuthenticationOptions options = new AuthenticationOptions.Builder()
            .withScope(initialScope)
            .withClientName("clientName-val")
            .withContext("context-val")
            .build();

        final StartAuthenticationResponse response =
            this.authentication.startAuthentication(this.config.getClientId(), AUTHORIZE_URL,
                REDIRECT_URL, "state", "nonce", null, this.defaultVersions, options);

        assertEquals(HttpUtils.extractQueryValue(response.getUrl(), "scope"), expectedScope);
    }

    @Test(expectedExceptions = InvalidArgumentException.class, expectedExceptionsMessageRegExp = "Required parameter 'context' was null or empty")
    public void startAuthenticationWithMobileConnectProductScopeShouldUseAuthorization()
    {
        final String initialScope = "openid mc_authn mc_identity_phone";
        final AuthenticationOptions options =
            new AuthenticationOptions.Builder().withScope(initialScope).build();

        this.authentication.startAuthentication(this.config.getClientId(), AUTHORIZE_URL,
            REDIRECT_URL, "state", "nonce", null, this.defaultVersions, options);
    }

    @Test
    public void startAuthenticationWithClaimsShouldEncodeAndIncludeClaims()
        throws JsonSerializationException
    {
        final ClaimsParameter claimsParameter = new ClaimsParameter.Builder()
            .withIdToken(new Claims.Builder().addEssential("test1"))
            .withUserinfo(new Claims.Builder().add("test2", false, "testvalue"))
            .build();

        final AuthenticationOptions options =
            new AuthenticationOptions.Builder().withClaims(claimsParameter).build();
        final String expectedClaims = this.jsonService.serialize(claimsParameter);

        final StartAuthenticationResponse response =
            this.authentication.startAuthentication(this.config.getClientId(), AUTHORIZE_URL,
                REDIRECT_URL, "state", "nonce", null, this.defaultVersions, options);

        assertEquals(HttpUtils.extractQueryValue(response.getUrl(), "claims"), expectedClaims);
    }

    @Test
    public void startAuthenticationWithClaimsShouldEncodeAndIncludeClaimsJson()
    {
        final String claims = "{\"user_info\":{\"test1\":{\"value\":\"test\"}}}";
        final AuthenticationOptions options =
            new AuthenticationOptions.Builder().withClaimsJson(claims).build();

        final StartAuthenticationResponse response =
            this.authentication.startAuthentication(this.config.getClientId(), AUTHORIZE_URL,
                REDIRECT_URL, "state", "nonce", null, this.defaultVersions, options);

        assertEquals(HttpUtils.extractQueryValue(response.getUrl(), "claims"), claims);
    }

    @Test
    public void requestTokenShouldHandleTokenResponse()
        throws RequestFailedException, InvalidResponseException
    {
        when(this.restClient.postFormData(eq(TOKEN_URL), isA(RestAuthentication.class),
            anyListOf(KeyValuePair.class), isNull(String.class),
            isNull(Iterable.class))).thenReturn(TestUtils.TOKEN_RESPONSE);

        final RequestTokenResponse response =
            this.authentication.requestToken(this.config.getClientId(),
                this.config.getClientSecret(), TOKEN_URL, REDIRECT_URL, "code");

        assertNotNull(response);
        assertEquals(response.getResponseCode(), HttpStatus.SC_ACCEPTED);
        assertNotNull(response.getResponseData());
        assertEquals(response.getResponseData().getAccessToken(),
            "966ad150-16c5-11e6-944f-43079d13e2f3");
    }

    @Test
    public void requestTokenShouldHandleInvalidCodeResponse()
        throws RequestFailedException, InvalidResponseException
    {
        when(this.restClient.postFormData(eq(TOKEN_URL), isA(RestAuthentication.class),
            anyListOf(KeyValuePair.class), isNull(String.class),
            isNull(Iterable.class))).thenReturn(TestUtils.INVALID_CODE_RESPONSE);

        final RequestTokenResponse response =
            this.authentication.requestToken(this.config.getClientId(),
                this.config.getClientSecret(), TOKEN_URL, REDIRECT_URL, "code");

        assertNotNull(response);
        assertEquals(response.getResponseCode(), HttpStatus.SC_BAD_REQUEST);
        assertNotNull(response.getErrorResponse());
        assertEquals(response.getErrorResponse().getError(), "invalid_grant");
        assertEquals(response.getErrorResponse().getErrorDescription(),
            "Authorization code doesn't exist or is invalid for the client");
    }

    @Test(expectedExceptions = RequestFailedException.class)
    public void requestTokenShouldHandleHttpRequestException()
        throws RequestFailedException, InvalidResponseException
    {
        when(this.restClient.postFormData(eq(TOKEN_URL), isA(RestAuthentication.class),
            anyListOf(KeyValuePair.class), isNull(String.class), isNull(Iterable.class))).thenThrow(
            new RequestFailedException(HttpUtils.HttpMethod.POST, TOKEN_URL,
                new Exception("test")));

        this.authentication.requestToken(this.config.getClientId(), this.config.getClientSecret(),
            TOKEN_URL, REDIRECT_URL, "code");
    }

    @DataProvider
    public Object[][] startAuthenticationRequiredArgsData()
    {
        return new Object[][] {{null, AUTHORIZE_URL, REDIRECT_URL, "state", "nonce"},
                               {this.config.getClientId(), null, REDIRECT_URL, "state", "nonce"},
                               {this.config.getClientId(), AUTHORIZE_URL, null, "state", "nonce"},
                               {this.config.getClientId(), AUTHORIZE_URL, REDIRECT_URL, null,
                                "nonce"},
                               {this.config.getClientId(), AUTHORIZE_URL, REDIRECT_URL, "state",
                                null}};
    }

    @Test(dataProvider = "startAuthenticationRequiredArgsData", expectedExceptions = IllegalArgumentException.class)
    public void startAuthenticationShouldThrowWhenRequiredArgIsNull(final String clientId,
        final URI authorizeUrl, final URI redirectUrl, final String state, final String nonce)
    {
        this.authentication.startAuthentication(clientId, authorizeUrl, redirectUrl, state, nonce,
            null, null, null);
    }

    @DataProvider
    public Object[][] startAuthenticationRequiredOptionsData()
    {
        return new Object[][] {{"context", null}, {null, "client"}};
    }

    @Test(dataProvider = "startAuthenticationRequiredOptionsData", expectedExceptions = IllegalArgumentException.class)
    public void startAuthenticationShouldThrowWhenRequiredOptionsNullAndShouldUseAuthz(
        final String context, final String clientId)
    {
        final AuthenticationOptions options =
            new AuthenticationOptions.Builder().withContext(context).withClientId(clientId).build();
        this.authentication.startAuthentication(this.config.getClientId(), AUTHORIZE_URL,
            REDIRECT_URL, "state", null, null, null, options);
    }

    @DataProvider
    public Object[][] requestTokenRequiredArgsData()
    {
        final String clientId = this.config.getClientId();
        final String clientSecret = this.config.getClientSecret();

        return new Object[][] {{"", clientSecret, TOKEN_URL, REDIRECT_URL, "code"},
                               {clientId, "", TOKEN_URL, REDIRECT_URL, "code"},
                               {clientId, clientSecret, null, REDIRECT_URL, "code"},
                               {clientId, clientSecret, TOKEN_URL, null, "code"},
                               {clientId, clientSecret, TOKEN_URL, REDIRECT_URL, ""},};
    }

    @Test(dataProvider = "requestTokenRequiredArgsData", expectedExceptions = IllegalArgumentException.class)
    public void requestTokenShouldThrowWhenRequiredArgIsNull(final String clientId,
        final String clientSecret, final URI requestTokenUrl, final URI redirectUrl,
        final String code) throws RequestFailedException, InvalidResponseException
    {
        this.authentication.requestToken(clientId, clientSecret, requestTokenUrl, redirectUrl,
            code);
    }
}
