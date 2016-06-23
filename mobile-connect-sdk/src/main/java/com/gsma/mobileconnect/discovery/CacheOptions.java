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

/**
 * Class used to identify data in the discovery response cache. See {@link IDiscovery#clearDiscoveryCache(CacheOptions)}.
 */
public class CacheOptions
{
    private String mcc;
    private String mnc;

    /**
     * The MCC.
     *
     * @return The MCC.
     */
    public String getMCC()
    {
        return mcc;
    }

    /**
     * Set the MCC.
     *
     * @param mcc New MCC value.
     */
    public void setMCC(String mcc)
    {
        this.mcc = mcc;
    }

    /**
     * The MNC
     *
     * @return The MNC
     */
    public String getMNC()
    {
        return mnc;
    }

    /**
     * Set the MNC.
     *
     * @param mnc New MNC value.
     */
    public void setMNC(String mnc)
    {
        this.mnc = mnc;
    }

    /**
     * Test if any properties have been set.
     *
     * @return True if any properties have been set, false otherwise.
     */
    public boolean hasData()
    {
        return (null != mcc || null != mnc);
    }
}
