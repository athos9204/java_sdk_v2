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
import com.gsma.mobileconnect.discovery.CompleteSelectedOperatorDiscoveryOptions;
import com.gsma.mobileconnect.discovery.DiscoveryException;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class CompleteSelectedOperatorDiscoveryTest
{
    private static final String SELECTED_MCC = "MCC";
    private static final String SELECTED_MNC = "MNC";

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void completeSelectedOperator_withMissingClientId_shouldThrowException() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null, null);
        Config config = new Config();
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("clientId"));

        // WHEN
        discovery.completeSelectedOperatorDiscovery(
                null,
                config.getClientSecret(),
                config.getDiscoveryURL(),
                null,
                "",
                "",
                null,
                null,
                captureDiscoveryResponse);
    }

    @Test
    public void completeSelectedOperator_withMissingClientSecret_shouldThrowException() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null, null);
        Config config = new Config();
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("clientSecret"));

        // WHEN
        discovery.completeSelectedOperatorDiscovery(
                config.getClientId(),
                null,
                config.getDiscoveryURL(),
                null,
                "",
                "",
                null,
                null,
                captureDiscoveryResponse);
    }

    @Test
    public void completeSelectedOperator_withMissingDiscoveryURL_shouldThrowException() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null, null);
        Config config = new Config();
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("discoveryURL"));

        // WHEN
        discovery.completeSelectedOperatorDiscovery(
                config.getClientId(),
                config.getClientSecret(),
                null,
                null,
                "",
                "",
                null,
                null,
                captureDiscoveryResponse);
    }

    @Test
    public void completeSelectedOperator_withMissingSelectedMCC_shouldThrowException() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null, null);
        Config config = new Config();
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("selectedMCC"));

        // WHEN
        discovery.completeSelectedOperatorDiscovery(
                config.getClientId(),
                config.getClientSecret(),
                config.getDiscoveryURL(),
                null,
                null,
                "",
                null,
                null,
                captureDiscoveryResponse);
    }

    @Test
    public void completeSelectedOperator_withMissingSelectedMNC_shouldThrowException() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null, null);
        Config config = new Config();
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("selectedMNC"));

        // WHEN
        discovery.completeSelectedOperatorDiscovery(
                config.getClientId(),
                config.getClientSecret(),
                config.getDiscoveryURL(),
                null,
                "",
                null,
                null,
                null,
                captureDiscoveryResponse);
    }

    @Test
    public void completeSelectedOperator_withMissingCallback_shouldThrowException() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null, null);
        Config config = new Config();

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("callback"));

        // WHEN
        discovery.completeSelectedOperatorDiscovery(
                config.getClientId(),
                config.getClientSecret(),
                config.getDiscoveryURL(),
                null,
                "",
                "",
                null,
                null,
                null);
    }

    @Test
    public void completeSelectedOperator_withMissingPreferences_shouldThrowException() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null, null);
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("preferences"));

        // WHEN
        discovery.completeSelectedOperatorDiscovery(
                null,
                null,
                "",
                "",
                null,
                null,
                captureDiscoveryResponse);
    }

    @Test
    public void completeSelectedOperatorDiscovery_withDetailsInCache_shouldReturnCachedValue()
            throws DiscoveryException
    {
        // GIVEN
        JsonNode expectedJsonObject = buildJsonObject();
        DiscoveryCacheValue value = new DiscoveryCacheValue(new Date(Long.MAX_VALUE), expectedJsonObject);
        DiscoveryCacheKey key = DiscoveryCacheKey.newWithDetails(SELECTED_MCC, SELECTED_MNC);

        IDiscoveryCache cache = Factory.getDefaultDiscoveryCache();
        cache.add(key, value);

        IDiscovery discovery = Factory.getDiscovery(cache, null);

        Config config = new Config();
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();

        // WHEN
        discovery.completeSelectedOperatorDiscovery(
                config, null,
                SELECTED_MCC, SELECTED_MNC,
                null, null, captureDiscoveryResponse );

        // THEN
        DiscoveryResponse discoveryResponse = captureDiscoveryResponse.getDiscoveryResponse();

        assertTrue(discoveryResponse.isCached());
        assertEquals(0,discoveryResponse.getResponseCode());
        assertNull(discoveryResponse.getHeaders());
        assertEquals(expectedJsonObject, discoveryResponse.getResponseData());
    }

    @Test
    public void completeSelectedOperatorDiscovery_whenOperatorIdentified_shouldCacheValue()
            throws DiscoveryException, IOException, RestException
    {
        // GIVEN
        IDiscoveryCache cache = Factory.getDefaultDiscoveryCache();

        JsonNode expectedJsonObject = buildJsonObject();
        RestClient mockedRestClient = mock(RestClient.class);
        RestResponse restResponse = new RestResponse("", 200, buildHeaders(), buildJsonStr() );

        when(mockedRestClient.callRestEndPoint(any(HttpRequestBase.class), any(HttpClientContext.class), anyInt(), Matchers.<List<KeyValuePair>>any())).thenReturn(restResponse);

        IDiscovery discovery = Factory.getDiscovery(cache, mockedRestClient);

        Config config = new Config();
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();

        // WHEN
        discovery.completeSelectedOperatorDiscovery(
                config, null,
                SELECTED_MCC, SELECTED_MNC,
                null, null, captureDiscoveryResponse );

        // THEN
        DiscoveryCacheKey key = DiscoveryCacheKey.newWithDetails(SELECTED_MCC, SELECTED_MNC);
        DiscoveryCacheValue value = cache.get(key);

        assertNotNull(value);
        assertEquals(expectedJsonObject, value.getValue());
    }

    @Test
    public void completeSelectedOperatorDiscovery_withAllOptionsSet_shouldCreateExpectedRequest()
            throws DiscoveryException, RestException, IOException
    {
        // GIVEN
        RestClient mockedRestClient = mock(RestClient.class);
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();
        Config config = new Config();

        String exampleJson = buildJsonStr();
        JsonNode expectedJsonObject = buildJsonObject();
        final RestResponse restResponse = new RestResponse(null, 0, null, exampleJson);

        CompleteSelectedOperatorDiscoveryOptions options = new CompleteSelectedOperatorDiscoveryOptions();
        options.setTimeout(333);

        String expectedAccept = "application/json";

        String expectedURL = "http://discovery.sandbox.mobileconnect.io/v2/discovery?Redirect_URL=http%3A%2F%2Flocalhost%3A8080%2Fmobileconnect&Selected-MCC=MCC&Selected-MNC=MNC";

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
        discovery.completeSelectedOperatorDiscovery(
                config,
                Config.REDIRECT_URL,
                SELECTED_MCC, SELECTED_MNC,
                options, null, captureDiscoveryResponse);

        // THEN
        DiscoveryResponse discoveryResponse = captureDiscoveryResponse.getDiscoveryResponse();
        assertEquals(expectedJsonObject, discoveryResponse.getResponseData());

        HttpRequestBase capturedRequest = captureHttpRequestBase.getValue();
        assertEquals(expectedURL, capturedRequest.getURI().toString());
        assertEquals(expectedAccept, capturedRequest.getFirstHeader("accept").getValue());
    }

    @Test
    public void completeSelectedOperatorDiscovery_withDefaults_shouldCreateExpectedRequest()
            throws DiscoveryException, RestException, IOException
    {
        // GIVEN
        RestClient mockedRestClient = mock(RestClient.class);
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();
        Config config = new Config();

        String exampleJson = buildJsonStr();
        JsonNode expectedJsonObject = buildJsonObject();
        final RestResponse restResponse = new RestResponse(null, 0, null, exampleJson);

        String expectedAccept = "application/json";

        String expectedURL = "http://discovery.sandbox.mobileconnect.io/v2/discovery?Redirect_URL=http%3A%2F%2Flocalhost%2F&Selected-MCC=MCC&Selected-MNC=MNC";

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
        }).when(mockedRestClient).callRestEndPoint(any(HttpRequestBase.class), any(HttpClientContext.class), eq(30000), Matchers.<List<KeyValuePair>>any());

        IDiscovery discovery = Factory.getDiscovery(null, mockedRestClient);

        // WHEN
        discovery.completeSelectedOperatorDiscovery(
                config,
                null,
                SELECTED_MCC, SELECTED_MNC,
                null, null, captureDiscoveryResponse);

        // THEN
        DiscoveryResponse discoveryResponse = captureDiscoveryResponse.getDiscoveryResponse();
        assertEquals(expectedJsonObject, discoveryResponse.getResponseData());

        HttpRequestBase capturedRequest = captureHttpRequestBase.getValue();
        assertEquals(expectedURL, capturedRequest.getURI().toString());
        assertEquals(expectedAccept, capturedRequest.getFirstHeader("accept").getValue());
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
