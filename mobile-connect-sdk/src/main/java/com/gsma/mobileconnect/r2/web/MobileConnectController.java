package com.gsma.mobileconnect.r2.web;

import com.gsma.mobileconnect.r2.*;
import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.cache.AbstractCacheable;
import com.gsma.mobileconnect.r2.cache.CacheAccessException;
import com.gsma.mobileconnect.r2.cache.ConcurrentCache;
import com.gsma.mobileconnect.r2.cache.ICache;
import com.gsma.mobileconnect.r2.constants.DefaultOptions;
import com.gsma.mobileconnect.r2.constants.Parameters;
import com.gsma.mobileconnect.r2.discovery.DiscoveryOptions;
import com.gsma.mobileconnect.r2.encoding.DefaultEncodeDecoder;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.utils.*;
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
import java.util.concurrent.TimeUnit;


@Controller
@EnableAutoConfiguration
@RequestMapping(path = "server_side_api", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class MobileConnectController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MobileConnectController.class);

    @Autowired
    private MobileConnectConfig mobileConnectConfig;
    private MobileConnectWebInterface mobileConnectWebInterface;
    private String clientName = "demoApp";
    private ICache cache;
    private IJsonService jsonService = new JacksonJsonService();

    public MobileConnectController(@Autowired final MobileConnectWebInterface mobileConnectWebInterface)
    {
        this.mobileConnectWebInterface = mobileConnectWebInterface;
        ListUtils.HashMapBuilder<Class<? extends AbstractCacheable>, Tuple<Long, Long>>
                cacheLimitsMapBuilder = new ListUtils.HashMapBuilder<Class<? extends AbstractCacheable>, Tuple<Long, Long>>();
        cacheLimitsMapBuilder.add(CacheableStringItem.class, new Tuple<>(TimeUnit.SECONDS.toSeconds(200L), TimeUnit.SECONDS.toSeconds(600L)));
        this.cache = new ConcurrentCache.Builder()
                .withJsonService(this.jsonService)
                .withCacheExpiryLimits(cacheLimitsMapBuilder.build())
                .build();
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

    @GetMapping("discovery_callback")
    @ResponseBody
    public MobileConnectWebResponse discoveryCallback(
            @RequestParam(required = false) String sdkSession,
            @RequestParam(required = false, value = "mcc_mnc") final String mccMnc,
            @RequestParam(required = false) final String code,
            @RequestParam(required = false) final String state,
            @RequestParam(required = false) String nonce,
            @RequestParam(required = false) final String subscriber_id, final HttpServletRequest request)
    {
        LOGGER.info( "* Handling redirect for sdkSession={}, mccMnc={}, code={}, state={}, nonce={}, subscriberId={}",
                sdkSession, mccMnc, code, state, nonce,
                LogUtils.mask(subscriber_id, LOGGER, Level.INFO));

        try {
            CacheableStringItem item = this.cache.get(state, CacheableStringItem.class, true);
            if(item != null) {
                sdkSession = (sdkSession == null) ? item.getSdkSession() : sdkSession;
                nonce = (nonce == null) ? item.getNonce() : nonce;
            }
        }
        catch (CacheAccessException e) {
            LOGGER.info("Error while extracting sdkSession from cache:" + e.getMessage());
        }

        final URI requestUri = HttpUtils.extractCompleteUrl(request);
        final MobileConnectStatus status =
                this.mobileConnectWebInterface.handleUrlRedirect(request, requestUri, sdkSession, state, nonce, null);

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

        String apiVersion = this.mobileConnectConfig.getApiVersion();
        final MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder()
                .withAuthenticationOptions(new AuthenticationOptions.Builder()
                        .withScope(scope)
                        .withContext(apiVersion.equals(DefaultOptions.VERSION_MOBILECONNECTAUTHZ) ? "demo" : null)
                        .withBindingMessage(apiVersion.equals(DefaultOptions.VERSION_MOBILECONNECTAUTHZ) ? "demo auth" : null)
                        .withClientName(clientName)
                        .build())
                .build();

        final MobileConnectStatus status =
                this.mobileConnectWebInterface.startAuthentication(request, sdkSession, subscriberId, null, null, options);

        MobileConnectWebResponse authResponse = new MobileConnectWebResponse(status);

        try {
            if(authResponse.getState() != null) {
                this.cache.add(authResponse.getState(), new CacheableStringItem(sdkSession, authResponse.getNonce()));
            }
        } catch (CacheAccessException e) {
            LOGGER.info("Error during adding values to cache: " + e.getMessage());
        }

        return authResponse;
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

        String apiVersion = this.mobileConnectConfig.getApiVersion();

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

    private URI setValueToNullIfIsEmpty (URI value) {
        if (value.toString().equals("")) {
            return null;
        }
        return value;
    }
}