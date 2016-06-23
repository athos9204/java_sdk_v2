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

package com.gsma.mobileconnect.utils;

import com.gsma.mobileconnect.utils.KeyValuePair;

import java.util.List;

/**
 * Class to hold data relating to an exception thrown while making a Rest call.
 * <p>
 * The various properties may not be set.
 */
public class RestException extends Exception
{
    final private String uri;
    final private int statusCode;
    final private List<KeyValuePair> headers;
    final private String contents;

    /**
     * The URI of the failed request.
     *
     * @return The URI of the failing request.
     */
    public String getUri()
    {
        return uri;
    }

    /**
     * The status code of the failed request.
     *
     * @return The http status code of the failed request.
     */
    public int getStatusCode()
    {
        return statusCode;
    }

    /**
     * The response Http headers of the failed request.
     *
     * @return The response Http headers of the failed request.
     */
    public List<KeyValuePair> getHeaders()
    {
        return headers;
    }

    /**
     * The response contents of the failed request.
     *
     * @return The response contents of the failed request.
     */
    public String getContents()
    {
        return contents;
    }

    public RestException(String message, String uri)
    {
        super(message);
        this.uri = uri;
        this.statusCode = 0;
        this.headers = null;
        this.contents = null;
    }

    public RestException(String message, String uri, Throwable ex)
    {
        super(message, ex);
        this.uri = uri;
        this.statusCode = 0;
        this.headers = null;
        this.contents = null;
    }

    public RestException(String message, RestResponse response)
    {
        super(message);
        this.uri = response.getUri();
        this.statusCode = response.getStatusCode();
        this.headers = response.getHeaders();
        this.contents = response.getResponse();
    }
}
