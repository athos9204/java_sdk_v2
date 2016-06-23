/*
 *                                   SOFTWARE USE PERMISSION
 *
 *  By downloading and accessing this software and associated documentation files ("Software") you are granted the
 *  unrestricted right to deal in the Software, including, without limitation the right to use, copy, modify, publish,
 *  sublicense and grant such rights to third parties, subject to the following conditions:
 *
 *  The following copyright notice and this permission notice shall be included in all copies, modifications or
 *  substantial portions of this Software: Copyright © 2016 GSM Association.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS," WITHOUT WARRANTY OF ANY KIND, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 *  ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. YOU
 *  AGREE TO INDEMNIFY AND HOLD HARMLESS THE AUTHORS AND COPYRIGHT HOLDERS FROM AND AGAINST ANY SUCH LIABILITY.
 */

package com.gsma.mobileconnect.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsma.mobileconnect.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.oidc.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Date;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

public class StartAuthenticationTest
{
    static private final String AUTHORIZATION_HREF = "http://operator_a.sandbox.mobileconnect.io/oidc/authorize";
    static private final String TOKEN_HREF = "http://operator_a.sandbox.mobileconnect.io/oidc/accesstoken";
    static private final String OPERATOR_JSON_STRING =
            "{\n" +
                    "   \"ttl\":1452186985151,\n" +
                    "   \"response\":{\n" +
                    "      \"serving_operator\":\"Example Operator A\",\n" +
                    "      \"country\":\"US\",\n" +
                    "      \"currency\":\"USD\",\n" +
                    "      \"apis\":{\n" +
                    "         \"operatorid\":{\n" +
                    "            \"link\":[{\n" +
                    "               \"href\":\"" + AUTHORIZATION_HREF + "\",\n" +
                    "               \"rel\":\"authorization\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "               \"href\":\"" + TOKEN_HREF + "\",\n" +
                    "               \"rel\":\"token\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "               \"href\":\"http://operator_a.sandbox.mobileconnect.io/oidc/userinfo\",\n" +
                    "               \"rel\":\"userinfo\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "               \"href\":\"openid profile email\",\n" +
                    "               \"rel\":\"scope\"\n" +
                    "            }]\n" +
                    "         }\n" +
                    "      },\n" +
                    "      \"client_id\":\"0c9df219\",\n" +
                    "      \"client_secret\":\"097097705fa43135c7a6bbcaede4f041\"\n" +
                    "   }\n" +
                    "}";

    static private final String OPERATOR_NO_AUTHORIZATION_JSON_STRING =
            "{\n" +
                    "   \"ttl\":1452186985151,\n" +
                    "   \"response\":{\n" +
                    "      \"serving_operator\":\"Example Operator A\",\n" +
                    "      \"country\":\"US\",\n" +
                    "      \"currency\":\"USD\",\n" +
                    "      \"apis\":{\n" +
                    "         \"operatorid\":{\n" +
                    "            \"link\":[{\n" +
                    "               \"href\":\"" + TOKEN_HREF + "\",\n" +
                    "               \"rel\":\"token\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "               \"href\":\"http://operator_a.sandbox.mobileconnect.io/oidc/userinfo\",\n" +
                    "               \"rel\":\"userinfo\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "               \"href\":\"openid profile email\",\n" +
                    "               \"rel\":\"scope\"\n" +
                    "            }]\n" +
                    "         }\n" +
                    "      },\n" +
                    "      \"client_id\":\"0c9df219\",\n" +
                    "      \"client_secret\":\"097097705fa43135c7a6bbcaede4f041\"\n" +
                    "   }\n" +
                    "}";
    @Rule
    final public ExpectedException thrown = ExpectedException.none();

