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

import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.utils.IBuilder;
import com.gsma.mobileconnect.r2.utils.ObjectUtils;
import com.gsma.mobileconnect.r2.utils.StringUtils;
import com.gsma.mobileconnect.r2.utils.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Concrete implementation of {@link ICache} using a ConcurrentHashMap as the internal
 * caching mechanism.
 *
 * @since 2.0
 */
public class ConcurrentCache extends AbstractCache
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentCache.class);
    private final ConcurrentHashMap<String, CacheEntry> cache =
        new ConcurrentHashMap<String, CacheEntry>();

    private ConcurrentCache(final Builder builder)
    {
        super(builder.jsonService, builder.cacheExpiryLimits);

        LOGGER.info("New instance of ConcurrentCache created");
    }

    @Override
    public boolean isEmpty()
    {
        final boolean empty = this.cache.isEmpty();

        LOGGER.debug("Cache isEmpty={}", empty);

        return empty;
    }

    @Override
    public void clear()
    {
        LOGGER.debug("Clearing entire cache");

        this.cache.clear();
    }

    @Override
    public void remove(final String key)
    {
        if (key != null)
        {
            LOGGER.debug("Removing key={} from cache", key);

            this.cache.remove(key);
        }
    }

    @Override
    protected void internalAdd(final String key, final CacheEntry value)
    {
        StringUtils.requireNonEmpty(key, "key");
        ObjectUtils.requireNonNull(value, "value");

        LOGGER.debug("Adding key={}, class={} to cache", key, value.getCachedClass());

        this.cache.put(key, value);
    }

    @Override
    protected CacheEntry internalGet(final String key)
    {
        StringUtils.requireNonEmpty(key, "key");

        final CacheEntry cacheEntry = this.cache.get(key);

        if (cacheEntry != null)
        {
            LOGGER.debug("Fetched key={}, class={} from cache", key, cacheEntry.getCachedClass());
        }
        else
        {
            LOGGER.info("Item with key={} is not held in the cache", key);
        }

        return cacheEntry;
    }

    @Override
    protected void internalRemove(final String key, final String value)
    {
        StringUtils.requireNonEmpty(key, "key");
        ObjectUtils.requireNonNull(value, "value");

        final CacheEntry cacheEntry = this.internalGet(key);
        if (value.equals(cacheEntry.getValue()))
        {
            LOGGER.debug("Removed key={}, class={} from cache", key, cacheEntry.getCachedClass());
            this.cache.remove(key, cacheEntry);
        }
        else
        {
            LOGGER.info("Item with key={} was not removed from cache as value did not match");
        }
    }

    public static final class Builder implements IBuilder<ICache>
    {
        private IJsonService jsonService;
        private Map<Class<? extends AbstractCacheable>, Tuple<Long, Long>> cacheExpiryLimits =
            DEFAULT_CACHE_EXPIRY_LIMITS;

        public Builder withJsonService(final IJsonService val)
        {
            this.jsonService = val;
            return this;
        }

        public Builder withCacheExpiryLimits(
            final Map<Class<? extends AbstractCacheable>, Tuple<Long, Long>> val)
        {
            ObjectUtils.requireNonNull(val, "val");

            this.cacheExpiryLimits = Collections.unmodifiableMap(
                new HashMap<Class<? extends AbstractCacheable>, Tuple<Long, Long>>(val));
            return this;
        }

        @Override
        public ConcurrentCache build()
        {
            ObjectUtils.requireNonNull(this.jsonService, "jsonService");

            return new ConcurrentCache(this);
        }
    }
}
