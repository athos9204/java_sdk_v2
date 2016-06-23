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

import org.junit.Test;
import static org.junit.Assert.*;

public class DiscoveryCacheKeyTest
{
    @Test
    public void equals_withIdenticalObjects_shouldReturnTrue()
    {
        // GIVEN
        DiscoveryCacheKey key = DiscoveryCacheKey.newWithDetails("a", "b");

        // WHEN
        boolean isEquals = key.equals(key);

        // THEN
        assertTrue(isEquals);
    }

    @Test
    public void equals_withIdenticalValues_shouldReturnTrue()
    {
        // GIVEN
        DiscoveryCacheKey key1 = DiscoveryCacheKey.newWithDetails("a", "b");
        DiscoveryCacheKey key2 = DiscoveryCacheKey.newWithDetails("a", "b");

        // WHEN
        boolean isEquals = key1.equals(key2);

        // THEN
        assertTrue(isEquals);
        assertEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    public void equals_withDifferentObjectTypes_shouldReturnFalse()
    {
        // GIVEN
        DiscoveryCacheKey key = DiscoveryCacheKey.newWithDetails("a", "b");
        Object key2 = new Object();

        // WHEN
        boolean isEquals = key.equals(key2);

        // THEN
        assertFalse(isEquals);
    }

    @Test
    public void equals_withDifferentMCC_shouldReturnFalse()
    {
        // GIVEN
        DiscoveryCacheKey key1 = DiscoveryCacheKey.newWithDetails("a", "b");
        DiscoveryCacheKey key2 = DiscoveryCacheKey.newWithDetails("b", "b");

        // WHEN
        boolean isEquals = key1.equals(key2);

        // THEN
        assertFalse(isEquals);
        assertNotEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    public void equals_withDifferentMNC_shouldReturnFalse()
    {
        // GIVEN
        DiscoveryCacheKey key1 = DiscoveryCacheKey.newWithDetails("a", "b");
        DiscoveryCacheKey key2 = DiscoveryCacheKey.newWithDetails("a", "a");

        // WHEN
        boolean isEquals = key1.equals(key2);

        // THEN
        assertFalse(isEquals);
        assertNotEquals(key1.hashCode(), key2.hashCode());
    }
}
