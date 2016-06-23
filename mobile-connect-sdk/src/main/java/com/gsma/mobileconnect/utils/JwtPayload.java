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

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Class to hold a parsed Jwt payload.
 * <p>
 * The nonce and pcr (sub) values are extracted from the claims.
 */
public class JwtPayload
{
    private String nonce;
    private String sub;
    private JsonNode claims;

    /**
     * The nonce value.
     * <p>
     * May be null.
     *
     * @return The nonce.
     */
    public String get_nonce()
    {
        return nonce;
    }

    /**
     * The sub value.
     * <p>
     * May be null. Also known as pcr.
     *
     * @return The sub value.
     */
    public String get_sub()
    {
        return sub;
    }

    /**
     * The claims in the Jwt payload.
     *
     * @return The claims.
     */
    public JsonNode getClaims()
    {
        return claims;
    }

    public void setClaims(JsonNode claims)
    {
        this.claims = claims;

        nonce = JsonUtils.getOptionalStringValue(claims, Constants.NONCE_CLAIM_KEY);
        sub = JsonUtils.getOptionalStringValue(claims, Constants.SUB_CLAIM_KEY);
    }
}