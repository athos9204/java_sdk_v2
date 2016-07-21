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
package com.gsma.mobileconnect.r2.utils;

import com.google.common.collect.ImmutableList;
import com.gsma.mobileconnect.r2.ErrorResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.List;

import static org.testng.Assert.*;

/**
 * Tests {@link HttpUtils}
 *
 * @since 2.0
 */
public class HttpUtilsTest
{
    private static final Iterable<KeyValuePair> AVAILABLE_COOKIES =
        new KeyValuePair.ListBuilder().add("a", "1").add("b", "2").add("c", "3").build();

    @Test
    public void generateAuthenticationError_noHeader()
    {
        final List<KeyValuePair> headers = new KeyValuePair.ListBuilder().build();

        assertNull(HttpUtils.generateAuthenticationError(headers));
    }

    @Test
    public void generateAuthenticationError_emptyHeader()
    {
        final List<KeyValuePair> headers =
            new KeyValuePair.ListBuilder().add(HttpHeaders.WWW_AUTHENTICATE, "").build();

        assertNull(HttpUtils.generateAuthenticationError(headers));
    }

    @Test
    public void generateAuthenticationError_invalidHeader()
    {
        final List<KeyValuePair> headers =
            new KeyValuePair.ListBuilder().add(HttpHeaders.WWW_AUTHENTICATE, "${!#0").build();

        assertNull(HttpUtils.generateAuthenticationError(headers));
    }

    @Test
    public void generateAuthenticationError_validHeader()
    {
        final List<KeyValuePair> headers = new KeyValuePair.ListBuilder()
            .add(HttpHeaders.WWW_AUTHENTICATE,
                "Bearer error=\"invalid_request\", error_description=\"No Access Token\"")
            .build();

        final ErrorResponse response = HttpUtils.generateAuthenticationError(headers);
        assertNotNull(response);
        assertEquals(response.getError(), "invalid_request");
        assertEquals(response.getErrorDescription(), "No Access Token");
    }

    @DataProvider
    public Object[][] isHttpErrorCodeData()
    {
        return new Object[][] {{HttpStatus.SC_OK, false}, {HttpStatus.SC_ACCEPTED, false},
                               {HttpStatus.SC_TEMPORARY_REDIRECT, false},
                               {HttpStatus.SC_UNAUTHORIZED, true},
                               {HttpStatus.SC_BAD_REQUEST, true},
                               {HttpStatus.SC_SERVICE_UNAVAILABLE, true},
                               {HttpStatus.SC_GATEWAY_TIMEOUT, true}};
    }

    @Test(dataProvider = "isHttpErrorCodeData")
    public void isHttpErrorCode(int code, boolean error)
    {
        assertEquals(HttpUtils.isHttpErrorCode(code), error);
    }

    @DataProvider
    public Object[][] extractQueryValueData()
    {
        return new Object[][] {{"http://test?test=answer", "test", "answer"},
                               {"http://test", "test", null}, {"http://test?test=", "test", ""},
                               {"http://test?a=0&b=1&test=answer&c=2", "test", "answer"}};
    }

    @Test(dataProvider = "extractQueryValueData")
    public void extractQueryValue(final String uri, final String key, final String expectedValue)
    {
        final String value = HttpUtils.extractQueryValue(URI.create(uri), key);
        assertEquals(value, expectedValue);
    }

    @DataProvider
    public Object[][] proxyRequiredData()
    {
        return new Object[][] {
            {ImmutableList.<String>of(), AVAILABLE_COOKIES, new KeyValuePair[] {}},
            {ImmutableList.of("b"), AVAILABLE_COOKIES,
             new KeyValuePair[] {new KeyValuePair("b", "2")}},
            {ImmutableList.of("b"), ImmutableList.<KeyValuePair>of(), new KeyValuePair[] {}},
            {ImmutableList.of("b"), null, new KeyValuePair[] {}}};
    }

    @Test(dataProvider = "proxyRequiredData")
    public void proxyRequired(final List<String> required, final Iterable<KeyValuePair> available,
        final KeyValuePair[] expected)
    {
        final List<KeyValuePair> result = HttpUtils.proxyRequired(required, available);

        assertEqualsNoOrder(result.toArray(new KeyValuePair[] {}), expected);
    }
}
