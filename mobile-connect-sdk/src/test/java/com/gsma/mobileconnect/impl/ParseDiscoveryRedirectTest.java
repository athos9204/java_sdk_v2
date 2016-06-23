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

import com.gsma.mobileconnect.discovery.IDiscovery;
import com.gsma.mobileconnect.discovery.ParsedDiscoveryRedirect;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class ParseDiscoveryRedirectTest
{
    private static final String MCC_MNC_PARAMETER = "mcc_mnc";

    @Rule
    final public ExpectedException thrown = ExpectedException.none();

    @Test
    public void parseDiscoveryRedirect_withMissingURL_shouldThrowException() throws URISyntaxException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null, null);
        CaptureParsedDiscoveryRedirect captureParsedDiscoveryRedirect = new CaptureParsedDiscoveryRedirect();

        //THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("redirectURL"));

        // WHEN
        discovery.parseDiscoveryRedirect(null, captureParsedDiscoveryRedirect);
    }

    @Test
    public void parseDiscoveryRedirect_withMissingCallback_shouldThrowException() throws URISyntaxException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null, null);

        //THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("callback"));

        // WHEN
        discovery.parseDiscoveryRedirect("", null);
    }

    @Test
    public void parseDiscoveryRedirect_withNoQueryString_shouldReturnEmptyDetails() throws URISyntaxException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null, null);
        CaptureParsedDiscoveryRedirect captureParsedDiscoveryRedirect = new CaptureParsedDiscoveryRedirect();
        URIBuilder builder = new URIBuilder("http://localhost/redirect");

        // WHEN
        discovery.parseDiscoveryRedirect(builder.build().toString(), captureParsedDiscoveryRedirect);

        // THEN
        ParsedDiscoveryRedirect parsedDiscoveryRedirect = captureParsedDiscoveryRedirect.getParsedDiscoveryRedirect();

        assertNull(parsedDiscoveryRedirect.getSelectedMCC());
        assertNull(parsedDiscoveryRedirect.getSelectedMNC());
        assertNull(parsedDiscoveryRedirect.getEncryptedMSISDN());
        assertFalse(parsedDiscoveryRedirect.hasMCCAndMNC());
    }

    @Test
    public void parseDiscoveryRedirect_withValidURL_shouldSucceed() throws URISyntaxException
    {
        // GIVEN
        URIBuilder builder = new URIBuilder("http://localhost/redirect");
        String expectedMCC = "901";
        String expectedMNC = "01";
        builder.addParameter(MCC_MNC_PARAMETER, expectedMCC + "_" + expectedMNC);
        String expectedEncryptedMsisdn = "expected msisdn";
        builder.addParameter("subscriber_id", expectedEncryptedMsisdn);

        IDiscovery discovery = Factory.getDiscovery(null, null);
        CaptureParsedDiscoveryRedirect captureParsedDiscoveryRedirect = new CaptureParsedDiscoveryRedirect();

        // WHEN
        discovery.parseDiscoveryRedirect(builder.build().toString(), captureParsedDiscoveryRedirect);

        // THEN
        ParsedDiscoveryRedirect parsedDiscoveryRedirect = captureParsedDiscoveryRedirect.getParsedDiscoveryRedirect();

        assertEquals(expectedMCC, parsedDiscoveryRedirect.getSelectedMCC());
        assertEquals(expectedMNC, parsedDiscoveryRedirect.getSelectedMNC());
        assertEquals(expectedEncryptedMsisdn, parsedDiscoveryRedirect.getEncryptedMSISDN());
        assertTrue(parsedDiscoveryRedirect.hasMCCAndMNC());
    }

    @Test
    public void parseDiscoveryRedirect_withUnexpectedMccMnc_shouldReturnEmptyDetails() throws URISyntaxException
    {
        // GIVEN
        URIBuilder builder = new URIBuilder("http://localhost/redirect");
        builder.addParameter(MCC_MNC_PARAMETER, "random-string");

        IDiscovery discovery = Factory.getDiscovery(null, null);
        CaptureParsedDiscoveryRedirect captureParsedDiscoveryRedirect = new CaptureParsedDiscoveryRedirect();

        // WHEN
        discovery.parseDiscoveryRedirect(builder.build().toString(), captureParsedDiscoveryRedirect);

        // THEN
        ParsedDiscoveryRedirect parsedDiscoveryRedirect = captureParsedDiscoveryRedirect.getParsedDiscoveryRedirect();

        assertNull(parsedDiscoveryRedirect.getSelectedMCC());
        assertNull(parsedDiscoveryRedirect.getSelectedMNC());
        assertNull(parsedDiscoveryRedirect.getEncryptedMSISDN());
        assertFalse(parsedDiscoveryRedirect.hasMCCAndMNC());
    }

    @Test
    public void parseDiscoveryRedirect_withBlankMccMnc_shouldReturnEmptyDetails() throws URISyntaxException
    {
        // GIVEN
        URIBuilder builder = new URIBuilder("http://localhost/redirect");
        builder.addParameter(MCC_MNC_PARAMETER, "");

        IDiscovery discovery = Factory.getDiscovery(null, null);
        CaptureParsedDiscoveryRedirect captureParsedDiscoveryRedirect = new CaptureParsedDiscoveryRedirect();

        // WHEN
        discovery.parseDiscoveryRedirect(builder.build().toString(), captureParsedDiscoveryRedirect);

        // THEN
        ParsedDiscoveryRedirect parsedDiscoveryRedirect = captureParsedDiscoveryRedirect.getParsedDiscoveryRedirect();

        assertNull(parsedDiscoveryRedirect.getSelectedMCC());
        assertNull(parsedDiscoveryRedirect.getSelectedMNC());
        assertNull(parsedDiscoveryRedirect.getEncryptedMSISDN());
        assertFalse(parsedDiscoveryRedirect.hasMCCAndMNC());
    }
}
