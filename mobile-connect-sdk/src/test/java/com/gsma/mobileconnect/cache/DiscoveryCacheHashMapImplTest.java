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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gsma.mobileconnect.impl.Factory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class DiscoveryCacheHashMapImplTest
{
    @Rule
    final public ExpectedException thrown = ExpectedException.none();

    @Test
    public void add_withSubscriberId_shouldRemoveSubscriberId()
    {
        // GIVEN
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

        String field = "field";
        String expectedFieldValue = "field_value";
        root.put(field, expectedFieldValue);

        String subscriberId = "subscriber_id";
        String expectedSubscriberIdValue = "subscriber_id_value";
        root.put(subscriberId, expectedSubscriberIdValue);
        DiscoveryCacheValue value = new DiscoveryCacheValue(new Date(Long.MAX_VALUE), root);

        DiscoveryCacheKey key = DiscoveryCacheKey.newWithDetails("a", "a");
        IDiscoveryCache cache = Factory.getDefaultDiscoveryCache();

        // WHEN
        cache.add(key, value);
        DiscoveryCacheValue cachedValue = cache.get(key);

        // THEN
        assertNotNull(cachedValue);
        assertNotEquals(value, cachedValue);
        assertNull(cachedValue.getValue().get(subscriberId));
        assertEquals(expectedFieldValue, cachedValue.getValue().get(field).textValue());

        // Original object should not be changed.
        assertEquals(expectedSubscriberIdValue, value.getValue().get(subscriberId).textValue());
    }

    @Test
    public void get_shouldReturnCopiesOfData()
    {
        // GIVEN
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

        String field1 = "field1";
        String expectedFieldValue1 = "value1";
        String expectedNewFieldValue1 = "new_value1";
        root.put(field1, expectedFieldValue1);

        String field2 = "field2";
        String expectedFieldValue2 = "value2";
        root.put(field2, expectedFieldValue2);

        DiscoveryCacheKey key = DiscoveryCacheKey.newWithDetails("a", "a");
        IDiscoveryCache cache = Factory.getDefaultDiscoveryCache();

        DiscoveryCacheValue value = new DiscoveryCacheValue(new Date(Long.MAX_VALUE), root);

        // WHEN
        cache.add(key, value);

        // Change the returned value
        DiscoveryCacheValue cachedValue1 = cache.get(key);
        ObjectNode cachedRoot = (ObjectNode)cachedValue1.getValue();
        cachedRoot.remove(field2);
        cachedRoot.put(field1, expectedNewFieldValue1);
        cachedValue1.getTtl().setTime(Long.MIN_VALUE);

        // Get from the cache again
        DiscoveryCacheValue cachedValue2 = cache.get(key);

        // THEN
        // cacheValue1 should reflect the changes
        assertNotNull(cachedValue1);
        assertEquals(expectedNewFieldValue1, cachedValue1.getValue().get(field1).textValue());
        assertNull(cachedValue1.getValue().get(field2));
        assertEquals(Long.MIN_VALUE, cachedValue1.getTtl().getTime());

        // cacheValue2 should not reflect the changes
        assertNotNull(cachedValue2);
        assertEquals(expectedFieldValue1, cachedValue2.getValue().get(field1).textValue());
        assertEquals(expectedFieldValue2, cachedValue2.getValue().get(field2).textValue());
        assertEquals(Long.MAX_VALUE, cachedValue2.getTtl().getTime());
    }

    @Test
    public void get_withExpiredTtl_shouldReturnNull()
    {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        DiscoveryCacheKey key = DiscoveryCacheKey.newWithDetails("a", "a");
        IDiscoveryCache cache = Factory.getDefaultDiscoveryCache();

        DiscoveryCacheValue value = new DiscoveryCacheValue( new Date(Long.MIN_VALUE), objectMapper.createObjectNode());

        // WHEN
        cache.add(key, value);
        boolean cacheIsEmptyAfterAdd = cache.isEmpty();
        DiscoveryCacheValue cachedValue = cache.get(key);
        boolean cacheIsEmptyAfterGet = cache.isEmpty();

        // THEN
        assertFalse(cacheIsEmptyAfterAdd);
        assertNull(cachedValue);
        assertTrue(cacheIsEmptyAfterGet);
    }

    @Test
    public void get_withInvalidKey_shouldReturnNull()
    {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        DiscoveryCacheKey key1 = DiscoveryCacheKey.newWithDetails("a", "a");
        DiscoveryCacheKey key2 = DiscoveryCacheKey.newWithDetails("a", "b");
        IDiscoveryCache cache = Factory.getDefaultDiscoveryCache();
        DiscoveryCacheValue value = new DiscoveryCacheValue(new Date(Long.MAX_VALUE), objectMapper.createObjectNode());
        cache.add(key1, value);

        // WHEN
        DiscoveryCacheValue cachedValue1 = cache.get(key1);
        DiscoveryCacheValue cachedValue2 = cache.get(key2);

        // THEN
        assertNotNull(cachedValue1);
        assertNull(cachedValue2);
    }

    @Test
    public void clear_shouldEmptyCache()
    {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        DiscoveryCacheKey key1 = DiscoveryCacheKey.newWithDetails("a", "a");
        DiscoveryCacheKey key2 = DiscoveryCacheKey.newWithDetails("a", "b");
        IDiscoveryCache cache = Factory.getDefaultDiscoveryCache();
        DiscoveryCacheValue value = new DiscoveryCacheValue(new Date(Long.MAX_VALUE), objectMapper.createObjectNode());
        cache.add(key1, value);
        cache.add(key2, value);

        // WHEN
        cache.clear();
        DiscoveryCacheValue cachedValue1 = cache.get(key1);
        DiscoveryCacheValue cachedValue2 = cache.get(key2);
        boolean cacheIsEmpty = cache.isEmpty();

        // THEN
        assertNull(cachedValue1);
        assertNull(cachedValue2);
        assertTrue(cacheIsEmpty);
    }

    @Test
    public void remove_withValidKey_shouldRemoveEntry()
    {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        DiscoveryCacheKey key1 = DiscoveryCacheKey.newWithDetails("a", "a");
        DiscoveryCacheKey key2 = DiscoveryCacheKey.newWithDetails("a", "b");
        IDiscoveryCache cache = Factory.getDefaultDiscoveryCache();
        DiscoveryCacheValue value = new DiscoveryCacheValue(new Date(Long.MAX_VALUE), objectMapper.createObjectNode());
        cache.add(key1, value);
        cache.add(key2, value);

        // WHEN
        cache.remove(key1);
        DiscoveryCacheValue cachedValue1 = cache.get(key1);
        DiscoveryCacheValue cachedValue2 = cache.get(key2);
        boolean cacheIsEmpty = cache.isEmpty();

        // THEN
        assertNull(cachedValue1);
        assertNotNull(cachedValue2);
        assertFalse(cacheIsEmpty);
    }

    @Test
    public void remove_withInvalidKey_shouldNotFail()
    {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        DiscoveryCacheKey key1 = DiscoveryCacheKey.newWithDetails("a", "a");
        DiscoveryCacheKey key2 = DiscoveryCacheKey.newWithDetails("a", "b");
        IDiscoveryCache cache = Factory.getDefaultDiscoveryCache();
        DiscoveryCacheValue value = new DiscoveryCacheValue(new Date(Long.MAX_VALUE), objectMapper.createObjectNode());
        cache.add(key1, value);

        // WHEN
        cache.remove(key2);
        DiscoveryCacheValue cachedValue1 = cache.get(key1);
        boolean cacheIsEmpty = cache.isEmpty();

        // THEN
        assertNotNull(cachedValue1);
        assertFalse(cacheIsEmpty);
    }

    @Test
    public void add_withNullKey_shouldThrowException()
    {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        DiscoveryCacheValue value = new DiscoveryCacheValue( new Date(Long.MAX_VALUE), objectMapper.createObjectNode());

        IDiscoveryCache cache = Factory.getDefaultDiscoveryCache();

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("Key"));

        // WHEN
        cache.add(null, value);
    }

    @Test
    public void add_withNullValue_shouldThrowException()
    {
        // GIVEN
        DiscoveryCacheKey key = DiscoveryCacheKey.newWithDetails("a", "a");
        IDiscoveryCache cache = Factory.getDefaultDiscoveryCache();

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("Value"));

        // WHEN
        cache.add(key, null);
    }
}
