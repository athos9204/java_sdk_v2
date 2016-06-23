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
import com.gsma.mobileconnect.oidc.DiscoveryResponseExpiredException;
import com.gsma.mobileconnect.oidc.IOIDC;
import com.gsma.mobileconnect.oidc.OIDCException;
import com.gsma.mobileconnect.oidc.ParsedIdToken;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

public class ParseIDTokenTest
{
    @Rule
    final public ExpectedException thrown = ExpectedException.none();

    @Test
    public void parseIDToken_withMissingDiscoveryResponse_shouldThrowException() throws OIDCException, DiscoveryResponseExpiredException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        CaptureParsedIdToken captureParsedIdToken = new CaptureParsedIdToken();

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("discoveryResult"));

        // WHEN
        oidc.parseIDToken(null, "", null, captureParsedIdToken);
    }

    @Test
    public void parseIDToken_withMissingIdToken_shouldThrowException() throws OIDCException, DiscoveryResponseExpiredException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        CaptureParsedIdToken captureParsedIdToken = new CaptureParsedIdToken();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, buildJsonNode());

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("id_token"));

        // WHEN
        oidc.parseIDToken(discoveryResponse, null, null, captureParsedIdToken);
    }

    @Test
    public void parseIDToken_withMissingCallback_shouldThrowException() throws OIDCException, DiscoveryResponseExpiredException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, buildJsonNode());

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("callback"));

        // WHEN
        oidc.parseIDToken(discoveryResponse, "", null, null);
    }

    @Test
    public void parseIDToken_withExpiredDiscoveryResponse_shouldThrowDiscoveryResponseExpiredException() throws OIDCException, DiscoveryResponseExpiredException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        CaptureParsedIdToken captureParsedIdToken = new CaptureParsedIdToken();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MIN_VALUE), 0, null, buildJsonNode());

        // THEN
        thrown.expect(DiscoveryResponseExpiredException.class);
        thrown.expectMessage(containsString("discoveryResult"));

        // WHEN
        oidc.parseIDToken(discoveryResponse, "", null, captureParsedIdToken);
    }

    @Test
    public void parseIDToken_withValid_id_token_shouldSucceed() throws OIDCException, DiscoveryResponseExpiredException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        CaptureParsedIdToken captureParsedIdToken = new CaptureParsedIdToken();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, buildJsonNode());

        String expected_id_token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJub25jZSI6Im5vbmNlXzAuc2V6Mmw2dHExMGkiLCJzdWIiOiIzMjM5YzZkMTZmM2IwMTNhZmE2ZDExZGY3NTkyMmFmNCIsImFtciI6IlNNU19VUkwiLCJhdXRoX3RpbWUiOjE0NTI1MDg5NTgsImFjciI6IjIiLCJhenAiOiIwYzlkZjIxOSIsImlhdCI6MTQ1MjUwODk1NywiZXhwIjoxNDUyNTEyNTU3LCJhdWQiOlsiMGM5ZGYyMTkiXSwiaXNzIjoiaHR0cDovL29wZXJhdG9yX2Euc2FuZGJveC5tb2JpbGVjb25uZWN0LmlvL29pZGMvYWNjZXNzdG9rZW4ifQ.LPvxElQV8dJL7HijFiu2j_Zk_ZO0-KehpX3_rURbOHo";

        // WHEN
        oidc.parseIDToken(discoveryResponse, expected_id_token, null, captureParsedIdToken);

        // THEN
        ParsedIdToken parsedIdToken = captureParsedIdToken.getParsedIdToken();

        assertEquals(expected_id_token, parsedIdToken.get_id_token());
    }

    @Test
    public void parseIDToken_withInvalid_id_token_shouldThrowException() throws OIDCException, DiscoveryResponseExpiredException
    {
        // GIVEN
        IOIDC oidc = Factory.getOIDC(null);
        CaptureParsedIdToken captureParsedIdToken = new CaptureParsedIdToken();
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 0, null, buildJsonNode());

        String invalid_id_token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJub25jZSI6Im5vbmzMjM5YzZkMTZmM2IwMTNhZmE2ZDExZGY3NTkyMmFmNCIsImFtciI6IlNNU19VUkwiLCJhdXRoX3RpbWUiOjE0NTI1MDg5NTgsImFjciI6IjIiLCJhenAiOiIwYzlkZjIxOSIsImlhdCI6MTQ1MjUwODk1NywiZXhwIjoxNDUyNTEyNTU3LCJhdWQiOlsiMGM5ZGYyMTkiXSwiaXNzIjoiaHR0cDovL29wZXJhdG9yX2Euc2FuZGJveC5tb2JpbGVjb25uZWN0LmlvL29pZGMvYWNjZXNzdG9rZW4ifQ.LPvxElQV8dJL7HijFiu2j_Zk_ZO0-KehpX3_rURbOHo";

        // THEN
        thrown.expect(OIDCException.class);
        thrown.expectMessage(containsString("Not an id_token"));

        // WHEN
        oidc.parseIDToken(discoveryResponse, invalid_id_token, null, captureParsedIdToken);
    }

    private JsonNode buildJsonNode()
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.createObjectNode();
    }
}
