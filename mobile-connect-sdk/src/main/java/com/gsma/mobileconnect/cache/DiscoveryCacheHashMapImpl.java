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

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gsma.mobileconnect.utils.Constants;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of IDiscoveryCache that uses a ConcurrentHashMap as a backing store.
 */
public class DiscoveryCacheHashMapImpl
    implements IDiscoveryCache
{
    final private ConcurrentHashMap<DiscoveryCacheKey, DiscoveryCacheValue> cache;

    public DiscoveryCacheHashMapImpl()
    {
        cache = new ConcurrentHashMap<DiscoveryCacheKey, DiscoveryCacheValue>();
    }

    public void add(DiscoveryCacheKey key, DiscoveryCacheValue value)
    {
        validateKey(key);
        validateValue(value);

        DiscoveryCacheValue cachedValue = buildCacheValue(value);

        cache.put(key, cachedValue);
    }

    public DiscoveryCacheValue get(DiscoveryCacheKey key)
    {
        validateKey(key);

        DiscoveryCacheValue value = cache.get(key);
        if(null == value)
        {
            return null;
        }
        if(value.hasExpired())
        {
            cache.remove(key, value);
            return null;
        }

        return buildCacheValue(value);
    }

    public void remove(DiscoveryCacheKey key)
    {
        validateKey(key);

        cache.remove(key);
    }

    public void clear()
    {
        cache.clear();
    }

    public boolean isEmpty()
    {
        return cache.isEmpty();
    }

    private void validateKey(DiscoveryCacheKey key)
    {
        if(null == key)
        {
            throw new IllegalArgumentException("Key cannot be null");
        }
    }

    private void validateValue(DiscoveryCacheValue value)
    {
        if(null == value )
        {
            throw new IllegalArgumentException("Value cannot be null");
        }
    }

    private DiscoveryCacheValue buildCacheValue(DiscoveryCacheValue value)
    {
        ObjectNode copy = value.getValue().deepCopy();
        copy.remove(Constants.SUBSCRIBER_ID_FIELD_NAME);

        return new DiscoveryCacheValue(new Date(value.getTtl().getTime()), copy);
    }
}
