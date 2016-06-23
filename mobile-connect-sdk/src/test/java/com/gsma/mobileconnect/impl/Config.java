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

import com.gsma.mobileconnect.discovery.IPreferences;

class Config
    implements IPreferences
{
    static final String REDIRECT_URL = "http://localhost:8080/mobileconnect";

    private static final String CLIENT_ID = "f6a93eee";
    private static final String CLIENT_SECRET = "e0db5e4fa07768d6459f989cda046acc";
    private static final String DISCOVERY_URL = "http://discovery.sandbox.mobileconnect.io/v2/discovery";

    public String getClientId()
    {
        return CLIENT_ID;
    }

    public String getClientSecret()
    {
        return CLIENT_SECRET;
    }

    public String getDiscoveryURL()
    {
        return DISCOVERY_URL;
    }
}
