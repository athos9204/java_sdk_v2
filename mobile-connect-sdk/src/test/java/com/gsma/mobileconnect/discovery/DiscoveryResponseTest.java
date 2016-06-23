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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsma.mobileconnect.utils.KeyValuePair;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DiscoveryResponseTest
{
    @Test
    public void emptyObject_shouldSerialize() throws IOException, ClassNotFoundException
    {
        // GIVEN
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(false, null, 0, null, null);

        // WHEN
        DiscoveryResponse rereadValue = roundTripSerialize(discoveryResponse);

        // THEN
        assertEquals(rereadValue.isCached(), discoveryResponse.isCached());
        assertEquals(rereadValue.getTtl(), discoveryResponse.getTtl());
        assertEquals(rereadValue.getResponseCode(), discoveryResponse.getResponseCode());
        assertTrue(areHeadersEqual(rereadValue.getHeaders(), discoveryResponse.getHeaders()));
        assertEquals(rereadValue.getResponseData(), discoveryResponse.getResponseData());
    }

    @Test
    public void initialisedObject_shouldSerialise() throws IOException, ClassNotFoundException
    {
        // GIVEN
        DiscoveryResponse discoveryResponse = new DiscoveryResponse(true, new Date(1000), 200, buildHeaders(), buildJsonObject());

        // WHEN
        DiscoveryResponse rereadValue = roundTripSerialize(discoveryResponse);

        // THEN
        assertEquals(rereadValue.isCached(), discoveryResponse.isCached());
        assertEquals(rereadValue.getTtl(), discoveryResponse.getTtl());
        assertEquals(rereadValue.getResponseCode(), discoveryResponse.getResponseCode());
        assertTrue(areHeadersEqual(rereadValue.getHeaders(), discoveryResponse.getHeaders()));
        assertEquals(rereadValue.getResponseData(), discoveryResponse.getResponseData());
    }

    private DiscoveryResponse roundTripSerialize(DiscoveryResponse in) throws IOException, ClassNotFoundException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutput output = new ObjectOutputStream(baos);

        output.writeObject(in);

        output.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInput input = new ObjectInputStream(bais);

        DiscoveryResponse out = (DiscoveryResponse)input.readObject();

        input.close();

        return out;
    }

    private JsonNode buildJsonObject() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree("{ \"field\": \"value\" }");
    }

    private List<KeyValuePair> buildHeaders()
    {
        List<KeyValuePair> headers = new ArrayList<KeyValuePair>(2);
        headers.add(new KeyValuePair("first-expected-key", "first-expected-value"));
        headers.add(new KeyValuePair("second-expected-key", "second-expected-value"));
        return headers;
    }

    private boolean areHeadersEqual(List<KeyValuePair> first, List<KeyValuePair> second)
    {
        if(first == second)
        {
            return true;
        }
        if(first == null || second == null)
        {
            return false;
        }
        if(first.size() != second.size())
        {
            return false;
        }
        int size = first.size();
        for(int i = 0 ; i < size ; ++i)
        {
            KeyValuePair f = first.get(i);
            KeyValuePair s = second.get(i);
            if(!f.getKey().equals(s.getKey()))
            {
                return false;
            }
            if(!f.getValue().equals(s.getValue()))
            {
                return false;
            }
        }
        return true;
    }

}
