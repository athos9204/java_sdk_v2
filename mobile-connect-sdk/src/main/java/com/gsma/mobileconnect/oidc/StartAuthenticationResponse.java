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

package com.gsma.mobileconnect.oidc;

import com.gsma.mobileconnect.discovery.DiscoveryResponse;

/**
 * Class to hold the response from {@link IOIDC#startAuthentication(DiscoveryResponse, String, String, String, String, Integer, String, String, AuthenticationOptions, IStartAuthenticationCallback)}.
 */
public class StartAuthenticationResponse
{
    private String url;

    private String screenMode;

    /**
     * The URL to use to authorize with the identified operator.
     *
     * @return The URL to use.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * The URL to use to authorize with the identified operator.
     *
     * @param url The url to use.
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * The screen mode to use for the UI.
     *
     * @return The screen mode to use for the UI.
     */
    public String getScreenMode()
    {
        return screenMode;
    }

    /**
     * The screen mode to use for the UI.
     *
     * @param screenMode The screen mode to use.
     */
    public void setScreenMode(String screenMode)
    {
        this.screenMode = screenMode;
    }
}
