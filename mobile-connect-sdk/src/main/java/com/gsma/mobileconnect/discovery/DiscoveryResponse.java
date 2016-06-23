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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsma.mobileconnect.utils.KeyValuePair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Class to hold a Discovery Response.
 * <p>
 * This potentially holds cached data as indicated by the cached property.
 */
public class DiscoveryResponse
    implements Serializable
{
    private static final long serialVersionUID = -241343415095528797L;

    final private boolean cached;

    final private int responseCode;

    final private List<KeyValuePair> headers;

    transient private JsonNode responseData;

    final private Date ttl;

    /**
     * Is the data from a local cache?
     *
     * @return True if the data is cached data, false otherwise
     */
    public boolean isCached()
    {
        return cached;
    }

    /**
     * Time to live of the response, if specified
     *
     * @return The ttl of the response
     */
    public Date getTtl()
    {
        return ttl;
    }

    /**
     * Has the response expired?
     * <p>
     * If no ttl is specified then it is assumed that the the response has not expired.
     * Otherwise compare the ttl against the current time.
     *
     * @return True if the response has expired
     */
    public boolean hasExpired()
    {
        return null != ttl && ttl.before(new Date());
    }

    /**
     * Return the Http responseCode
     *
     * @return The Http response code, 0 if cached data.
     */
    public int getResponseCode()
    {
        return responseCode;
    }

    /**
     * Return the list of Http headers in the response
     *
     * @return The response Http headers, null if cached data.
     */
    public List<KeyValuePair> getHeaders()
    {
        return headers;
    }

    /**
     * The Json discovery response.
     * <p>
     * This could be operator endpoints, operator selection or an error
     *
     * @return The response from the call to the Discovery service.
     */
    public JsonNode getResponseData()
    {
        return responseData;
    }

    public DiscoveryResponse(boolean cached, Date ttl, int responseCode, List<KeyValuePair> headers, JsonNode responseData)
    {
        this.cached = cached;
        this.ttl = ttl;
        this.responseCode = responseCode;
        this.headers = headers;
        this.responseData = responseData;
    }

    private void writeObject(ObjectOutputStream out)
            throws IOException
    {
        out.defaultWriteObject();
        if(responseData == null)
        {
            out.writeBoolean(false);
        }
        else
        {
            out.writeBoolean(true);
            new ObjectMapper().configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false).writeValue(out, responseData);
        }
    }

    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        if(in.readBoolean())
        {
            this.responseData = new ObjectMapper().configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false).readValue(in, JsonNode.class);
        }
    }
}
