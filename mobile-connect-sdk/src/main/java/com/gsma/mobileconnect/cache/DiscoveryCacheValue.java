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

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;

/**
 * Value that can be stored in an instance of the IDiscoveryCache.
 */
public class DiscoveryCacheValue
{
    private Date ttl;
    private JsonNode value;

    /**
     * The time-to-live value of the entry.
     *
     * @return The time-to-live value of the entry.
     */
    public Date getTtl()
    {
        return ttl;
    }

    /**
     * The discovery response.
     *
     * @return The cached discovery response.
     */
    public JsonNode getValue()
    {
        return value;
    }

    /**
     * Create an instance of DiscoveryCacheValue.
     *
     * @param ttl The time-to-live value. (Required).
     * @param value The discovery response. (Required).
     */
    public DiscoveryCacheValue(Date ttl, JsonNode value)
    {
        if(null == ttl)
        {
            throw new IllegalArgumentException("ttl cannot be null");
        }
        if(null == value)
        {
            throw new IllegalArgumentException("value cannot be null");
        }

        this.ttl = ttl;
        this.value = value;
    }

    /**
     * Has the entry expired?
     * <p>
     * Compares the ttl value against the current time.
     *
     * @return True if the value has exceeded its ttl value, false otherwise.
     */
    public boolean hasExpired()
    {
        return ttl.before(new Date());
    }
}
