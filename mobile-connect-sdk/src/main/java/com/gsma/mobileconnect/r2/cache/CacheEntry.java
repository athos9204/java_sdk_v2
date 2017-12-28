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
package com.gsma.mobileconnect.r2.cache;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Wrapper for items stored in the cache.
 *
 * @since 2.0
 */
class CacheEntry
{
    private String value;
    private final Date cachedTime;
    private final Class<? extends AbstractCacheable> clazz;
    private final AtomicBoolean expired;
    /**
     * Wrap specified value for storage in the cache.
     *
     * @param value to wrap.
     */
    CacheEntry(final String value, final Class<? extends AbstractCacheable> clazz)
    {
        this.value = value;
        this.clazz = clazz;
        this.cachedTime = new Date();
        this.expired = new AtomicBoolean(false);
    }


    /**
     * @return the value held.
     */
    String getValue()
    {
        return this.value;
    }

    /**
     * @return the time the item was cached.
     */
    Date getCachedTime()
    {
        return this.cachedTime;
    }

    /**
     * @return the class type of the item cached.
     */
    Class<? extends AbstractCacheable> getCachedClass()
    {
        return this.clazz;
    }

    /**
     * @return true if this item has been marked as expired.
     */
    boolean isExpired()
    {
        return this.expired.get();
    }

    /**
     * mark this item as expired.
     */
    void expire()
    {
        this.expired.set(true);
    }
}
