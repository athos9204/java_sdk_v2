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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gsma.mobileconnect.cache.DiscoveryCacheKey;
import com.gsma.mobileconnect.cache.DiscoveryCacheValue;
import com.gsma.mobileconnect.cache.IDiscoveryCache;
import com.gsma.mobileconnect.discovery.DiscoveryException;
import com.gsma.mobileconnect.discovery.DiscoveryOptions;
import com.gsma.mobileconnect.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.discovery.IDiscovery;
import com.gsma.mobileconnect.utils.KeyValuePair;
import com.gsma.mobileconnect.utils.RestClient;
import com.gsma.mobileconnect.utils.RestException;
import com.gsma.mobileconnect.utils.RestResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class StartAutomatedOperatorDiscoveryTest
{
    private static final String IDENTIFIED_MCC = "MCC";
    private static final String IDENTIFIED_MNC = "MNC";

    @Rule
    final public ExpectedException thrown = ExpectedException.none();

    @Test
    public void startAutomatedOperatorDiscovery_withMissingClientId_shouldThrowException() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null);
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();
        Config config = new Config();

        //THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("clientId"));

        // WHEN
        discovery.startAutomatedOperatorDiscovery(
                null,
                config.getClientSecret(),
                config.getDiscoveryURL(),
                Config.REDIRECT_URL,
                null,
                null,
                captureDiscoveryResponse);
    }

    @Test
    public void startAutomatedOperatorDiscovery_withMissingClientSecret_shouldThrowException() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null);
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();
        Config config = new Config();

        //THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("clientSecret"));

        // WHEN
        discovery.startAutomatedOperatorDiscovery(
                config.getClientId(),
                null,
                config.getDiscoveryURL(),
                Config.REDIRECT_URL,
                null,
                null,
                captureDiscoveryResponse);
    }

    @Test
    public void startAutomatedOperatorDiscovery_withMissingDiscoveryURL_shouldThrowException() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null);
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();
        Config config = new Config();

        //THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("discoveryURL"));

        // WHEN
        discovery.startAutomatedOperatorDiscovery(
                config.getClientId(),
                config.getClientSecret(),
                null,
                Config.REDIRECT_URL,
                null,
                null,
                captureDiscoveryResponse);
    }

    @Test
    public void startAutomatedOperatorDiscovery_withMissingRedirectURL_shouldThrowException() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null);
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();
        Config config = new Config();

        //THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("redirectURL"));

        // WHEN
        discovery.startAutomatedOperatorDiscovery(
                config.getClientId(),
                config.getClientSecret(),
                config.getDiscoveryURL(),
                null,
                null,
                null,
                captureDiscoveryResponse);
    }

    @Test
    public void startAutomatedOperatorDiscovery_withMissingCallback_shouldThrowException() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null);
        Config config = new Config();

        //THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("callback"));

        // WHEN
        discovery.startAutomatedOperatorDiscovery(
                config,
                Config.REDIRECT_URL,
                null,
                null,
                null);
    }

    @Test
    public void startAutomatedOperatorDiscovery_withMissingPreferences_shouldThrowException() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null);
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();

        //THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("preferences"));

        // WHEN
        discovery.startAutomatedOperatorDiscovery(
                null,
                Config.REDIRECT_URL,
                null,
                null,
                captureDiscoveryResponse);
    }

    @Test
    public void startAutomatedOperatorDiscovery_withInvalidDiscoveryURL_shouldThrowException() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null);
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();
        Config config = new Config();

        //THEN
        thrown.expect(DiscoveryException.class);
        thrown.expectMessage(containsString("Failed to build discovery URI"));

        // WHEN
        discovery.startAutomatedOperatorDiscovery(
                config.getClientId(),
                config.getClientSecret(),
                "<invalid uri>",
                Config.REDIRECT_URL,
                null,
                null,
                captureDiscoveryResponse);
    }

    @Test
    public void startAutomatedOperatorDiscovery_withCookies_shouldFilterMobileConnectCookies()
        throws DiscoveryException, RestException, IOException
    {
        // GIVEN
        RestClient mockedRestClient = mock(RestClient.class);
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();
        List<KeyValuePair> allCookies = new ArrayList<KeyValuePair>();
        List<KeyValuePair> expectedCookies = new ArrayList<KeyValuePair>();
        Config config = new Config();

        KeyValuePair ignored1 = new KeyValuePair("ignored1", "should be ignored");
        KeyValuePair ignored2 = new KeyValuePair("ignored2", "should be ignored");

        KeyValuePair valid1 = new KeyValuePair( "Most-Recent-Selected-Operator-Expiry", "valid");
        KeyValuePair valid2 = new KeyValuePair( "Most-Recent-Selected-Operator", "valid");
        KeyValuePair valid3 = new KeyValuePair( "Enum-Nonce", "valid");

        allCookies.add(ignored1);
        allCookies.add(valid1);
        allCookies.add(valid2);
        allCookies.add(valid3);
        allCookies.add(ignored2);

        expectedCookies.add(valid1);
        expectedCookies.add(valid2);
        expectedCookies.add(valid3);

        String exampleJson = buildJsonStr();
        JsonNode expectedJsonObject = buildJsonObject();
        final RestResponse restResponse = new RestResponse(null, 0, null, exampleJson);

        final List<KeyValuePair> capturedCookies = new ArrayList<KeyValuePair>();

        doAnswer(new Answer()
        {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable
            {
                List<KeyValuePair> cookies = invocationOnMock.getArgumentAt(3, List.class);
                capturedCookies.addAll(cookies);
                return restResponse;
            }
        }).when(mockedRestClient).callRestEndPoint(any(HttpRequestBase.class), any(HttpClientContext.class), anyInt(), Matchers.<List<KeyValuePair>>any());

        IDiscovery discovery = Factory.getDiscovery(null, mockedRestClient);

        // WHEN
        discovery.startAutomatedOperatorDiscovery(
                config,
                Config.REDIRECT_URL,
                null,
                allCookies,
                captureDiscoveryResponse);

        // THEN
        DiscoveryResponse discoveryResponse = captureDiscoveryResponse.getDiscoveryResponse();
        assertEquals(expectedJsonObject, discoveryResponse.getResponseData());
        assertEquals(expectedCookies, capturedCookies);
    }

    @Test
    public void startAutomatedOperatorDiscovery_withAllOptionsSet_shouldCreateExpectedRequest()
            throws DiscoveryException, RestException, IOException
    {
        // GIVEN
        RestClient mockedRestClient = mock(RestClient.class);
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();
        Config config = new Config();

        String exampleJson = buildJsonStr();
        JsonNode expectedJsonObject = buildJsonObject();
        final RestResponse restResponse = new RestResponse(null, 0, null, exampleJson);

        DiscoveryOptions options = new DiscoveryOptions();
        options.setUsingMobileData(false);
        options.setManuallySelect(true);
        options.setLocalClientIP("127.0.0.1");
        options.setIdentifiedMCC("901");
        options.setIdentifiedMNC("01");
        options.setTimeout(333);
        String expectedClientIP = "10.0.0.1";
        options.setClientIP(expectedClientIP);
        String expectedAccept = "application/json";

        String expectedURL = "http://discovery.sandbox.mobileconnect.io/v2/discovery?Manually-Select=true&Identified-MCC=901&Identified-MNC=01&Using-Mobile-Data=false&Local-Client-IP=127.0.0.1&Redirect_URL=http%3A%2F%2Flocalhost%3A8080%2Fmobileconnect";

        final CaptureHttpRequestBase captureHttpRequestBase = new CaptureHttpRequestBase();

        doAnswer(new Answer()
        {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable
            {
                Object[] args = invocationOnMock.getArguments();
                HttpRequestBase requestBase = (HttpRequestBase)args[0];
                captureHttpRequestBase.setValue(requestBase);
                return restResponse;
            }
        }).when(mockedRestClient).callRestEndPoint(any(HttpRequestBase.class), any(HttpClientContext.class), eq(options.getTimeout()), Matchers.<List<KeyValuePair>>any());

        IDiscovery discovery = Factory.getDiscovery(null, mockedRestClient);

        // WHEN
        discovery.startAutomatedOperatorDiscovery(
                config,
                Config.REDIRECT_URL,
                options,
                null,
                captureDiscoveryResponse);

        // THEN
        DiscoveryResponse discoveryResponse = captureDiscoveryResponse.getDiscoveryResponse();
        assertEquals(expectedJsonObject, discoveryResponse.getResponseData());
        HttpRequestBase capturedRequest = captureHttpRequestBase.getValue();
        assertEquals(expectedURL, capturedRequest.getURI().toString());
        assertEquals(expectedClientIP, capturedRequest.getFirstHeader("X-Source-IP").getValue());
        assertEquals(expectedAccept, capturedRequest.getFirstHeader("accept").getValue());
    }

    @Test
    public void startAutomatedOperatorDiscovery_withDetailsInCache_shouldReturnCachedValue()
            throws DiscoveryException
    {
        // GIVEN
        JsonNode expectedJsonObject = buildJsonObject();
        DiscoveryCacheValue value = new DiscoveryCacheValue(new Date(Long.MAX_VALUE), expectedJsonObject);
        DiscoveryCacheKey key = DiscoveryCacheKey.newWithDetails(IDENTIFIED_MCC, IDENTIFIED_MNC);

        IDiscoveryCache cache = Factory.getDefaultDiscoveryCache();
        cache.add(key, value);

        IDiscovery discovery = Factory.getDiscovery(cache, null);

        DiscoveryOptions options = buildDiscoveryOptions();

        Config config = new Config();
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();

        // WHEN
        discovery.startAutomatedOperatorDiscovery(config, "", options, null, captureDiscoveryResponse);

        // THEN
        DiscoveryResponse discoveryResponse = captureDiscoveryResponse.getDiscoveryResponse();

        assertTrue(discoveryResponse.isCached());
        assertEquals(0,discoveryResponse.getResponseCode());
        assertNull(discoveryResponse.getHeaders());
        assertEquals(expectedJsonObject, discoveryResponse.getResponseData());
    }

    @Test
    public void startAutomatedOperatorDiscovery_whenOperatorIdentified_shouldCacheValue()
            throws DiscoveryException, IOException, RestException
    {
        // GIVEN
        IDiscoveryCache cache = Factory.getDefaultDiscoveryCache();

        RestClient mockedRestClient = mock(RestClient.class);
        JsonNode expectedJsonObject = buildJsonObject();
        RestResponse restResponse = new RestResponse("", 200, buildHeaders(), buildJsonStr() );

        when(mockedRestClient.callRestEndPoint(any(HttpRequestBase.class), any(HttpClientContext.class), anyInt(), Matchers.<List<KeyValuePair>>any())).thenReturn(restResponse);

        IDiscovery discovery = Factory.getDiscovery(cache, mockedRestClient);

        Config config = new Config();
        DiscoveryOptions options = buildDiscoveryOptions();
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();

        // WHEN
        discovery.startAutomatedOperatorDiscovery(config, "", options, null, captureDiscoveryResponse);

        // THEN
        DiscoveryCacheKey key = DiscoveryCacheKey.newWithDetails(IDENTIFIED_MCC, IDENTIFIED_MNC);
        DiscoveryCacheValue value = cache.get(key);

        assertNotNull(value);
        assertEquals(expectedJsonObject, value.getValue());
    }

    @Test
    public void startAutomatedOperatorDiscovery_whenRestExceptionThrown_discoveryExceptionContainsExpectedValue()
            throws IOException, RestException
    {
        // GIVEN
        RestClient mockedRestClient = mock(RestClient.class);
        RestResponse restResponse = new RestResponse("expected-uri", 404, buildHeaders(), "expected-contents");
        RestException restException = new RestException("TEST", restResponse);
        when(mockedRestClient.callRestEndPoint(any(HttpRequestBase.class), any(HttpClientContext.class), anyInt(), Matchers.<List<KeyValuePair>>any())).thenThrow(restException);

        IDiscovery discovery = Factory.getDiscovery(null, mockedRestClient);

        Config config = new Config();
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();

        // WHEN
        DiscoveryException capturedException = null;
        try
        {
            discovery.startAutomatedOperatorDiscovery(config, "", null, null, captureDiscoveryResponse);
            assertFalse(true); // exception expected
        }
        catch (DiscoveryException ex)
        {
            capturedException = ex;
        }

        // THEN
        assertNotNull(capturedException);
        assertEquals(restResponse.getUri(), capturedException.getUri());
        assertEquals(restResponse.getStatusCode(), capturedException.getResponseCode());
        assertEquals(restResponse.getHeaders(), capturedException.getHeaders());
        assertEquals(restResponse.getResponse(), capturedException.getContents());
    }

    private DiscoveryOptions buildDiscoveryOptions()
    {
        DiscoveryOptions options = new DiscoveryOptions();
        options.setIdentifiedMCC(IDENTIFIED_MCC);
        options.setIdentifiedMNC(IDENTIFIED_MNC);
        return options;
    }

    private JsonNode buildJsonObject()
    {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        root.put("field", "field-value");
        return root;
    }

    private String buildJsonStr()
            throws JsonProcessingException
    {
        JsonNode root = buildJsonObject();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(root);
    }

    private List<KeyValuePair> buildHeaders()
    {
        List<KeyValuePair> headers = new ArrayList<KeyValuePair>(1);
        headers.add(new KeyValuePair("Content-Type", "application/json"));
        return headers;
    }
}