    @Test
    public void startAuthentication_withMissingDiscoveryResponse_shouldThrowException() throws OIDCException, DiscoveryResponseExpiredException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        CaptureStartAuthenticationResponse captureStartAuthenticationResponse = new CaptureStartAuthenticationResponse();

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("discoveryResult"));

        // WHEN
        oidc.startAuthentication(null, "", null, "", null, null, null, null, null, captureStartAuthenticationResponse);
    }

    @Test
    public void startAuthentication_withMissingRedirectURI_shouldThrowException() throws OIDCException, DiscoveryResponseExpiredException, IOException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        CaptureStartAuthenticationResponse captureStartAuthenticationResponse = new CaptureStartAuthenticationResponse();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, parseJson(OPERATOR_JSON_STRING));

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("redirectURI"));

        // WHEN
        oidc.startAuthentication(discoveryResponse, null, null, "", null, null, null, null, null, captureStartAuthenticationResponse);
    }

    @Test
    public void startAuthentication_withMissingNonce_shouldThrowException() throws OIDCException, DiscoveryResponseExpiredException, IOException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        CaptureStartAuthenticationResponse captureStartAuthenticationResponse = new CaptureStartAuthenticationResponse();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, parseJson(OPERATOR_JSON_STRING));

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("nonce"));

        // WHEN
        oidc.startAuthentication(discoveryResponse, "", null, null, null, null, null, null, null, captureStartAuthenticationResponse);
    }

    @Test
    public void startAuthentication_withMissingCallback_shouldThrowException() throws OIDCException, DiscoveryResponseExpiredException, IOException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, parseJson(OPERATOR_JSON_STRING));

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("callback"));

        // WHEN
        oidc.startAuthentication(discoveryResponse, "", null, "", null, null, null, null, null, null);
    }

    @Test
    public void startAuthentication_withExpiredDiscoveryResponse_shouldThrowException() throws OIDCException, DiscoveryResponseExpiredException, IOException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        CaptureStartAuthenticationResponse captureStartAuthenticationResponse = new CaptureStartAuthenticationResponse();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MIN_VALUE), 0, null, parseJson(OPERATOR_JSON_STRING));

        // THEN
        thrown.expect(DiscoveryResponseExpiredException.class);
        thrown.expectMessage(containsString("discoveryResult has expired"));

        // WHEN
        oidc.startAuthentication(discoveryResponse, "", null, "", null, null, null, null, null, captureStartAuthenticationResponse);
    }

    @Test
    public void startAuthentication_withInvalidDiscoveryResponse_shouldThrowException() throws OIDCException, DiscoveryResponseExpiredException, IOException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        CaptureStartAuthenticationResponse captureStartAuthenticationResponse = new CaptureStartAuthenticationResponse();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, parseJson("{}"));

        // THEN
        thrown.expect(OIDCException.class);
        thrown.expectMessage(containsString("Not a valid discovery result"));

        // WHEN
        oidc.startAuthentication(discoveryResponse, "", null, "", null, null, null, null, null, captureStartAuthenticationResponse);
    }

    @Test
    public void startAuthentication_withMissingAuthorizationEndPoint_shouldThrowException() throws OIDCException, DiscoveryResponseExpiredException, IOException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        CaptureStartAuthenticationResponse captureStartAuthenticationResponse = new CaptureStartAuthenticationResponse();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, parseJson(OPERATOR_NO_AUTHORIZATION_JSON_STRING));

        // THEN
        thrown.expect(OIDCException.class);
        thrown.expectMessage(containsString("No authorization href"));

        // WHEN
        oidc.startAuthentication(discoveryResponse, "", null, "", null, null, null, null, null, captureStartAuthenticationResponse);
    }

    @Test
    public void startAuthentication_withDefaults_shouldGenerateExpectedURL() throws OIDCException, DiscoveryResponseExpiredException, IOException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        CaptureStartAuthenticationResponse captureStartAuthenticationResponse = new CaptureStartAuthenticationResponse();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, parseJson(OPERATOR_JSON_STRING));

        // WHEN
        oidc.startAuthentication(discoveryResponse, "http://localhost:8080/mobile_connect", null, "expected-nonce", null, null, null, null, null, captureStartAuthenticationResponse);

        // THEN
        StartAuthenticationResponse startAuthenticationResponse = captureStartAuthenticationResponse.getStartAuthenticationResponse();

        String expectedURL = "http://operator_a.sandbox.mobileconnect.io/oidc/authorize?client_id=0c9df219&response_type=code&scope=openid&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fmobile_connect&acr_values=2&nonce=expected-nonce&display=page&max-age=3600";

        assertEquals(expectedURL, startAuthenticationResponse.getUrl());
        assertEquals("overlay", startAuthenticationResponse.getScreenMode());
    }

    @Test
    public void startAuthentication_withAllValuesSet_shouldGenerateExpectedURL() throws OIDCException, DiscoveryResponseExpiredException, IOException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        CaptureStartAuthenticationResponse captureStartAuthenticationResponse = new CaptureStartAuthenticationResponse();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, parseJson(OPERATOR_JSON_STRING));
        AuthenticationOptions options = new AuthenticationOptions();

        options.setDisplay("page");
        options.setScreenMode("expected-screen-mode");
        options.setClaimsLocales("en");
        options.setDtbs("expected-dtbs");
        options.setIdTokenHint("expected-id-token-hint");
        options.setLoginHint("ENCR_MSISDN:expected-login-hint");
        options.setPrompt("login");
        options.setUiLocales("en");

        // WHEN
        oidc.startAuthentication(discoveryResponse, "https://developer.mobileconnect.io/done/", "expected-state", "expected-nonce", "openid profile", 333, "3", "expected-encrypted-msisdn", options, captureStartAuthenticationResponse);

        // THEN
        StartAuthenticationResponse startAuthenticationResponse = captureStartAuthenticationResponse.getStartAuthenticationResponse();

        String expectedURL = "http://operator_a.sandbox.mobileconnect.io/oidc/authorize?client_id=0c9df219&response_type=code&scope=openid+profile&redirect_uri=https%3A%2F%2Fdeveloper.mobileconnect.io%2Fdone%2F&acr_values=3&state=expected-state&nonce=expected-nonce&display=page&prompt=login&max-age=333&ui-locales=en&claims_locales=en&id_token_hint=expected-id-token-hint&login_hint=ENCR_MSISDN%3Aexpected-login-hint&dtbs=expected-dtbs";

        assertEquals(expectedURL, startAuthenticationResponse.getUrl());
        assertEquals("expected-screen-mode", startAuthenticationResponse.getScreenMode());
    }

    @Test
    public void startAuthentication_withEncryptedMSISDN1_shouldGenerateExpectedURL() throws OIDCException, DiscoveryResponseExpiredException, IOException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        CaptureStartAuthenticationResponse captureStartAuthenticationResponse = new CaptureStartAuthenticationResponse();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, parseJson(OPERATOR_JSON_STRING));
        AuthenticationOptions options = new AuthenticationOptions();

        // WHEN
        oidc.startAuthentication(discoveryResponse, "https://developer.mobileconnect.io/done/", null, "expected-nonce", null, null, null, "expected-encrypted-msisdn", options, captureStartAuthenticationResponse);

        // THEN
        StartAuthenticationResponse startAuthenticationResponse = captureStartAuthenticationResponse.getStartAuthenticationResponse();

        String expectedURL = "http://operator_a.sandbox.mobileconnect.io/oidc/authorize?client_id=0c9df219&response_type=code&scope=openid&redirect_uri=https%3A%2F%2Fdeveloper.mobileconnect.io%2Fdone%2F&acr_values=2&nonce=expected-nonce&display=page&max-age=3600&login_hint=ENCR_MSISDN%3Aexpected-encrypted-msisdn";

        assertEquals(expectedURL, startAuthenticationResponse.getUrl());
        assertEquals("overlay", startAuthenticationResponse.getScreenMode());
    }

    @Test
    public void startAuthentication_withEncryptedMSISDN2_shouldGenerateExpectedURL() throws OIDCException, DiscoveryResponseExpiredException, IOException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        CaptureStartAuthenticationResponse captureStartAuthenticationResponse = new CaptureStartAuthenticationResponse();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, parseJson(OPERATOR_JSON_STRING));

        // WHEN
        oidc.startAuthentication(discoveryResponse, "https://developer.mobileconnect.io/done/", null, "expected-nonce", null, null, null, "expected-encrypted-msisdn", null, captureStartAuthenticationResponse);

        // THEN
        StartAuthenticationResponse startAuthenticationResponse = captureStartAuthenticationResponse.getStartAuthenticationResponse();

        String expectedURL = "http://operator_a.sandbox.mobileconnect.io/oidc/authorize?client_id=0c9df219&response_type=code&scope=openid&redirect_uri=https%3A%2F%2Fdeveloper.mobileconnect.io%2Fdone%2F&acr_values=2&nonce=expected-nonce&display=page&max-age=3600&login_hint=ENCR_MSISDN%3Aexpected-encrypted-msisdn";

        assertEquals(expectedURL, startAuthenticationResponse.getUrl());
        assertEquals("overlay", startAuthenticationResponse.getScreenMode());
    }

    private static JsonNode parseJson(String jsonStr)
            throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(jsonStr);
        if (null == root)
        {
            throw new IOException("Invalid json");
        }
        return root;
    }
}
