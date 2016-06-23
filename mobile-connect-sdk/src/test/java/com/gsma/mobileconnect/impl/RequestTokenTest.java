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
import com.gsma.mobileconnect.utils.KeyValuePair;
import com.gsma.mobileconnect.utils.RestClient;
import com.gsma.mobileconnect.utils.RestException;
import com.gsma.mobileconnect.utils.RestResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
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

public class RequestTokenTest
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

    static private final String OPERATOR_MISSING_TOKEN_JSON_STRING =
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

    private static final String EXPECTED_ACCESS_TOKEN = "082009c0-b850-11e5-beb1-398aa5789941";
    private static final String EXPECTED_TOKEN_TYPE = "Bearer";
    private static final String EXPECTED_EXPIRES_IN = "3600";
    private static final String EXPECTED_ID_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJub25jZSI6Im5vbmNlXzAuc2V6Mmw2dHExMGkiLCJzdWIiOiIzMjM5YzZkMTZmM2IwMTNhZmE2ZDExZGY3NTkyMmFmNCIsImFtciI6IlNNU19VUkwiLCJhdXRoX3RpbWUiOjE0NTI1MDg5NTgsImFjciI6IjIiLCJhenAiOiIwYzlkZjIxOSIsImlhdCI6MTQ1MjUwODk1NywiZXhwIjoxNDUyNTEyNTU3LCJhdWQiOlsiMGM5ZGYyMTkiXSwiaXNzIjoiaHR0cDovL29wZXJhdG9yX2Euc2FuZGJveC5tb2JpbGVjb25uZWN0LmlvL29pZGMvYWNjZXNzdG9rZW4ifQ.LPvxElQV8dJL7HijFiu2j_Zk_ZO0-KehpX3_rURbOHo";

    static private final String ACCESS_TOKEN_JSON_STRING =
            "{\n" +
                    "   \"access_token\":\"" + EXPECTED_ACCESS_TOKEN + "\",\n" +
                    "   \"token_type\":\"" + EXPECTED_TOKEN_TYPE + "\",\n" +
                    "   \"expires_in\":" + EXPECTED_EXPIRES_IN + ",\n" +
                    "   \"id_token\":\"" + EXPECTED_ID_TOKEN + "\"\n" +
                    "}";

    final static private String ERROR_STRING = "invalid_request";
    final static private String ERROR_DESCRIPTION_STRING = "the server could not understand the client's request";
    final static private String ERROR_RESPONSE_JSON_STRING = "{\n" +
            "   \"error\":\"" + ERROR_STRING + "\",\n" +
            "   \"error_description\":\"" + ERROR_DESCRIPTION_STRING + "\"\n" +
            "}";

    @Rule
    final public ExpectedException thrown = ExpectedException.none();

    @Test
    public void requestToken_withMissingDiscoveryResult_shouldThrowException() throws OIDCException, DiscoveryResponseExpiredException
    {
        // GIVEN
        IOIDC ioidc = Factory.getOIDC(null);
        CaptureRequestTokenResponse captureRequestTokenResponse = new CaptureRequestTokenResponse();

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("discoveryResult"));

        // WHEN
        ioidc.requestToken(null, "", "", null, captureRequestTokenResponse);
    }

    @Test
    public void requestToken_withMissingRedirectURI_shouldThrowException() throws OIDCException, DiscoveryResponseExpiredException, IOException
    {
        // GIVEN
        IOIDC ioidc = Factory.getOIDC(null);
        CaptureRequestTokenResponse captureRequestTokenResponse = new CaptureRequestTokenResponse();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, parseJson(OPERATOR_JSON_STRING));

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("redirectURI"));

        // WHEN
        ioidc.requestToken(discoveryResponse, null, "", null, captureRequestTokenResponse);
    }

    @Test
    public void requestToken_withMissingCode_shouldThrowException() throws OIDCException, DiscoveryResponseExpiredException, IOException
    {
        // GIVEN
        IOIDC ioidc = Factory.getOIDC(null);
        CaptureRequestTokenResponse captureRequestTokenResponse = new CaptureRequestTokenResponse();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, parseJson(OPERATOR_JSON_STRING));

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("code"));

        // WHEN
        ioidc.requestToken(discoveryResponse, "", null, null, captureRequestTokenResponse);
    }

    @Test
    public void requestToken_withMissingCallback_shouldThrowException() throws OIDCException, DiscoveryResponseExpiredException, IOException
    {
        // GIVEN
        IOIDC ioidc = Factory.getOIDC(null);
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, parseJson(OPERATOR_JSON_STRING));

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("callback"));

        // WHEN
        ioidc.requestToken(discoveryResponse, "", "", null, null);
    }

    @Test
    public void requestToken_withExpiredDiscoveryResponse_shouldThrowException() throws OIDCException, DiscoveryResponseExpiredException, IOException
    {
        // GIVEN
        IOIDC ioidc = Factory.getOIDC(null);
        CaptureRequestTokenResponse captureRequestTokenResponse = new CaptureRequestTokenResponse();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MIN_VALUE), 0, null, parseJson(OPERATOR_JSON_STRING));

        // THEN
        thrown.expect(DiscoveryResponseExpiredException.class);
        thrown.expectMessage(containsString("discoveryResult has expired"));

        // WHEN
        ioidc.requestToken(discoveryResponse, "", "", null, captureRequestTokenResponse);
    }

    @Test
    public void requestToken_withInvalidDiscoveryResponse_shouldThrowException() throws OIDCException, DiscoveryResponseExpiredException, IOException
    {
        // GIVEN
        IOIDC ioidc = Factory.getOIDC(null);
        CaptureRequestTokenResponse captureRequestTokenResponse = new CaptureRequestTokenResponse();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, parseJson("{}"));

        // THEN
        thrown.expect(OIDCException.class);
        thrown.expectMessage(containsString("Not a valid discovery result."));

        // WHEN
        ioidc.requestToken(discoveryResponse, "", "", null, captureRequestTokenResponse);
    }

    @Test
    public void requestToken_withMissingTokenDiscoveryResponse_shouldThrowException() throws OIDCException, DiscoveryResponseExpiredException, IOException
    {
        // GIVEN
        IOIDC ioidc = Factory.getOIDC(null);
        CaptureRequestTokenResponse captureRequestTokenResponse = new CaptureRequestTokenResponse();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, parseJson(OPERATOR_MISSING_TOKEN_JSON_STRING));

        // THEN
        thrown.expect(OIDCException.class);
        thrown.expectMessage(containsString("No token href"));

        // WHEN
        ioidc.requestToken(discoveryResponse, "", "", null, captureRequestTokenResponse);
    }

    @Test
    public void requestToken_withDefaults_shouldCreateExpectedRequest() throws OIDCException, DiscoveryResponseExpiredException, IOException, RestException
    {
        // GIVEN
        RestClient mockedRestClient = mock(RestClient.class);
        IOIDC ioidc = Factory.getOIDC(mockedRestClient);
        CaptureRequestTokenResponse captureRequestTokenResponse = new CaptureRequestTokenResponse();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, parseJson(OPERATOR_JSON_STRING));

        final CaptureHttpRequestBase captureHttpRequestBase = new CaptureHttpRequestBase();
        final RestResponse restResponse = new RestResponse(null, 0, null, "{}");

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

        // WHEN
        ioidc.requestToken(discoveryResponse, "", "", null, captureRequestTokenResponse);

        // THEN
        HttpRequestBase request = captureHttpRequestBase.getValue();

        assertEquals(TOKEN_HREF, request.getURI().toString());
        assertEquals("POST", request.getMethod());
        assertEquals("application/x-www-form-urlencoded", request.getFirstHeader("Content-Type").getValue());
        assertEquals("application/json", request.getFirstHeader("accept").getValue());

        assertTrue(request instanceof HttpPost);

        HttpPost postRequest = (HttpPost) request;
        List<NameValuePair> contents = URLEncodedUtils.parse(postRequest.getEntity());

        assertEquals("", findValueOfName(contents, "code"));
        assertEquals("authorization_code", findValueOfName(contents, "grant_type"));
        assertEquals("", findValueOfName(contents, "redirect_uri"));
    }

    @Test
    public void requestToken_withAllOptionsSet_shouldCreateExpectedRequest() throws OIDCException, DiscoveryResponseExpiredException, IOException, RestException
    {
        // GIVEN
        RestClient mockedRestClient = mock(RestClient.class);
        IOIDC ioidc = Factory.getOIDC(mockedRestClient);
        CaptureRequestTokenResponse captureRequestTokenResponse = new CaptureRequestTokenResponse();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, parseJson(OPERATOR_JSON_STRING));

        TokenOptions options = new TokenOptions();
        options.setTimeout(333);

        final CaptureHttpRequestBase captureHttpRequestBase = new CaptureHttpRequestBase();
        final RestResponse restResponse = new RestResponse(null, 0, null, "{}");

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

        String expectedRedirectURI = "expected-redirect-uri";
        String expectedCode = "expected-code";

        // WHEN
        ioidc.requestToken(discoveryResponse, expectedRedirectURI, expectedCode, options, captureRequestTokenResponse);

        // THEN
        HttpRequestBase request = captureHttpRequestBase.getValue();

        assertEquals(TOKEN_HREF, request.getURI().toString());
        assertEquals("POST", request.getMethod());
        assertEquals("application/x-www-form-urlencoded", request.getFirstHeader("Content-Type").getValue());
        assertEquals("application/json", request.getFirstHeader("accept").getValue());

        assertTrue(request instanceof HttpPost);

        HttpPost postRequest = (HttpPost) request;
        List<NameValuePair> contents = URLEncodedUtils.parse(postRequest.getEntity());

        assertEquals(expectedCode, findValueOfName(contents, "code"));
        assertEquals("authorization_code", findValueOfName(contents, "grant_type"));
        assertEquals(expectedRedirectURI, findValueOfName(contents, "redirect_uri"));
    }

    @Test
    public void requestToken_whenRestExceptionIsThrown_shouldThrowExpectedOIDCException() throws IOException, RestException, DiscoveryResponseExpiredException
    {
        // GIVEN
        RestClient mockedRestClient = mock(RestClient.class);
        IOIDC ioidc = Factory.getOIDC(mockedRestClient);
        CaptureRequestTokenResponse captureRequestTokenResponse = new CaptureRequestTokenResponse();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, parseJson(OPERATOR_JSON_STRING));

        RestResponse restResponse = new RestResponse("expected-uri", 404, buildHeaders(), "expected-contents");
        RestException restException = new RestException("TEST", restResponse);

        when(mockedRestClient.callRestEndPoint(any(HttpRequestBase.class), any(HttpClientContext.class), anyInt(), Matchers.<List<KeyValuePair>>any())).thenThrow(restException);

        OIDCException capturedException = null;
        // WHEN
        try
        {
            ioidc.requestToken(discoveryResponse, "", "", null, captureRequestTokenResponse);
        }
        catch (OIDCException ex)
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

    @Test
    public void requestToken_withValidResponse_shouldHaveSuccessResponse() throws IOException, RestException, OIDCException, DiscoveryResponseExpiredException
    {
        // GIVEN
        RestClient mockedRestClient = mock(RestClient.class);
        IOIDC ioidc = Factory.getOIDC(mockedRestClient);
        CaptureRequestTokenResponse captureRequestTokenResponse = new CaptureRequestTokenResponse();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(false, new Date(Long.MAX_VALUE), 200, buildHeaders(), parseJson(OPERATOR_JSON_STRING));

        final RestResponse restResponse = new RestResponse(null, 0, null, ACCESS_TOKEN_JSON_STRING);

        when(mockedRestClient.callRestEndPoint(any(HttpRequestBase.class), any(HttpClientContext.class), anyInt(), Matchers.<List<KeyValuePair>>any())).thenReturn(restResponse);

        // WHEN
        ioidc.requestToken(discoveryResponse, "", "", null, captureRequestTokenResponse);

        // THEN
        RequestTokenResponse requestTokenResponse = captureRequestTokenResponse.getRequestTokenResponse();

        assertEquals(restResponse.getHeaders(), requestTokenResponse.getHeaders());
        assertEquals(restResponse.getStatusCode(), requestTokenResponse.getResponseCode());

        assertNull(requestTokenResponse.getErrorResponse());

        assertEquals(EXPECTED_ACCESS_TOKEN, requestTokenResponse.getResponseData().get_access_token());
        assertEquals(EXPECTED_TOKEN_TYPE, requestTokenResponse.getResponseData().get_token_type());
        assertEquals(EXPECTED_EXPIRES_IN, requestTokenResponse.getResponseData().get_expires_in().toString());
        assertEquals(ACCESS_TOKEN_JSON_STRING, requestTokenResponse.getResponseData().getOriginalResponse());
        assertEquals(EXPECTED_ID_TOKEN, requestTokenResponse.getResponseData().getParsedIdToken().get_id_token());
    }

    @Test
    public void requestToken_withErrorResponse_shouldHaveErrorResponse() throws IOException, RestException, OIDCException, DiscoveryResponseExpiredException
    {
        // GIVEN
        RestClient mockedRestClient = mock(RestClient.class);
        IOIDC ioidc = Factory.getOIDC(mockedRestClient);
        CaptureRequestTokenResponse captureRequestTokenResponse = new CaptureRequestTokenResponse();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(false, new Date(Long.MAX_VALUE), 200, buildHeaders(), parseJson(OPERATOR_JSON_STRING));

        final RestResponse restResponse = new RestResponse(null, 0, null, ERROR_RESPONSE_JSON_STRING);

        when(mockedRestClient.callRestEndPoint(any(HttpRequestBase.class), any(HttpClientContext.class), anyInt(), Matchers.<List<KeyValuePair>>any())).thenReturn(restResponse);

        // WHEN
        ioidc.requestToken(discoveryResponse, "", "", null, captureRequestTokenResponse);

        // THEN
        RequestTokenResponse requestTokenResponse = captureRequestTokenResponse.getRequestTokenResponse();

        assertEquals(restResponse.getHeaders(), requestTokenResponse.getHeaders());
        assertEquals(restResponse.getStatusCode(), requestTokenResponse.getResponseCode());

        assertNotNull(requestTokenResponse.getErrorResponse());
        assertEquals(ERROR_STRING, requestTokenResponse.getErrorResponse().get_error());
        assertEquals(ERROR_DESCRIPTION_STRING, requestTokenResponse.getErrorResponse().get_error_description());
    }

    private String findValueOfName(List<NameValuePair> pairs, String name)
    {
        for(NameValuePair nvp: pairs)
        {
            if(nvp.getName().equals(name))
            {
                return nvp.getValue();
            }
        }
        return null;
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

    private List<KeyValuePair> buildHeaders()
    {
        List<KeyValuePair> headers = new ArrayList<KeyValuePair>(1);
        headers.add(new KeyValuePair("Content-Type", "application/json"));
        return headers;
    }
}
