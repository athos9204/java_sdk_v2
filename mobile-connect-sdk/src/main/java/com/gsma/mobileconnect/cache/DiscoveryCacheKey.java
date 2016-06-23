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

package com.gsma.mobileconnect.cache;

/**
 * Key for IDiscoveryCache entries.
 */
public class DiscoveryCacheKey
{
    final private String mcc;

    final private String mnc;

    /**
     * The MCC.
     *
     * @return The MCC.
     */
    String getMCC()
    {
        return mcc;
    }

    /**
     * The MNC.
     *
     * @return The MNC.
     */
    String getMNC()
    {
        return mnc;
    }

    DiscoveryCacheKey(String mcc, String mnc)
    {
        if(null == mcc || null == mnc)
        {
            throw new IllegalArgumentException("All parameters must be set.");
        }
        this.mcc = mcc;
        this.mnc = mnc;
    }

    /**
     * Create an instance of DiscoveryCacheKey
     *
     * @param mcc The MCC.
     * @param mnc The MNC.
     * @return A DiscoveryCacheKey with the operator details.
     */
    public static DiscoveryCacheKey newWithDetails(String mcc, String mnc)
    {
        if(null == mcc || null == mnc)
        {
            return null;
        }
        return new DiscoveryCacheKey(mcc, mnc);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof DiscoveryCacheKey))
        {
            return false;
        }

        DiscoveryCacheKey that = (DiscoveryCacheKey) o;

        if (mcc != null ? !mcc.equals(that.mcc) : that.mcc != null)
        {
            return false;
        }
        return mnc != null ? mnc.equals(that.mnc) : that.mnc == null;
    }

    @Override
    public int hashCode()
    {
        int result = mcc != null ? mcc.hashCode() : 0;
        result = 31 * result + (mnc != null ? mnc.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "Operator: " + mcc + ", " + mnc;
    }
}
