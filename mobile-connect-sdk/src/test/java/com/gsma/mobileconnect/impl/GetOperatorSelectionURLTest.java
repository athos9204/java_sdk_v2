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

import com.gsma.mobileconnect.discovery.DiscoveryException;
import com.gsma.mobileconnect.discovery.IDiscovery;
import com.gsma.mobileconnect.utils.*;
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
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GetOperatorSelectionURLTest
{
    @Rule
    final public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getOperatorSelectionURL_withMissingClientId_shouldThrowException() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null);
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();
        Config config = new Config();

        //THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("clientId"));

        // WHEN
        discovery.getOperatorSelectionURL(
                null,
                config.getClientSecret(),
                config.getDiscoveryURL(),
                Config.REDIRECT_URL,
                null,
                captureDiscoveryResponse);
    }

    @Test
    public void getOperatorSelectionURL_withMissingClientSecret_shouldThrowException() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null);
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();
        Config config = new Config();

        //THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("clientSecret"));

        // WHEN
        discovery.getOperatorSelectionURL(
                config.getClientId(),
                null,
                config.getDiscoveryURL(),
                Config.REDIRECT_URL,
                null,
                captureDiscoveryResponse);
    }

    @Test
    public void getOperatorSelectionURL_withMissingDiscoveryURL_shouldThrowException() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null);
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();
        Config config = new Config();

        //THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("discoveryURL"));

        // WHEN
        discovery.getOperatorSelectionURL(
                config.getClientId(),
                config.getClientSecret(),
                null,
                Config.REDIRECT_URL,
                null,
                captureDiscoveryResponse);
    }

    @Test
    public void getOperatorSelectionURL_withMissingRedirectURL_shouldThrowException() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null);
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();
        Config config = new Config();

        //THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("redirectURL"));

        // WHEN
        discovery.getOperatorSelectionURL(
                config.getClientId(),
                config.getClientSecret(),
                config.getDiscoveryURL(),
                null,
                null,
                captureDiscoveryResponse);
    }

    @Test
    public void getOperatorSelectionURL_withMissingCallback_shouldThrowException() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null);
        Config config = new Config();

        //THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("callback"));

        // WHEN
        discovery.getOperatorSelectionURL(
                config.getClientId(),
                config.getClientSecret(),
                config.getDiscoveryURL(),
                Config.REDIRECT_URL,
                null,
                null);
    }

    @Test
    public void getOperatorSelectionURL_withMissingPreferences_shouldThrowException() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null);
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();

        //THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("preferences"));

        // WHEN
        discovery.getOperatorSelectionURL(
                null,
                null,
                null,
                captureDiscoveryResponse);
    }

    @Test
    public void getOperatorSelectionURL_shouldCreateExpectedRequest() throws DiscoveryException, IOException, RestException
    {
        // GIVEN
        RestClient mockedRestClient = mock(RestClient.class);

        final CaptureHttpRequestBase captureHttpRequestBase = new CaptureHttpRequestBase();
        final RestResponse restResponse = new RestResponse("", 0, null, "");
        TimeoutOptions options = new TimeoutOptions();
        options.setTimeout(333);

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
        Config config = new Config();
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();


        String expectedURL = "http://discovery.sandbox.mobileconnect.io/v2/discovery?Manually-Select=true&Using-Mobile-Data=false&Redirect_URL=http%3A%2F%2Flocalhost%3A8080%2Fmobileconnect";
        String expectedAccept = "application/json";

        // WHEN
        discovery.getOperatorSelectionURL(
                config,
                Config.REDIRECT_URL,
                options,
                captureDiscoveryResponse);

        // THEN
        HttpRequestBase capturedRequest = captureHttpRequestBase.getValue();

        assertEquals(expectedURL, capturedRequest.getURI().toString());
        assertEquals(expectedAccept, capturedRequest.getFirstHeader("accept").getValue());
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
            discovery.getOperatorSelectionURL(config, Config.REDIRECT_URL, null, captureDiscoveryResponse);
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

    private List<KeyValuePair> buildHeaders()
    {
        List<KeyValuePair> headers = new ArrayList<KeyValuePair>(1);
        headers.add(new KeyValuePair("Content-Type", "application/json"));
        return headers;
    }
}
