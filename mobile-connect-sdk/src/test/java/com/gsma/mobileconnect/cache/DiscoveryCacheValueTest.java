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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DiscoveryCacheValueTest
{
    @Rule
    final public ExpectedException thrown = ExpectedException.none();

    @Test
    public void create_withoutTtl_shouldThrowException()
    {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("ttl"));

        new DiscoveryCacheValue(null, objectMapper.createObjectNode());
    }

    @Test
    public void create_withoutValue_shouldThrowException()
    {
        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("value"));

        new DiscoveryCacheValue(new Date(Long.MAX_VALUE), null);
    }

    @Test
    public void hasExpired_whenExpired_shouldReturnTrue()
    {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        DiscoveryCacheValue value = new DiscoveryCacheValue(new Date(new Date().getTime()-1), objectMapper.createObjectNode());

        // WHEN
        boolean hasExpired = value.hasExpired();

        // THEN
        assertTrue(hasExpired);
    }

    @Test
    public void hasExpired_whenNotExpired_shouldReturnFalse()
    {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        DiscoveryCacheValue value = new DiscoveryCacheValue(new Date(new Date().getTime()+60), objectMapper.createObjectNode());

        // WHEN
        boolean hasExpired = value.hasExpired();

        // THEN
        assertFalse(hasExpired);
    }

}
