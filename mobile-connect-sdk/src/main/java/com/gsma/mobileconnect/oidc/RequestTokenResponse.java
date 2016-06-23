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

package com.gsma.mobileconnect.oidc;

import com.gsma.mobileconnect.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.utils.ErrorResponse;
import com.gsma.mobileconnect.utils.KeyValuePair;

import java.util.List;

/**
 * Class to hold the response of {@link IOIDC#requestToken(DiscoveryResponse, String, String, TokenOptions, IRequestTokenCallback)}
 * <p>
 * Will contain either an error response or request data.
 */
public class RequestTokenResponse
{
    private int responseCode;

    private List<KeyValuePair> headers;

    private RequestTokenResponseData responseData;

    private ErrorResponse errorResponse;

    /**
     * The Http response code of the Rest call.
     *
     * @return The Http response code.
     */
    public int getResponseCode()
    {
        return responseCode;
    }

    /**
     * Set the Http response code.
     *
     * @param responseCode The Http response code.
     */
    public void setResponseCode(int responseCode)
    {
        this.responseCode = responseCode;
    }

    /**
     * The Http headers of the Rest response.
     *
     * @return The Http headers.
     */
    public List<KeyValuePair> getHeaders()
    {
        return headers;
    }

    /**
     * Set the Http headers.
     *
     * @param headers The Http headers.
     */
    public void setHeaders(List<KeyValuePair> headers)
    {
        this.headers = headers;
    }

    /**
     * The response data if the Rest call did not return an error.
     *
     * @return The RequestTokenResponseData or null.
     */
    public RequestTokenResponseData getResponseData()
    {
        return responseData;
    }

    /**
     * Set the response data.
     *
     * @param responseData The response data.
     */
    public void setResponseData(RequestTokenResponseData responseData)
    {
        this.responseData = responseData;
    }

    /**
     * Return true if there is response data.
     *
     * @return True if there is response data.
     */
    public boolean hasResponseData()
    {
        return null != responseData;
    }

    /**
     * The error response if the Rest call returned an error.
     *
     * @return The error response or null.
     */
    public ErrorResponse getErrorResponse()
    {
        return errorResponse;
    }

    /**
     * Set the error response.
     *
     * @param errorResponse The error response.
     */
    public void setErrorResponse(ErrorResponse errorResponse)
    {
        this.errorResponse = errorResponse;
    }

    /**
     * Return true if the there is an error response.
     *
     * @return True if there is an error response.
     */
    public boolean hasErrorResponse()
    {
        return null != errorResponse;
    }

}
