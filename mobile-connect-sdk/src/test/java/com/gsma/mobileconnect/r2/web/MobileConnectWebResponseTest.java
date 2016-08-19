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
package com.gsma.mobileconnect.r2.web;

import com.gsma.mobileconnect.r2.MobileConnectStatus;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @since 2.0
 */
public class MobileConnectWebResponseTest
{
    @Test
    public void webResponseWithAuthenticationStatus()
    {
        final MobileConnectStatus status = MobileConnectStatus.authentication("url", "state", "nonce");
        final MobileConnectWebResponse mobileConnectWebResponse = new MobileConnectWebResponse(status);

        assertNull(mobileConnectWebResponse.getApplicationShortName());
        assertNull(mobileConnectWebResponse.getSubscriberId());
        assertNull(mobileConnectWebResponse.getToken());
        assertNull(mobileConnectWebResponse.getIdentity());
        assertNull(mobileConnectWebResponse.getDescription());
        assertNull(mobileConnectWebResponse.getError());
        assertEquals(mobileConnectWebResponse.getUrl(), "url");
        assertEquals(mobileConnectWebResponse.getState(), "state");
        assertEquals(mobileConnectWebResponse.getNonce(), "nonce");
        assertEquals(mobileConnectWebResponse.getStatus(), "success");
        assertEquals(mobileConnectWebResponse.getAction(), "authentication");
    }

    @Test
    public void webResponseWithErrorStatus()
    {
        final MobileConnectStatus status = MobileConnectStatus.error("error", "message", new Exception());
        final MobileConnectWebResponse mobileConnectWebResponse = new MobileConnectWebResponse(status);

        assertEquals(mobileConnectWebResponse.getStatus(), "failure");
        assertEquals(mobileConnectWebResponse.getAction(), "error");
        assertEquals(mobileConnectWebResponse.getError(), "error");
        assertEquals(mobileConnectWebResponse.getDescription(), "message");
    }
}
