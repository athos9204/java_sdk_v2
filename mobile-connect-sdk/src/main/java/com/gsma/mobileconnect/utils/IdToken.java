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
 * Class to hold the parts of an id_token.
 */
public class IdToken
{
    private JwtHeader header;

    private JwtPayload payload;

    private String signature;

    /**
     * The Jwt header.
     *
     * @return The Jwt header.
     */
    public JwtHeader getHeader()
    {
        return header;
    }

    /**
     * Set the Jwt header.
     *
     * @param header The Jwt header.
     */
    public void setHeader(JwtHeader header)
    {
        this.header = header;
    }

    /**
     * The Jwt payload.
     *
     * @return The Jwt payload.
     */
    public JwtPayload getPayload()
    {
        return payload;
    }

    /**
     * Set the Jwt payload
     *
     * @param payload The Jwt payload.
     */
    public void setPayload(JwtPayload payload)
    {
        this.payload = payload;
    }

    /**
     * The signature.
     *
     * @return Ths signature.
     */
    public String getSignature()
    {
        return signature;
    }

    /**
     * Set the signature.
     *
     * @param signature The signature.
     */
    public void setSignature(String signature)
    {
        this.signature = signature;
    }
}
