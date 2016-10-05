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
package com.gsma.mobileconnect.r2.demo;

import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.MobileConnectWebInterface;
import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.constants.Parameters;
import com.gsma.mobileconnect.r2.utils.HttpUtils;
import com.gsma.mobileconnect.r2.utils.LogUtils;
import com.gsma.mobileconnect.r2.web.MobileConnectWebResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

/**
 * @since 2.0
 */
@Controller
@EnableAutoConfiguration
@RequestMapping(path = "api/mobileconnect", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class DemoAppController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoAppController.class);

    private final MobileConnectWebInterface mobileConnectWebInterface;

    public DemoAppController(@Autowired final MobileConnectWebInterface mobileConnectWebInterface)
    {
        this.mobileConnectWebInterface = mobileConnectWebInterface;
    }

    @GetMapping("start_discovery")
    @ResponseBody
    public MobileConnectWebResponse startDiscovery(
        @RequestParam(required = false) final String msisdn,
        @RequestParam(required = false) final String mcc,
        @RequestParam(required = false) final String mnc, final HttpServletRequest request)
    {
        LOGGER.info("* Attempting discovery for msisdn={}, mcc={}, mnc={}",
            LogUtils.mask(msisdn, LOGGER, Level.INFO), mcc, mnc);

        final MobileConnectStatus status =
            this.mobileConnectWebInterface.attemptDiscovery(request, msisdn, mcc, mnc, true, null);

        return new MobileConnectWebResponse(status);
    }

    @GetMapping("start_authentication")
    @ResponseBody
    public MobileConnectWebResponse startAuthentication(
        @RequestParam(required = false) final String sdkSession,
        @RequestParam(required = false) final String subscriberId,
        @RequestParam(required = false) final String scope, final HttpServletRequest request)
    {
        LOGGER.info("* Starting authentication for sdkSession={}, subscriberId={}, scope={}",
            sdkSession, LogUtils.mask(subscriberId, LOGGER, Level.INFO), scope);

        final MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder()
            .withAuthenticationOptions(new AuthenticationOptions.Builder()
                .withScope(scope)
                .withContext("demo")
                .withBindingMessage("demo auth")
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
                .withContext("headless")
                .withBindingMessage("demo headless")
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
            this.mobileConnectWebInterface.requestUserInfo(request, sdkSession, accessToken, null);

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
            this.mobileConnectWebInterface.requestIdentity(request, sdkSession, accessToken, null);

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

    @GetMapping("")
    @ResponseBody
    public MobileConnectWebResponse handleRedirect(
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
}
