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
package com.gsma.mobileconnect.r2.nodiscovery;

import com.gsma.mobileconnect.r2.MobileConnectRequestOptions;
import com.gsma.mobileconnect.r2.MobileConnectStatus;
import com.gsma.mobileconnect.r2.MobileConnectWebInterface;
import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.constants.Scopes;
import com.gsma.mobileconnect.r2.discovery.DiscoveryOptions;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.discovery.OperatorUrls;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
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
public class WithoutDiscoveryExampleAppController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(WithoutDiscoveryExampleAppController.class);

    private final MobileConnectWebInterface mobileConnectWebInterface;
    private final OperatorUrls operatorUrlsWithProviderMetadata;
    private final OperatorUrls operatorUrlsWithoutProviderMetadata;

    public WithoutDiscoveryExampleAppController(@Autowired final OperatorUrls operatorUrlsWithProviderMetadata, @Autowired final OperatorUrls operatorUrlsWithoutProviderMetadata, @Autowired final MobileConnectWebInterface mobileConnectWebInterface)
    {
        this.mobileConnectWebInterface = mobileConnectWebInterface;
        this.operatorUrlsWithProviderMetadata = operatorUrlsWithProviderMetadata;
        this.operatorUrlsWithoutProviderMetadata = operatorUrlsWithoutProviderMetadata;
    }
    //subId, clientId, clientName, clientSecret
    @GetMapping("start_manual_discovery")
    @ResponseBody
    public MobileConnectWebResponse startDiscovery(
        @RequestParam(required = false) final String subId,
        @RequestParam(required = false) final String clientId,
        @RequestParam(required = false) final String clientName,
        @RequestParam(required = false) final String clientSecret,
        final HttpServletRequest request) throws JsonDeserializationException {
        LOGGER.info("* Attempting discovery for clientId={}, clientSecret={}, clientName={}",
            LogUtils.mask(clientId, LOGGER, Level.INFO), clientSecret, clientName);

        DiscoveryResponse discoveryResponse = this.mobileConnectWebInterface.generateDiscoveryManually(clientSecret,
                clientId, subId, clientName, this.operatorUrlsWithProviderMetadata);

        final MobileConnectStatus status = this.mobileConnectWebInterface.attemptManuallyDiscovery(discoveryResponse);

        return new MobileConnectWebResponse(status);
    }

    @GetMapping("start_manual_discovery_no_metadata")
    @ResponseBody
    public MobileConnectWebResponse startDiscoveryNoMetadata(
            @RequestParam(required = false) final String subId,
            @RequestParam(required = false) final String clientId,
            @RequestParam(required = false) final String clientSecret,
            final HttpServletRequest request) throws JsonDeserializationException {
        LOGGER.info("* Attempting discovery for clientId={}, clientSecret={}",
                LogUtils.mask(clientId, LOGGER, Level.INFO), clientSecret);

        DiscoveryResponse discoveryResponse = this.mobileConnectWebInterface.generateDiscoveryManually(clientSecret,
                clientId, subId, "appName", this.operatorUrlsWithoutProviderMetadata);

        final MobileConnectStatus status = this.mobileConnectWebInterface.attemptManuallyDiscovery(discoveryResponse);

        return new MobileConnectWebResponse(status);
    }

    @GetMapping("start_authentication")
    @ResponseBody
    public MobileConnectWebResponse startAuthentication(
        @RequestParam(required = false) final String sdkSession,
        @RequestParam(required = false) final String subscriberId,
        final HttpServletRequest request)
    {

        LOGGER.info("* Starting authentication for sdkSession={}, subscriberId={}, scope={}",
            sdkSession, LogUtils.mask(subscriberId, LOGGER, Level.INFO), Scopes.MOBILECONNECT);

        final MobileConnectRequestOptions options = new MobileConnectRequestOptions.Builder()
            .withAuthenticationOptions(new AuthenticationOptions.Builder()
                .withScope(Scopes.MOBILECONNECTAUTHORIZATION)
                .withContext("demo")
                .withBindingMessage("demo auth")
                .build())
            .withDiscoveryOptions(new DiscoveryOptions.Builder()
                .withXRedirect("APP")
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
