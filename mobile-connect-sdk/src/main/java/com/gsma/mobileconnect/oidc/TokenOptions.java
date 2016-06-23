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

/**
 * Class to hold the optional parameters for the {@link IOIDC#parseIDToken(DiscoveryResponse, String, TokenOptions, IParseIDTokenCallback)}
 * and {@link IOIDC#requestToken(DiscoveryResponse, String, String, TokenOptions, IRequestTokenCallback)}.
 */
public class TokenOptions
{
    /**
     * Default timeout value (ms).
     */
    private static final int DEFAULT_TIMEOUT = 30000;

    /**
     * Default value for checkIdTokenSignature.
     */
    private static final boolean DEFAULT_CHECK_ID_TOKEN_SIGNATURE = true;

    private int timeout;

    private boolean checkIdTokenSignature;

    /**
     * The Rest call timeout (ms).
     *
     * @return The timeout.
     */
    public int getTimeout()
    {
        return timeout;
    }

    /**
     * Set the Rest call timeout (ms).
     *
     * @param timeout New timeout value.
     */
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    /**
     * Should the id_token signature should be checked?
     *
     * @return True if the id_token signature should be checked.
     */
    public boolean isCheckIdTokenSignature()
    {
        return checkIdTokenSignature;
    }

    /**
     * Set whether the signature of the id_token should be checked.
     *
     * @param checkIdTokenSignature New value.
     */
    public void setCheckIdTokenSignature(boolean checkIdTokenSignature)
    {
        this.checkIdTokenSignature = checkIdTokenSignature;
    }

    public TokenOptions()
    {
        this.timeout = DEFAULT_TIMEOUT;
        this.checkIdTokenSignature = DEFAULT_CHECK_ID_TOKEN_SIGNATURE;
    }
}
