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

/**
 * Class to hold a Rest error response
 */
public class ErrorResponse
{
    private String error;
    private String error_description;
    private String error_uri;

    /**
     * The error code.
     *
     * @return The error code.
     */
    public String get_error()
    {
        return error;
    }

    /**
     * Set the error code.
     *
     * @param error The error code.
     */
    public void set_error(String error)
    {
        this.error = error;
    }

    /**
     * The error description.
     *
     * @return The error description.
     */
    public String get_error_description()
    {
        return error_description;
    }

    /**
     * Set the error description.
     *
     * @param error_description The error description.
     */
    public void set_error_description(String error_description)
    {
        this.error_description = error_description;
    }

    /**
     * The error uri.
     *
     * @return The error uri
     */
    public String get_error_uri()
    {
        return error_uri;
    }

    /**
     * Set the error uri
     *
     * @param error_uri The error uri
     */
    public void set_error_uri(String error_uri)
    {
        this.error_uri = error_uri;
    }
}
