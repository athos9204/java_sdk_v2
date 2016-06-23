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

/**
 * Class to hold the response from {@link IOIDC#parseAuthenticationResponse(String, IParseAuthenticationResponseCallback)}.
 */
public class ParsedAuthorizationResponse
{
    private String error;
    private String error_description;
    private String error_uri;
    private String state;
    private String code;

    /**
     * Get the value of the error parameter.
     *
     * @return The error value.
     */
    public String get_error()
    {
        return error;
    }

    /**
     * Set the value of the error parameter.
     *
     * @param error The error value.
     */
    public void set_error(String error)
    {
        this.error = error;
    }

    /**
     * The value of the error_description parameter.
     *
     * @return The error_description value.
     */
    public String get_error_description()
    {
        return error_description;
    }

    /**
     * Set the value of the error_description parameter.
     *
     * @param error_description The error_description value.
     */
    public void set_error_description(String error_description)
    {
        this.error_description = error_description;
    }

    /**
     * Get the value of the error_uri parameter.
     *
     * @return The error_uri value.
     */
    public String get_error_uri()
    {
        return error_uri;
    }

    /**
     * Set the value of the error_uri parameter.
     *
     * @param error_uri The error_uri value.
     */
    public void set_error_uri(String error_uri)
    {
        this.error_uri = error_uri;
    }

    /**
     * Get the value of the state parameter.
     *
     * @return The state value.
     */
    public String get_state()
    {
        return state;
    }

    /**
     * Set the value of the state parameter.
     *
     * @param state The state value.
     */
    public void set_state(String state)
    {
        this.state = state;
    }

    /**
     * Get the value of the code parameter.
     *
     * @return The code value.
     */
    public String get_code()
    {
        return code;
    }

    /**
     * Set the value of the code parameter.
     *
     * @param code The code value.
     */
    public void set_code(String code)
    {
        this.code = code;
    }
}
