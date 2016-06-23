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

import com.gsma.mobileconnect.utils.KeyValuePair;

import java.util.List;

/**
 * Class to hold discovery exceptions.
 */
public class DiscoveryException extends Exception
{
    private String uri;
    private int responseCode;
    private List<KeyValuePair> headers;
    private String contents;

    /**
     * The URI of Http request that caused the exception.
     *
     * @return The URI of Http request that caused the exception, if available, null otherwise.
     */
    public String getUri()
    {
        return uri;
    }

    /**
     * The response code of the Http response that caused the exception.
     *
     * @return The response code of the Http response that caused the exception, if available, 0 otherwise.
     */
    public int getResponseCode()
    {
        return responseCode;
    }

    /**
     * The Http headers of the Http response that caused the exception.
     *
     * @return The Http headers of the Http response that caused the exception, if available, null otherwise.
     */
    public List<KeyValuePair> getHeaders()
    {
        return headers;
    }

    /**
     * The contents of the Http response that caused the exception.
     *
     * @return The contents of the Http response that caused the exception, if available, null otherwise.
     */
    public String getContents()
    {
        return contents;
    }

    public DiscoveryException(String message)
    {
        super(message);
    }

    public DiscoveryException(String message, Throwable nestedException)
    {
        super(message, nestedException);
    }

    public DiscoveryException(String message, String uri, int responseCode, List<KeyValuePair> headers, String contents, Throwable nestedException)
    {
        super(message, nestedException);
        this.uri = uri;
        this.responseCode = responseCode;
        this.headers = headers;
        this.contents = contents;
    }
}
