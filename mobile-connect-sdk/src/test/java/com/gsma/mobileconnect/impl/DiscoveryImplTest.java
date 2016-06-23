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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsma.mobileconnect.cache.DiscoveryCacheKey;
import com.gsma.mobileconnect.cache.DiscoveryCacheValue;
import com.gsma.mobileconnect.cache.IDiscoveryCache;
import com.gsma.mobileconnect.discovery.CacheOptions;
import com.gsma.mobileconnect.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.discovery.IDiscovery;
import com.gsma.mobileconnect.utils.Constants;
import com.gsma.mobileconnect.utils.ErrorResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Date;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class DiscoveryImplTest
{
    static private final String OPERATOR_SELECTION_URL = "http://discovery.sandbox.mobileconnect.io/v2/discovery/users/operator-selection?session_id=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyZWRpcmVjdFVybCI6Imh0dHA6Ly9sb2NhbGhvc3QvY2FsbGJhY2suaHRtbCIsInVzZXIiOnsibmFtZSI6IjBjOWRmMjE5IiwicGFzcyI6IjA5NzA5NzcwNWZhNDMxMzVjN2E2YmJjYWVkZTRmMDQxIn0sImlhdCI6MTQ1MjE4NjU4Nn0.WRAB3-Fk2YkvkbBfCKZwpLDIxxWsf5CBTILiu5nS0wk&operator=undefined";
    static private final String OPERATOR_NOT_IDENTIFIED_JSON_STRING = "{\n" +
            "   \"links\":[{\n" +
            "      \"rel\":\"operatorSelection\",\n" +
            "      \"href\":\"" + OPERATOR_SELECTION_URL + "\"\n" +
            "   }]\n" +
            "}";

    static private final String OPERATOR_IDENTIFIED_JSON_STRING =
            "{\n" +
                    "   \"ttl\":1452186985151,\n" +
                    "   \"response\":{\n" +
                    "      \"serving_operator\":\"Example Operator A\",\n" +
                    "      \"country\":\"US\",\n" +
                    "      \"currency\":\"USD\",\n" +
                    "      \"apis\":{\n" +
                    "         \"operatorid\":{\n" +
                    "            \"link\":[{\n" +
                    "               \"href\":\"http://operator_a.sandbox.mobileconnect.io/oidc/authorize\",\n" +
                    "               \"rel\":\"authorization\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "               \"href\":\"http://operator_a.sandbox.mobileconnect.io/oidc/accesstoken\",\n" +
                    "               \"rel\":\"token\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "               \"href\":\"http://operator_a.sandbox.mobileconnect.io/oidc/userinfo\",\n" +
                    "               \"rel\":\"userinfo\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "               \"href\":\"openid profile email\",\n" +
                    "               \"rel\":\"scope\"\n" +
                    "            }]\n" +
                    "         }\n" +
                    "      },\n" +
                    "      \"client_id\":\"0c9df219\",\n" +
                    "      \"client_secret\":\"097097705fa43135c7a6bbcaede4f041\"\n" +
                    "   }\n" +
                    "}";

    final static private String ERROR_STRING = "invalid_request";
    final static private String ERROR_DESCRIPTION_STRING = "the server could not understand the client's request";
    final static private String ERROR_JSON_STRING = "{\n" +
            "   \"error\":\"" + ERROR_STRING + "\",\n" +
            "   \"error_description\":\"" + ERROR_DESCRIPTION_STRING + "\"\n" +
            "}";

    static private final long MIN_TTL_VALUE = 5*60*1000;
    static private final long MAX_TTL_VALUE = 180L*24*60*60*1000;
    static private final long ALLOWED_TTL_DIFFERENCE = 1000;

    static private final String IDENTIFIED_MCC_STRING = "901";
    static private final String IDENTIFIED_MNC_STRING = "01";
    static private final String IDENTIFIED_JSON_STR = "{\"type\":\"identified\"}";

    static private final String SELECTED_MCC_STRING = "902";
    static private final String SELECTED_MNC_STRING = "02";
    static private final String SELECTED_JSON_STR = "{\"type\":\"selected\"}";

    @Rule
    final public ExpectedException thrown = ExpectedException.none();

    @Test
    public void determineTtl_withNullValue_shouldReturnMinValue()
    {
        // GIVEN
        DiscoveryImpl discovery = new DiscoveryImpl(null, null);

        // WHEN
        Date ttl = discovery.determineTtl(null);

        // THEN
        assertTrue(isMinTtl(ttl));
    }

    @Test
    public void determineTtl_withValueSmallerThanMinValue_shouldReturnMinValue()
    {
        // GIVEN
        Long ttlValue = new Date().getTime() + 1000;
        DiscoveryImpl discovery = new DiscoveryImpl(null, null);

        // WHEN
        Date ttl = discovery.determineTtl(ttlValue);

        // THEN
        assertTrue(isMinTtl(ttl));
    }

    @Test
    public void determineTtl_withValueGreaterThanMaxValue_shouldReturnMaxValue()
    {
        // GIVEN
        Long ttlValue = Long.MAX_VALUE;
        DiscoveryImpl discovery = new DiscoveryImpl(null, null);

        // WHEN
        Date ttl = discovery.determineTtl(ttlValue);

        // THEN
        assertTrue(isMaxTtl(ttl));
    }

    @Test
    public void determineTtl_withValidValue_shouldReturnValueUnchanged()
    {
        // GIVEN
        Long ttlValue = new Date().getTime() + MIN_TTL_VALUE + 1000;
        DiscoveryImpl discovery = new DiscoveryImpl(null, null);

        // WHEN
        Date ttl = discovery.determineTtl(ttlValue);

        // THEN
        assertEquals(ttlValue.longValue(), ttl.getTime());
    }

    @Test
    public void isOperatorSelectionRequired_withMissingDiscoveryResult_shouldThrowException()
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null, null);

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("discoveryResult"));

        // WHEN
        discovery.isOperatorSelectionRequired(null);
    }

    @Test
    public void isOperatorSelectionRequired_withOperatorNotIdentifiedResponse_shouldReturnTrue() throws IOException
    {
        // GIVEN
        JsonNode jsonObject = parseJson(OPERATOR_NOT_IDENTIFIED_JSON_STRING);
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(), 0, null, jsonObject);
        IDiscovery discovery = Factory.getDiscovery(null, null);

        // WHEN
        boolean isOperatorSelectionRequired = discovery.isOperatorSelectionRequired(discoveryResponse);

        // THEN
        assertTrue(isOperatorSelectionRequired);
    }

    @Test
    public void isOperatorSelectionRequired_withOperatorIdentifiedResponse_shouldReturnFalse() throws IOException
    {
        // GIVEN
        JsonNode jsonObject = parseJson(OPERATOR_IDENTIFIED_JSON_STRING);
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(), 0, null, jsonObject);
        IDiscovery discovery = Factory.getDiscovery(null, null);

        // WHEN
        boolean isOperatorSelectionRequired = discovery.isOperatorSelectionRequired(discoveryResponse);

        // THEN
        assertFalse(isOperatorSelectionRequired);
    }

    @Test
    public void isOperatorSelectionRequired_withErrorResponse_shouldReturnFalse() throws IOException
    {
        // GIVEN
        JsonNode jsonObject = parseJson(ERROR_JSON_STRING);
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(), 0, null, jsonObject);
        IDiscovery discovery = Factory.getDiscovery(null, null);

        // WHEN
        boolean isOperatorSelectionRequired = discovery.isOperatorSelectionRequired(discoveryResponse);

        // THEN
        assertFalse(isOperatorSelectionRequired);
    }

    @Test
    public void isOperatorSelectionRequired_withInvalidResponseCode_shouldThrowException() throws IOException
    {
        // GIVEN
        JsonNode jsonObject = parseJson(ERROR_JSON_STRING);
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(false, new Date(), 404, null, jsonObject);
        IDiscovery discovery = Factory.getDiscovery(null, null);

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("discoveryResult"));

        // WHEN
        discovery.isOperatorSelectionRequired(discoveryResponse);
    }

    @Test
    public void extractOperatorSelectionURL_withNoDiscoveryResponse_shouldReturnNull()
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null, null);

        // WHEN
        String url = discovery.extractOperatorSelectionURL(null);

        // THEN
        assertNull(url);
    }

    @Test
    public void extractOperatorSelectionURL_withOperatorNotIdentifiedResponse_shouldReturnExpectedURL() throws IOException
    {
        // GIVEN
        JsonNode jsonObject = parseJson(OPERATOR_NOT_IDENTIFIED_JSON_STRING);
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(), 0, null, jsonObject);
        IDiscovery discovery = Factory.getDiscovery(null, null);

        // WHEN
        String url = discovery.extractOperatorSelectionURL(discoveryResponse);

        // THEN
        assertEquals(OPERATOR_SELECTION_URL, url);
    }

    @Test
    public void isErrorResponse_withNullDiscoveryResponse_shouldReturnFalse()
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null, null);

        // WHEN
        boolean isErrorResponse = discovery.isErrorResponse(null);

        // THEN
        assertFalse(isErrorResponse);
    }

    @Test
    public void isErrorResponse_withErrorResponse_shouldReturnTrue() throws IOException
    {
        // GIVEN
        JsonNode jsonObject = parseJson(ERROR_JSON_STRING);
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(), 0, null, jsonObject);
        IDiscovery discovery = Factory.getDiscovery(null, null);

        // WHEN
        boolean isErrorResponse = discovery.isErrorResponse(discoveryResponse);

        // THEN
        assertTrue(isErrorResponse);
    }

    @Test
    public void isErrorResponse_withOperatorIdentifiedResponse_shouldReturnFalse() throws IOException
    {
        // GIVEN
        JsonNode jsonObject = parseJson(OPERATOR_IDENTIFIED_JSON_STRING);
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(), 0, null, jsonObject);
        IDiscovery discovery = Factory.getDiscovery(null, null);

        // WHEN
        boolean isErrorResponse = discovery.isErrorResponse(discoveryResponse);

        // THEN
        assertFalse(isErrorResponse);
    }

    @Test
    public void isErrorResponse_withOperatorNotIdentifiedResponse_shouldReturnFalse() throws IOException
    {
        // GIVEN
        JsonNode jsonObject = parseJson(OPERATOR_NOT_IDENTIFIED_JSON_STRING);
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(), 0, null, jsonObject);
        IDiscovery discovery = Factory.getDiscovery(null, null);

        // WHEN
        boolean isErrorResponse = discovery.isErrorResponse(discoveryResponse);

        // THEN
        assertFalse(isErrorResponse);
    }

    @Test
    public void getErrorResponse_withNullDiscoveryResponse_shouldReturnNull()
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null, null);

        // WHEN
        ErrorResponse errorResponse = discovery.getErrorResponse(null);

        // THEN
        assertNull(errorResponse);
    }

    @Test
    public void getErrorResponse_withErrorResponse_shouldReturnExpectedErrorResponse() throws IOException
    {
        // GIVEN
        JsonNode jsonObject = parseJson(ERROR_JSON_STRING);
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(), 0, null, jsonObject);
        IDiscovery discovery = Factory.getDiscovery(null, null);

        // WHEN
        ErrorResponse errorResponse = discovery.getErrorResponse(discoveryResponse);

        // THEN
        assertNotNull(errorResponse);
        assertEquals(ERROR_STRING, errorResponse.get_error());
        assertEquals(ERROR_DESCRIPTION_STRING, errorResponse.get_error_description());
    }

    @Test
    public void getCachedValue_withNoCache_shouldReturnNull()
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null, null);

        // WHEN
        DiscoveryResponse discoveryResponse = discovery.getCachedDiscoveryResult(IDENTIFIED_MCC_STRING, IDENTIFIED_MNC_STRING);

        // THEN
        assertNull(discoveryResponse);
    }

    @Test
    public void getCachedValue_withCachedDetails_shouldReturnExpectedEntry() throws IOException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(buildDiscoveryCache(), null);

        // WHEN
        DiscoveryResponse cachedValue = discovery.getCachedDiscoveryResult(IDENTIFIED_MCC_STRING, IDENTIFIED_MNC_STRING);

        // THEN
        assertNotNull(cachedValue);
        assertEquals(parseJson(IDENTIFIED_JSON_STR), cachedValue.getResponseData());
    }

    @Test
    public void getCachedValue_withCachedSelectedDetails_shouldReturnExpectedEntry() throws IOException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(buildDiscoveryCache(), null);

        // WHEN
        DiscoveryResponse cachedValue = discovery.getCachedDiscoveryResult(SELECTED_MCC_STRING, SELECTED_MNC_STRING);

        // THEN
        assertNotNull(cachedValue);
        assertEquals(parseJson(SELECTED_JSON_STR), cachedValue.getResponseData());
    }

    @Test
    public void getCachedValue_withNoMCC_shouldThrowException() throws IOException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(buildDiscoveryCache(), null);

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("mcc"));

        // WHEN
        discovery.getCachedDiscoveryResult(null, "mnc");
    }

    @Test
    public void getCachedValue_withNoMNC_shouldThrowException() throws IOException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(buildDiscoveryCache(), null);

        // THEN
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(containsString("mnc"));

        // WHEN
        discovery.getCachedDiscoveryResult("mcc", null);
    }

    @Test
    public void clearDiscoveryCache_withNoCache_shouldNotFail()
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null, null);

        // WHEN
        discovery.clearDiscoveryCache(null);

        // THEN
        assertTrue(true); // expecting to reach here
    }

    @Test
    public void clearDiscoveryCache_withNoOptions_shouldEmptyCache() throws IOException
    {
        // GIVEN
        IDiscoveryCache cache = buildDiscoveryCache();
        IDiscovery discovery = Factory.getDiscovery(cache, null);

        // WHEN
        discovery.clearDiscoveryCache(null);

        // THEN
        assertTrue(cache.isEmpty());
    }

    @Test
    public void clearDiscoveryCache_withNoOptionsSpecified_shouldNotEmptyCache() throws IOException
    {
        // GIVEN
        IDiscoveryCache cache = buildDiscoveryCache();
        IDiscovery discovery = Factory.getDiscovery(cache, null);
        CacheOptions cacheOptions = new CacheOptions();

        // WHEN
        discovery.clearDiscoveryCache(cacheOptions);

        // THEN
        assertFalse(cache.isEmpty());
    }

    @Test
    public void clearDiscoveryCache_withIdentifiedOptions_shouldRemoveEntry() throws IOException
    {
        // GIVEN
        IDiscoveryCache cache = buildDiscoveryCache();
        IDiscovery discovery = Factory.getDiscovery(cache, null);
        CacheOptions cacheOptions = new CacheOptions();
        cacheOptions.setMCC(IDENTIFIED_MCC_STRING);
        cacheOptions.setMNC(IDENTIFIED_MNC_STRING);

        // WHEN
        discovery.clearDiscoveryCache(cacheOptions);
        DiscoveryResponse cachedValue = discovery.getCachedDiscoveryResult(IDENTIFIED_MCC_STRING, IDENTIFIED_MNC_STRING);

        // THEN
        assertFalse(cache.isEmpty());
        assertNull(cachedValue);
    }

    @Test
    public void clearDiscoveryCache_withSelectedOptions_shouldRemoveEntry() throws IOException
    {
        // GIVEN
        IDiscoveryCache cache = buildDiscoveryCache();
        IDiscovery discovery = Factory.getDiscovery(cache, null);
        CacheOptions cacheOptions = new CacheOptions();
        cacheOptions.setMCC(SELECTED_MCC_STRING);
        cacheOptions.setMNC(SELECTED_MNC_STRING);

        // WHEN
        discovery.clearDiscoveryCache(cacheOptions);
        DiscoveryResponse cachedValue = discovery.getCachedDiscoveryResult(SELECTED_MCC_STRING, SELECTED_MNC_STRING);

        // THEN
        assertFalse(cache.isEmpty());
        assertNull(cachedValue);
    }

    @Test
    public void addCachedValue_withNoCache_shouldNotFail() throws IOException
    {
        // GIVEN
        DiscoveryImpl discovery = (DiscoveryImpl)Factory.getDiscovery(null, null);

        DiscoveryCacheKey key = DiscoveryCacheKey.newWithDetails(IDENTIFIED_MCC_STRING, IDENTIFIED_MNC_STRING);
        DiscoveryResponse value = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), Constants.OPERATOR_IDENTIFIED_RESPONSE, null, parseJson(IDENTIFIED_JSON_STR));

        // WHEN
        discovery.addCachedValue(key, value);

        // THEN
        assertTrue(true); // expecting to reach here
    }

    @Test
    public void addCachedValue_withNoKey_shouldNotAddToCache() throws IOException
    {
        // GIVEN
        IDiscoveryCache cache = Factory.getDefaultDiscoveryCache();
        DiscoveryImpl discovery = (DiscoveryImpl)Factory.getDiscovery(cache, null);

        DiscoveryResponse value = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), Constants.OPERATOR_IDENTIFIED_RESPONSE, null, parseJson(IDENTIFIED_JSON_STR));

        // WHEN
        discovery.addCachedValue(null, value);

        // THEN
        assertTrue(cache.isEmpty());
    }

    @Test
    public void addCachedValue_withNoValue_shouldNotAddToCache()
    {
        // GIVEN
        IDiscoveryCache cache = Factory.getDefaultDiscoveryCache();
        DiscoveryImpl discovery = (DiscoveryImpl)Factory.getDiscovery(cache, null);

        DiscoveryCacheKey key = DiscoveryCacheKey.newWithDetails(IDENTIFIED_MCC_STRING, IDENTIFIED_MNC_STRING);

        // WHEN
        discovery.addCachedValue(key, null);

        // THEN
        assertTrue(cache.isEmpty());
    }

    @Test
    public void addCachedValue_withErrorResponse_shouldNotAddToCache() throws IOException
    {
        // GIVEN
        IDiscoveryCache cache = Factory.getDefaultDiscoveryCache();
        DiscoveryImpl discovery = (DiscoveryImpl)Factory.getDiscovery(cache, null);

        DiscoveryCacheKey key = DiscoveryCacheKey.newWithDetails(IDENTIFIED_MCC_STRING, IDENTIFIED_MNC_STRING);
        DiscoveryResponse value = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), 404, null, parseJson(IDENTIFIED_JSON_STR));

        // WHEN
        discovery.addCachedValue(key, value);

        // THEN
        assertTrue(cache.isEmpty());
    }

    @Test
    public void addCachedValue_withNoTtl_shouldNotAddToCache() throws IOException
    {
        // GIVEN
        IDiscoveryCache cache = Factory.getDefaultDiscoveryCache();
        DiscoveryImpl discovery = (DiscoveryImpl)Factory.getDiscovery(cache, null);

        DiscoveryCacheKey key = DiscoveryCacheKey.newWithDetails(IDENTIFIED_MCC_STRING, IDENTIFIED_MNC_STRING);
        DiscoveryResponse value = new DiscoveryResponse(true, null, Constants.OPERATOR_IDENTIFIED_RESPONSE, null, parseJson(IDENTIFIED_JSON_STR));

        // WHEN
        discovery.addCachedValue(key, value);

        // THEN
        assertTrue(cache.isEmpty());
    }

    @Test
    public void addCachedValue_withNoJson_shouldNotAddToCache()
    {
        // GIVEN
        IDiscoveryCache cache = Factory.getDefaultDiscoveryCache();
        DiscoveryImpl discovery = (DiscoveryImpl)Factory.getDiscovery(cache, null);

        DiscoveryCacheKey key = DiscoveryCacheKey.newWithDetails(IDENTIFIED_MCC_STRING, IDENTIFIED_MNC_STRING);
        DiscoveryResponse value = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), Constants.OPERATOR_IDENTIFIED_RESPONSE, null, null);

        // WHEN
        discovery.addCachedValue(key, value);

        // THEN
        assertTrue(cache.isEmpty());
    }

    @Test
    public void addCachedValue_withValidDetails_shouldAddToCache() throws IOException
    {
        // GIVEN
        IDiscoveryCache cache = Factory.getDefaultDiscoveryCache();
        DiscoveryImpl discovery = (DiscoveryImpl)Factory.getDiscovery(cache, null);

        DiscoveryCacheKey key = DiscoveryCacheKey.newWithDetails(IDENTIFIED_MCC_STRING, IDENTIFIED_MNC_STRING);
        DiscoveryResponse value = new DiscoveryResponse(true, new Date(Long.MAX_VALUE), Constants.OPERATOR_IDENTIFIED_RESPONSE, null, parseJson(IDENTIFIED_JSON_STR));

        // WHEN
        discovery.addCachedValue(key, value);

        // THEN
        assertFalse(cache.isEmpty());
    }

    private boolean isMinTtl(Date ttl)
    {
        long now = new Date().getTime();

        long minTime = new Date(now + MIN_TTL_VALUE).getTime();

        return Math.abs(ttl.getTime() - minTime) < ALLOWED_TTL_DIFFERENCE;
    }

    private boolean isMaxTtl(Date ttl)
    {
        long now = new Date().getTime();

        long maxTime = new Date(now + MAX_TTL_VALUE).getTime();

        return Math.abs(ttl.getTime() - maxTime) < ALLOWED_TTL_DIFFERENCE;
    }

    private static JsonNode parseJson(String jsonStr)
        throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(jsonStr);
        if (null == root)
        {
            throw new IOException("Invalid json");
        }
        return root;
    }

    private IDiscoveryCache buildDiscoveryCache() throws IOException
    {
        IDiscoveryCache cache = Factory.getDefaultDiscoveryCache();

        DiscoveryCacheKey key = DiscoveryCacheKey.newWithDetails(IDENTIFIED_MCC_STRING, IDENTIFIED_MNC_STRING);
        DiscoveryCacheValue value = new DiscoveryCacheValue(new Date(Long.MAX_VALUE), parseJson(IDENTIFIED_JSON_STR));
        cache.add(key, value);

        key = DiscoveryCacheKey.newWithDetails(SELECTED_MCC_STRING, SELECTED_MNC_STRING);
        value = new DiscoveryCacheValue(new Date(Long.MAX_VALUE), parseJson(SELECTED_JSON_STR));
        cache.add(key, value);

        return cache;
    }
}
