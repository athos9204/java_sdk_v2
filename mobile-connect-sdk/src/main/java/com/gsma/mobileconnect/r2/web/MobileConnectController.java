package com.gsma.mobileconnect.r2.web;

import com.gsma.mobileconnect.r2.*;
import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.constants.DefaultOptions;
import com.gsma.mobileconnect.r2.constants.Parameters;
import com.gsma.mobileconnect.r2.constants.Scopes;
import com.gsma.mobileconnect.r2.discovery.DiscoveryOptions;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.discovery.OperatorUrls;
import com.gsma.mobileconnect.r2.encoding.DefaultEncodeDecoder;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.utils.HttpUtils;
import com.gsma.mobileconnect.r2.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;


@Controller
@EnableAutoConfiguration
@RequestMapping(path = "api/mobileconnect", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class MobileConnectController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MobileConnectController.class);

    @Autowired
    private MobileConnectConfig mobileConnectConfig;

    @Autowired
    private OperatorUrls operatorUrls;
    private MobileConnectWebInterface mobileConnectWebInterface;
    private String clientName;
    private String apiVersion = mobileConnectConfig.getApiVersion();

    public MobileConnectController(@Autowired final MobileConnectWebInterface mobileConnectWebInterface)
    {
        this.mobileConnectWebInterface = mobileConnectWebInterface;
    }

    @GetMapping("start_discovery")
    @ResponseBody
    public MobileConnectWebResponse startDiscovery(
            @RequestParam(required = false) final String msisdn,
            @RequestParam(required = false) final String mcc,
            @RequestParam(required = false) final String mnc,
            @RequestParam(required = false) final String sourceIp,
            final HttpServletRequest request)
    {
        LOGGER.info("* Attempting discovery for msisdn={}, mcc={}, mnc={}",
                LogUtils.mask(msisdn, LOGGER, Level.INFO), sourceIp);

        MobileConnectRequestOptions requestOptions =
                new MobileConnectRequestOptions.Builder()
                        .withDiscoveryOptions(new DiscoveryOptions.Builder()
                                .withClientIp(sourceIp).build())
                        .build();
        final MobileConnectStatus status =
                this.mobileConnectWebInterface.attemptDiscovery(request, msisdn, mcc, mnc, true, mobileConnectConfig.getIncludeRequestIp(), requestOptions);

        return new MobileConnectWebResponse(status);
    }

    @GetMapping("start_manual_discovery")
    @ResponseBody
    public MobileConnectWebResponse startManualDiscovery (
            @RequestParam(required = false) final String subId,
            @RequestParam(required = false) final String clientName,
            final HttpServletRequest request
    ) throws JsonDeserializationException {

        String clientId = mobileConnectConfig.getClientId();
        String clientSecret = mobileConnectConfig.getClientSecret();

        LOGGER.info("* Attempting discovery for clientId={}, clientSecret={}, clientName={}",
                LogUtils.mask(clientId, LOGGER, Level.INFO), clientSecret, clientName);

        this.clientName = clientName;
        DiscoveryResponse discoveryResponse = this.mobileConnectWebInterface.generateDiscoveryManually(clientSecret,
                clientId, subId, clientName, operatorUrls);

        final MobileConnectStatus status = this.mobileConnectWebInterface.attemptManuallyDiscovery(discoveryResponse);

        return new MobileConnectWebResponse(status);
    }

    @GetMapping("start_manual_discovery_no_metadata")
    @ResponseBody
    public MobileConnectWebResponse startAuthenticationNoMetadata(
            @RequestParam(required = false) final String subId,
            final HttpServletRequest request) throws JsonDeserializationException {

        String clientId = mobileConnectConfig.getClientId();
        String clientSecret = mobileConnectConfig.getClientSecret();

        LOGGER.info("* Starting authentication for sdkSession={}, subscriberId={}, scope={}",
                LogUtils.mask(subId, LOGGER, Level.INFO), Scopes.MOBILECONNECT);
        OperatorUrls operatorUrlsWD = new OperatorUrls.Builder()
                .withAuthorizationUrl(operatorUrls.getAuthorizationUrl())
                .withRequestTokenUrl(operatorUrls.getRequestTokenUrl())
                .withUserInfoUrl(operatorUrls.getUserInfoUrl())
                .build();
        DiscoveryResponse discoveryResponse = this.mobileConnectWebInterface.generateDiscoveryManually(clientSecret,
                clientId, subId, "appName", operatorUrlsWD);

        final MobileConnectStatus status =
                this.mobileConnectWebInterface.attemptManuallyDiscovery(discoveryResponse);

        return new MobileConnectWebResponse(status);
    }

    @GetMapping("discovery_callback")
    @ResponseBody
    public MobileConnectWebResponse discoveryCallback(
            @RequestParam(required = false) final String sdkSession,
            @RequestParam(required = false, value = "mcc_mnc") final String mccMnc,
            @RequestParam(required = false) final String code,
            @RequestParam(required = false) final String expectedState,
            @RequestParam(required = false) final String expectedNonce,
            @RequestParam(required = false) final String subscriberId, final HttpServletRequest request)
    {
        LOGGER.info(
                "* Handling redirect for sdkSession={}, mccMnc={}, code={}, expectedState={}, expectedNonce={}, subscriberId={}",
                sdkSession, mccMnc, code, expectedState, expectedNonce,
                LogUtils.mask(subscriberId, LOGGER, Level.INFO));

        final URI requestUri = HttpUtils.extractCompleteUrl(request);
        final MobileConnectStatus status =
                this.mobileConnectWebInterface.handleUrlRedirect(request, requestUri, sdkSession,
                        expectedState, expectedNonce, null);

        return new MobileConnectWebResponse(status);
    }

    @GetMapping("endpoints")
    @ResponseBody
    public void endpoints (
            @RequestParam(required = false) final String authURL,
            @RequestParam(required = false) final String tokenURL,
            @RequestParam(required = false) final String userInfoURl,
            @RequestParam(required = false) final String metadata,
            @RequestParam(required = false) final URI discoveryURL,
            @RequestParam(required = false) final URI redirectURL
    ) {
        LOGGER.info("* Getting endpoints: authorizationUrl={}, tokenUrl={}, userInfoUrl={}, metadataUrl{}, discoveryUrl={}, redirectUrl={}",
                authURL, tokenURL, userInfoURl, metadata, discoveryURL, redirectURL);
        operatorUrls = new OperatorUrls.Builder()
                .withAuthorizationUrl(setValueToNullIfIsEmpty(authURL))
                .withRequestTokenUrl(setValueToNullIfIsEmpty(tokenURL))
                .withUserInfoUrl(setValueToNullIfIsEmpty(userInfoURl))
                .withProviderMetadataUri(setValueToNullIfIsEmpty(metadata))
                .build();

        MobileConnectConfig connectConfig = new MobileConnectConfig.Builder()
                .withDiscoveryUrl(setValueToNullIfIsEmpty(discoveryURL))
                .withRedirectUrl(setValueToNullIfIsEmpty(redirectURL))
                .build();
        this.mobileConnectWebInterface = MobileConnect.buildWebInterface(connectConfig, new DefaultEncodeDecoder());

    }

    @GetMapping({"start_authentication", "start_authorization"})
    @ResponseBody
    public MobileConnectWebResponse startAuthentication(
            @RequestParam(required = false) final String sdkSession,
            @RequestParam(required = false) final String subscriberId,
            @RequestParam(required = false) final String scope, final HttpServletRequest request)
    {
        LOGGER.info("* Starting authentication for sdkSession={}, subscriberId={}, scope={}",
                sdkSession, LogUtils.mask(subscriberId, LOGGER, Level.INFO), scope);

        if (scope == null && operatorUrls.getProviderMetadataUri() == null) {
            apiVersion = DefaultOptions.VERSION_MOBILECONNECTAUTHN;
        } else if (scope == null){
            apiVersion = DefaultOptions.VERSION_MOBILECONNECTAUTHZ;
        }

        final MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder()
                .withAuthenticationOptions(new AuthenticationOptions.Builder()
                        .withScope(scope)
                        .withContext(apiVersion.equals(DefaultOptions.VERSION_MOBILECONNECTAUTHZ) ? "demo" : null)
                        .withBindingMessage(apiVersion.equals(DefaultOptions.VERSION_MOBILECONNECTAUTHZ) ? "demo auth" : null)
                        .withClientName(clientName)
                        .build())
                .build();
        final MobileConnectStatus status =
                this.mobileConnectWebInterface.startAuthentication(request, sdkSession, subscriberId,
                        null, null, options);

        return new MobileConnectWebResponse(status);
    }

    @GetMapping("start_authentication_r1")
    @ResponseBody
    public MobileConnectWebResponse startAuthenticationR1(
            @RequestParam(required = false) final String sdkSession,
            @RequestParam(required = false) final String subscriberId,
            final HttpServletRequest request)
    {

        LOGGER.info("* Starting authentication for sdkSession={}, subscriberId={}, scope={}",
                sdkSession, LogUtils.mask(subscriberId, LOGGER, Level.INFO), Scopes.MOBILECONNECT);

        final MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder()
                .withAuthenticationOptions(new AuthenticationOptions.Builder()
                        .withScope(Scopes.MOBILECONNECT)
                        .build())
                .build();

        final MobileConnectStatus status =
                this.mobileConnectWebInterface.startAuthentication(request, sdkSession, subscriberId,
                        null, null, options);

        return new MobileConnectWebResponse(status);
    }

    @GetMapping("headless_authentication")
    @ResponseBody
    public MobileConnectWebResponse headlessAuthentication(
            @RequestParam(required = false) final String sdkSession,
            @RequestParam(required = false) final String subscriberId,
            @RequestParam(required = false) final String scope, final HttpServletRequest request)
    {
        LOGGER.info("* Starting authentication for sdkSession={}, subscriberId={}, scope={}",
                sdkSession, LogUtils.mask(subscriberId, LOGGER, Level.INFO), scope);

        final MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder()
                .withAuthenticationOptions(new AuthenticationOptions.Builder()
                        .withScope(scope)
                        .withContext(apiVersion.equals(DefaultOptions.VERSION_MOBILECONNECTAUTHZ) ? "headless" : null)
                        .withBindingMessage(apiVersion.equals(DefaultOptions.VERSION_MOBILECONNECTAUTHZ) ? "demo headless" : null)
                        .build())
                .witAutoRetrieveIdentitySet(true)
                .build();

        final MobileConnectStatus status =
                this.mobileConnectWebInterface.requestHeadlessAuthentication(request, sdkSession,
                        subscriberId, null, null, options);

        return new MobileConnectWebResponse(status);
    }

    @GetMapping("user_info")
    @ResponseBody
    public MobileConnectWebResponse requestUserInfo(
            @RequestParam(required = false) final String sdkSession,
            @RequestParam(required = false) final String accessToken, final HttpServletRequest request)
    {
        LOGGER.info("* Requesting user info for sdkSession={}, accessToken={}", sdkSession,
                LogUtils.mask(accessToken, LOGGER, Level.INFO));

        final MobileConnectStatus status =
                this.mobileConnectWebInterface.requestUserInfo(request, sdkSession, accessToken);

        return new MobileConnectWebResponse(status);
    }

    @GetMapping("identity")
    @ResponseBody
    public MobileConnectWebResponse requestIdentity(
            @RequestParam(required = false) final String sdkSession,
            @RequestParam(required = false) final String accessToken, final HttpServletRequest request)
    {
        LOGGER.info("* Requesting identity info for sdkSession={}, accessToken={}", sdkSession,
                LogUtils.mask(accessToken, LOGGER, Level.INFO));

        final MobileConnectStatus status =
                this.mobileConnectWebInterface.requestIdentity(request, sdkSession, accessToken);

        return new MobileConnectWebResponse(status);
    }

    @GetMapping("refresh_token")
    @ResponseBody
    public MobileConnectWebResponse refreshToken(
            @RequestParam(required = false) final String sdkSession,
            @RequestParam(required = false) final String refreshToken, final HttpServletRequest request)
    {
        LOGGER.info("* Calling refresh token for sdkSession={}, refreshToken={}", sdkSession,
                LogUtils.mask(refreshToken, LOGGER, Level.INFO));

        final MobileConnectStatus status =
                this.mobileConnectWebInterface.refreshToken(request, refreshToken, sdkSession);

        return new MobileConnectWebResponse(status);
    }

    @GetMapping("revoke_token")
    @ResponseBody
    public MobileConnectWebResponse revokeToken(
            @RequestParam(required = false) final String sdkSession,
            @RequestParam(required = false) final String accessToken, final HttpServletRequest request)
    {
        LOGGER.info("* Calling revoke token for sdkSession={}, accessToken={}", sdkSession,
                LogUtils.mask(accessToken, LOGGER, Level.INFO));

        final MobileConnectStatus status =
                this.mobileConnectWebInterface.revokeToken(request, accessToken,
                        Parameters.ACCESS_TOKEN_HINT, sdkSession);

        return new MobileConnectWebResponse(status);
    }

    private String setValueToNullIfIsEmpty (String value) {
        if (value == null || value.equals("")) {
            return null;
        }
        return value;
    }

    private URI setValueToNullIfIsEmpty (URI value) {
        if (value.toString().equals("")) {
            return null;
        }
        return value;
    }
}