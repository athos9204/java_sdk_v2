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
package com.gsma.mobileconnect.r2;

import com.gsma.mobileconnect.r2.authentication.IAuthenticationService;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.discovery.IDiscoveryService;
import com.gsma.mobileconnect.r2.identity.IIdentityService;
import com.gsma.mobileconnect.r2.utils.IBuilder;
import com.gsma.mobileconnect.r2.utils.LogUtils;
import com.gsma.mobileconnect.r2.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Convenience wrapper for {@link IDiscoveryService} and {@link IAuthenticationService}
 * methods for use with non-web applications.
 *
 * @since 2.0
 */
public class MobileConnectInterface
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MobileConnectInterface.class);

    private final IDiscoveryService discoveryService;
    private final IAuthenticationService authnService;
    private final IIdentityService identityService;
    private final MobileConnectConfig config;
    private final ExecutorService executorService;

    private MobileConnectInterface(Builder builder)
    {
        this.discoveryService = builder.discoveryService;
        this.authnService = builder.authnService;
        this.identityService = builder.identityService;
        this.config = builder.config;
        this.executorService = builder.executorService;

        LOGGER.info("New instance of MobileConnectInterface created, using config={}", this.config);
    }

    /**
     * Asynchronously attempt discovery using the supplied parameters. If msisdn, mcc and mnc are
     * null the result will be operator selection, otherwise valid parameters will result in a
     * StartAuthorization status.
     *
     * @param msisdn  MSISDN from user
     * @param mcc     Mobile Country Code
     * @param mnc     Mobile Network Code
     * @param options Optional parameters
     * @return MobileConnectStatus object with required information for continuing the Mobile
     * Connect process
     */
    public Future<MobileConnectStatus> attemptDiscoveryAsync(final String msisdn, final String mcc,
        final String mnc, final MobileConnectRequestOptions options)
    {
        LOGGER.debug("Queuing attemptDiscovery async request for msisdn={}, mcc={}, mnc={}",
            LogUtils.mask(msisdn, LOGGER, Level.DEBUG), mcc, mnc);

        return this.executorService.submit(new Callable<MobileConnectStatus>()
        {
            @Override
            public MobileConnectStatus call() throws Exception
            {
                return MobileConnectInterface.this.attemptDiscovery(msisdn, mcc, mnc, options);
            }
        });
    }

    /**
     * Attempt discovery using the supplied parameters. If msisdn, mcc and mnc are null the result
     * will be operator selection, otherwise valid parameters will result in a StartAuthorization
     * status
     *
     * @param msisdn  MSISDN from user
     * @param mcc     Mobile Country Code
     * @param mnc     Mobile Network Code
     * @param options Optional parameters
     * @return MobileConnectStatus object with required information for continuing the Mobile
     * Connect process
     */
    public MobileConnectStatus attemptDiscovery(final String msisdn, final String mcc,
        final String mnc, final MobileConnectRequestOptions options)
    {
        LOGGER.debug("Running attemptDiscovery request for msisdn={}, mcc={}, mnc={}",
            LogUtils.mask(msisdn, LOGGER, Level.DEBUG), mcc, mnc);

        return MobileConnectInterfaceHelper.attemptDiscovery(this.discoveryService, msisdn, mcc,
            mnc, null, this.config, options.getDiscoveryOptionsBuilder());
    }

    /**
     * Asynchronously attempt discovery using the values returned from the operator selection
     * redirect
     *
     * @param redirectedUrl URI redirected to by the completion of the operator selection UI
     * @return MobileConnectStatus object with required information for continuing the Mobile
     * Connect process
     */
    public Future<MobileConnectStatus> attemptDiscoveryAfterOperatorSelectionAsync(
        final URI redirectedUrl)
    {
        LOGGER.debug(
            "Queuing attemptDiscoveryAfterOperatorSelection async request for redirectedUrl={}",
            LogUtils.maskUri(redirectedUrl, LOGGER, Level.DEBUG));

        return this.executorService.submit(new Callable<MobileConnectStatus>()
        {
            @Override
            public MobileConnectStatus call() throws Exception
            {
                return MobileConnectInterface.this.attemptDiscoveryAfterOperatorSelection(
                    redirectedUrl);
            }
        });
    }

    /**
     * Attempt discovery using the values returned from the operator selection redirect
     *
     * @param redirectedUrl URI redirected to by the completion of the operator selection UI
     * @return MobileConnectStatus object with required information for continuing the Mobile
     * Connect process
     */
    public MobileConnectStatus attemptDiscoveryAfterOperatorSelection(final URI redirectedUrl)
    {
        LOGGER.debug(
            "Running attemptDiscoveryAfterOperatorSelection async request for redirectUrl={}",
            LogUtils.maskUri(redirectedUrl, LOGGER, Level.DEBUG));

        return MobileConnectInterfaceHelper.attemptDiscoveryAfterOperatorSelection(
            this.discoveryService, redirectedUrl, this.config);
    }

    /**
     * Creates an authorization url with parameters to begin the authorization process
     *
     * @param discoveryResponse The response returned by the discovery process
     * @param encryptedMsisdn   Encrypted MSISDN/Subscriber Id returned from the Discovery process
     * @param state             Unique state value, this will be returned by the authorization
     *                          process and should be checked for equality as a secURIty measure
     * @param nonce             Unique value to associate a client session with an id token
     * @param options           Optional parameters
     * @return MobileConnectStatus object with required information for continuing the Mobile
     * Connect process
     */
    public MobileConnectStatus startAuthentication(final DiscoveryResponse discoveryResponse,
        final String encryptedMsisdn, final String state, final String nonce,
        final MobileConnectRequestOptions options)
    {
        LOGGER.debug("Running startAuthentication for encryptedMsisdn={}, state={}, nonce={}",
            LogUtils.mask(encryptedMsisdn, LOGGER, Level.DEBUG), state,
            LogUtils.mask(nonce, LOGGER, Level.DEBUG));

        return MobileConnectInterfaceHelper.startAuthentication(this.authnService,
            discoveryResponse, encryptedMsisdn, state, nonce, this.config,
            options.getAuthenticationOptionsBuilder());
    }

    /**
     * Request token using the values returned from the authorization redirect
     *
     * @param discoveryResponse The response returned by the discovery process
     * @param redirectedUrl     URI redirected to by the completion of the authorization UI
     * @param expectedState     The state value returned from the StartAuthorization call should be
     *                          passed here, it will be used to validate the authenticity of the
     *                          authorization process
     * @param expectedNonce     The nonce value returned from the StartAuthorization call should be
     *                          passed here, it will be used to ensure the token was not requested
     *                          using a replay attack
     * @return MobileConnectStatus object with required information for continuing the Mobile
     * Connect process
     */
    public Future<MobileConnectStatus> requestTokenAsync(final DiscoveryResponse discoveryResponse,
        final URI redirectedUrl, final String expectedState, final String expectedNonce)
    {
        LOGGER.debug(
            "Queuing requestToken async for redirectedUrl={}, expectedState={}, expectedNonce={}",
            LogUtils.maskUri(redirectedUrl, LOGGER, Level.DEBUG), expectedState,
            LogUtils.mask(expectedNonce, LOGGER, Level.DEBUG));

        return this.executorService.submit(new Callable<MobileConnectStatus>()
        {
            @Override
            public MobileConnectStatus call() throws Exception
            {
                return MobileConnectInterface.this.requestToken(discoveryResponse, redirectedUrl,
                    expectedState, expectedNonce);
            }
        });
    }

    /**
     * Synchronous wrapper for <see cref="MobileConnectInterface.requestToken(DiscoveryResponse,
     * URI, String, String)"/>
     *
     * @param discoveryResponse The response returned by the discovery process
     * @param redirectedUrl     URI redirected to by the completion of the authorization UI
     * @param expectedState     The state value returned from the StartAuthorization call should be
     *                          passed here, it will be used to validate the authenticity of the
     *                          authorization process
     * @param expectedNonce     The nonce value returned from the StartAuthorization call should be
     *                          passed here, it will be used to ensure the token was not requested
     *                          using a replay attack
     * @return MobileConnectStatus object with required information for continuing the Mobile
     * Connect process
     */
    public MobileConnectStatus requestToken(final DiscoveryResponse discoveryResponse,
        final URI redirectedUrl, final String expectedState, final String expectedNonce)
    {
        LOGGER.debug(
            "Running requestToken for redirectedUrl={}, expectedState={}, expectedNonce={}",
            LogUtils.maskUri(redirectedUrl, LOGGER, Level.DEBUG), expectedState,
            LogUtils.mask(expectedNonce, LOGGER, Level.DEBUG));

        return MobileConnectInterfaceHelper.requestToken(this.authnService, discoveryResponse,
            redirectedUrl, expectedState, expectedNonce, this.config);
    }

    /**
     * Handles continuation of the process following a completed redirect. Only the redirectedUrl is
     * required, however if the redirect being handled is the result of calling the Authorization
     * URL then the remaining parameters are required.
     *
     * @param redirectedUrl     Url redirected to by the completion of the previous step
     * @param discoveryResponse The response returned by the discovery process
     * @param expectedState     The state value returned from the StartAuthorization call should be
     *                          passed here, it will be used to validate the authenticity of the
     *                          authorization process
     * @param expectedNonce     The nonce value returned from the StartAuthorization call should be
     *                          passed here, it will be used to ensure the token was not requested
     *                          using a replay attack
     * @return MobileConnectStatus object with required information for continuing the Mobile
     * Connect process
     */
    public Future<MobileConnectStatus> handleUrlRedirectAsync(final URI redirectedUrl,
        final DiscoveryResponse discoveryResponse, final String expectedState,
        final String expectedNonce)
    {
        LOGGER.debug(
            "Queuing handleUrlRedirect async for redirectedUrl={}, expectedState={}, expectedNonce={}",
            LogUtils.maskUri(redirectedUrl, LOGGER, Level.DEBUG), expectedState,
            LogUtils.mask(expectedNonce, LOGGER, Level.DEBUG));

        return this.executorService.submit(new Callable<MobileConnectStatus>()
        {
            @Override
            public MobileConnectStatus call() throws Exception
            {
                return MobileConnectInterface.this.handleUrlRedirect(redirectedUrl,
                    discoveryResponse, expectedState, expectedNonce);
            }
        });
    }

    /**
     * Synchronous wrapper for <see cref="MobileConnectInterface.HandleUrlRedirectAsync(URI,
     * DiscoveryResponse, String, String)"/>
     *
     * @param redirectedUrl     Url redirected to by the completion of the previous step
     * @param discoveryResponse The response returned by the discovery process
     * @param expectedState     The state value returned from the StartAuthorization call should be
     *                          passed here, it will be used to validate the authenticity of the
     *                          authorization process
     * @param expectedNonce     The nonce value returned from the StartAuthorization call should be
     *                          passed here, it will be used to ensure the token was not requested
     *                          using a replay attack
     * @return MobileConnectStatus object with required information for continuing the Mobile
     * Connect process
     */
    public MobileConnectStatus handleUrlRedirect(final URI redirectedUrl,
        final DiscoveryResponse discoveryResponse, final String expectedState,
        final String expectedNonce)
    {
        LOGGER.debug(
            "Running handleUrlRedirect for redirectedUrl={}, expectedState={}, expectedNonce={}",
            LogUtils.maskUri(redirectedUrl, LOGGER, Level.DEBUG), expectedState,
            LogUtils.mask(expectedNonce, LOGGER, Level.DEBUG));

        return MobileConnectInterfaceHelper.handleUrlRedirect(this.discoveryService,
            this.authnService, redirectedUrl, discoveryResponse, expectedState, expectedNonce,
            this.config);
    }

    /**
     * Request user info using the access token returned by <see cref="requestToken(DiscoveryResponse,
     * URI, String, String)"/>
     *
     * @param discoveryResponse The response returned by the discovery process
     * @param accessToken       Access token from requestToken stage
     * @return MobileConnectStatus object with UserInfo information
     */
    public Future<MobileConnectStatus> requestUserInfoAsync(
        final DiscoveryResponse discoveryResponse, final String accessToken)
    {
        LOGGER.debug("Queuing requestUserInfo async for accessToken={}",
            LogUtils.mask(accessToken, LOGGER, Level.DEBUG));

        return this.executorService.submit(new Callable<MobileConnectStatus>()
        {
            @Override
            public MobileConnectStatus call() throws Exception
            {
                return MobileConnectInterface.this.requestUserInfo(discoveryResponse, accessToken);
            }
        });
    }

    /**
     * Synchronous wrapper for <see cref="RequestUserInfoAsync(DiscoveryResponse, String,
     * MobileConnectRequestOptions)"/>
     *
     * @param discoveryResponse The response returned by the discovery process
     * @param accessToken       Access token from requestToken stage
     * @return MobileConnectStatus object with UserInfo information
     */
    public MobileConnectStatus requestUserInfo(final DiscoveryResponse discoveryResponse,
        final String accessToken)
    {
        LOGGER.debug("Running requestUserInfo for accessToken={}",
            LogUtils.mask(accessToken, LOGGER, Level.DEBUG));

        return MobileConnectInterfaceHelper.requestUserInfo(this.identityService, discoveryResponse,
            accessToken);
    }

    /**
     * Request user info using the access token returned by <see cref="requestToken(DiscoveryResponse,
     * URI, String, String)"/>
     *
     * @param discoveryResponse The response returned by the discovery process
     * @param accessToken       Access token from requestToken stage
     * @return MobileConnectStatus object with UserInfo information
     */
    public Future<MobileConnectStatus> requestIdentityAsync(
        final DiscoveryResponse discoveryResponse, final String accessToken)
    {
        LOGGER.debug("Queuing requestUserInfo async for accessToken={}",
            LogUtils.mask(accessToken, LOGGER, Level.DEBUG));

        return this.executorService.submit(new Callable<MobileConnectStatus>()
        {
            @Override
            public MobileConnectStatus call() throws Exception
            {
                return MobileConnectInterface.this.requestIdentity(discoveryResponse, accessToken);
            }
        });
    }

    /**
     * Synchronous wrapper for <see cref="RequestIdentityAsync(DiscoveryResponse, String,
     * MobileConnectRequestOptions)"/>
     *
     * @param discoveryResponse The response returned by the discovery process
     * @param accessToken       Access token from requestToken stage
     * @return MobileConnectStatus object with UserInfo information
     */
    public MobileConnectStatus requestIdentity(final DiscoveryResponse discoveryResponse,
        final String accessToken)
    {
        LOGGER.debug("Running requestIdentity for accessToken={}",
            LogUtils.mask(accessToken, LOGGER, Level.DEBUG));

        return MobileConnectInterfaceHelper.requestIdentity(this.identityService, discoveryResponse,
            accessToken);
    }

    public static final class Builder implements IBuilder<MobileConnectInterface>
    {
        private IDiscoveryService discoveryService;
        private IAuthenticationService authnService;
        private IIdentityService identityService;
        private MobileConnectConfig config;
        private ExecutorService executorService;

        public Builder withDiscoveryService(final IDiscoveryService val)
        {
            this.discoveryService = val;
            return this;
        }

        public Builder withAuthnService(final IAuthenticationService val)
        {
            this.authnService = val;
            return this;
        }

        public Builder withIdentityService(final IIdentityService val)
        {
            this.identityService = val;
            return this;
        }

        public Builder withConfig(final MobileConnectConfig val)
        {
            this.config = val;
            return this;
        }

        public Builder withExecutorService(final ExecutorService val)
        {
            this.executorService = val;
            return this;
        }

        @Override
        public MobileConnectInterface build()
        {
            ObjectUtils.requireNonNull(this.discoveryService, "discoveryService");
            ObjectUtils.requireNonNull(this.authnService, "authnService");
            ObjectUtils.requireNonNull(this.identityService, "identityService");
            ObjectUtils.requireNonNull(this.config, "config");
            ObjectUtils.requireNonNull(this.executorService, "executorService");

            return new MobileConnectInterface(this);
        }
    }
}
