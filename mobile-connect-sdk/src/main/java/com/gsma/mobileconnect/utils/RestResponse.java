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

import java.util.List;

/**
 * Class to hold the response from making a Rest call.
 */
public class RestResponse
{
    final private String uri;
    final private int statusCode;
    final private List<KeyValuePair> headers;
    final private String response;

    private boolean jsonContent;

    /**
     * Return the uri of the request.
     *
     * @return The uri of the request
     */
    public String getUri()
    {
        return uri;
    }

    /**
     * Return the status code of the response
     *
     * @return The status code of the response
     */
    public int getStatusCode()
    {
        return statusCode;
    }

    /**
     * Return the response Http headers
     *
     * @return The response Http headers
     */
    public List<KeyValuePair> getHeaders()
    {
        return headers;
    }

    /**
     * Return the response data.
     *
     * @return The response data.
     */
    public String getResponse()
    {
        return response;
    }

    /**
     * Is the response json?
     *
     * @return True if the response is json.
     */
    public boolean isJsonContent()
    {
        return jsonContent;
    }

    public RestResponse(String uri, int statusCode, List<KeyValuePair> headers, String response)
    {
        this.uri = uri;
        this.statusCode = statusCode;
        this.headers = headers;
        this.response = response;

        if(null != headers)
        {
            for (KeyValuePair kvp : headers)
            {
                if(Constants.CONTENT_TYPE_HEADER_NAME.equalsIgnoreCase(kvp.getKey()))
                {
                    String value = kvp.getValue();
                    if(StringUtils.isNullOrEmpty(value))
                    {
                        continue;
                    }
                    jsonContent = value.contains(Constants.ACCEPT_JSON_HEADER_VALUE);
                    break;
                }
            }
        }
    }
}
