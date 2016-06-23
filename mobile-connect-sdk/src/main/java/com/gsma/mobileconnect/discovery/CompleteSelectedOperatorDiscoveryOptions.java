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
package com.gsma.mobileconnect.discovery;

import java.util.List;

/**
 * Class to hold the optional parameters for {@link IDiscovery#completeSelectedOperatorDiscovery(String, String, String, String, String, String, CompleteSelectedOperatorDiscoveryOptions, List, IDiscoveryResponseCallback)}
 */
public class CompleteSelectedOperatorDiscoveryOptions
{
    /**
     * Default timeout value for the Rest call in milliseconds.
     */
    private static final int DEFAULT_TIMEOUT = 30000;

    /**
     * Default value for cookies enabled.
     */
    private static final boolean DEFAULT_COOKIES_ENABLED = true;

    private int timeout;

    private boolean cookiesEnabled;

    /**
     * Return the timeout value used by the SDK for the Discovery Rest request.
     *
     * @return The timeout to be used.
     */
    public int getTimeout()
    {
        return timeout;
    }

    /**
     * Set the timeout of a Discovery request.
     * <p>
     * The timeout (in milliseconds) to be used by the SDK when making a Discovery request.
     *
     * @param newValue New timeout value.
     */
    public void setTimeout(int newValue)
    {
        timeout = newValue;
    }

    /**
     * Are cookies to be stored/sent.
     *
     * @return True if cookies are to be sent.
     */
    public boolean isCookiesEnabled()
    {
        return cookiesEnabled;
    }

    /**
     * Are cookies to be stored/sent.
     *
     * @param newValue True id cookies are to be sent.
     */
    public void setCookiesEnabled(boolean newValue)
    {
        cookiesEnabled = newValue;
    }

    public CompleteSelectedOperatorDiscoveryOptions()
    {
        timeout = DEFAULT_TIMEOUT;
        cookiesEnabled = DEFAULT_COOKIES_ENABLED;
    }
}
