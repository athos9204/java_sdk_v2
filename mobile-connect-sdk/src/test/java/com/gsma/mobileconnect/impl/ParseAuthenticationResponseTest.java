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

import com.gsma.mobileconnect.oidc.IOIDC;
import com.gsma.mobileconnect.oidc.OIDCException;
import com.gsma.mobileconnect.oidc.ParsedAuthorizationResponse;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ParseAuthenticationResponseTest
{
    private final static String ERROR_STRING = "expected-error";
    private final static String ERROR_DESCRIPTION_STRING = "expected-error-description";
    private final static String ERROR_URI_STRING = "http://localhost/expected_error_uri";

    private final static String STATE_STRING = "expected-state";
    private final static String CODE_STRING = "expected-code";

    @Rule
    final public ExpectedException thrown = ExpectedException.none();

    @Test
    public void parseAuthenticationResponse_withMissingUrl_shouldThrowException() throws OIDCException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        CaptureParsedAuthorizationResponse captureParsedAuthorizationResponse = new CaptureParsedAuthorizationResponse();

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("redirectURL"));

        // WHEN
        oidc.parseAuthenticationResponse(null, captureParsedAuthorizationResponse);
    }

    @Test
    public void parseAuthenticationResponse_withCallback_shouldThrowException() throws OIDCException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("callback"));

        // WHEN
        oidc.parseAuthenticationResponse("", null);
    }

    @Test
    public void parseAuthenticationResponse_withInvalidUrl_shouldNotFail() throws OIDCException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        CaptureParsedAuthorizationResponse captureParsedAuthorizationResponse = new CaptureParsedAuthorizationResponse();

        // WHEN
        oidc.parseAuthenticationResponse("", captureParsedAuthorizationResponse);

        // THEN
        ParsedAuthorizationResponse parsedAuthorizationResponse = captureParsedAuthorizationResponse.getParsedAuthorizationResponse();
        assertNull(parsedAuthorizationResponse.get_error());
        assertNull(parsedAuthorizationResponse.get_error_description());
        assertNull(parsedAuthorizationResponse.get_error_uri());
        assertNull(parsedAuthorizationResponse.get_code());
        assertNull(parsedAuthorizationResponse.get_state());
    }

    @Test
    public void parseAuthenticationResponse_withErrorUrl_shouldParseError() throws OIDCException, URISyntaxException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        CaptureParsedAuthorizationResponse captureParsedAuthorizationResponse = new CaptureParsedAuthorizationResponse();

        URIBuilder builder = new URIBuilder("");
        builder.addParameter("error", ERROR_STRING);
        builder.addParameter("error_description", ERROR_DESCRIPTION_STRING);
        builder.addParameter("error_uri", ERROR_URI_STRING);

        // WHEN
        oidc.parseAuthenticationResponse(builder.build().toString(), captureParsedAuthorizationResponse);

        // THEN
        ParsedAuthorizationResponse parsedAuthorizationResponse = captureParsedAuthorizationResponse.getParsedAuthorizationResponse();
        assertEquals(ERROR_STRING, parsedAuthorizationResponse.get_error());
        assertEquals(ERROR_DESCRIPTION_STRING, parsedAuthorizationResponse.get_error_description());
        assertEquals(ERROR_URI_STRING, parsedAuthorizationResponse.get_error_uri());
        assertNull(parsedAuthorizationResponse.get_code());
        assertNull(parsedAuthorizationResponse.get_state());
    }

    @Test
    public void parseAuthenticationResponse_withSuccessUrl_shouldParseSuccess() throws OIDCException, URISyntaxException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        CaptureParsedAuthorizationResponse captureParsedAuthorizationResponse = new CaptureParsedAuthorizationResponse();

        URIBuilder builder = new URIBuilder("");
        builder.addParameter("code", CODE_STRING);
        builder.addParameter("state", STATE_STRING);

        // WHEN
        oidc.parseAuthenticationResponse(builder.build().toString(), captureParsedAuthorizationResponse);

        // THEN
        ParsedAuthorizationResponse parsedAuthorizationResponse = captureParsedAuthorizationResponse.getParsedAuthorizationResponse();
        assertNull(parsedAuthorizationResponse.get_error());
        assertNull(parsedAuthorizationResponse.get_error_description());
        assertNull(parsedAuthorizationResponse.get_error_uri());
        assertEquals(CODE_STRING, parsedAuthorizationResponse.get_code());
        assertEquals(STATE_STRING, parsedAuthorizationResponse.get_state());
    }
}
