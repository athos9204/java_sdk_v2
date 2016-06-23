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

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Class to hold the details parsed from an id_token.
 */
public class ParsedIdToken
{
    private String id_token;

    private JsonNode id_token_claims;

    private String pcr;

    private String nonce;

    private boolean id_token_verified;

    /**
     * Get the un-parsed id_token.
     *
     * @return The id_token.
     */
    public String get_id_token()
    {
        return id_token;
    }

    /**
     * Set the un-parsed id_token.
     *
     * @param id_token The un-parsed id_token.
     */
    public void set_id_token(String id_token)
    {
        this.id_token = id_token;
    }

    /**
     * Get the Jwt claims in the token.
     *
     * @return The Jwt claims.
     */
    public JsonNode get_id_token_claims()
    {
        return id_token_claims;
    }

    /**
     * Set the Jwt claims.
     *
     * @param id_token_claims The Jwt claims.
     */
    public void set_id_token_claims(JsonNode id_token_claims)
    {
        this.id_token_claims = id_token_claims;
    }

    /**
     * The pcr (sub) value in the Jwt, map be null.
     *
     * @return The pcr value.
     */
    public String get_pcr()
    {
        return pcr;
    }

    /**
     * Set the pcr value.
     *
     * @param pcr The pcr value.
     */
    public void set_pcr(String pcr)
    {
        this.pcr = pcr;
    }

    /**
     * Has the id_token has been verified?
     *
     * @return True if the id_token has been verified.
     */
    public boolean is_id_token_verified()
    {
        return id_token_verified;
    }

    /**
     * Set whether the id_token has been verified.
     *
     * @param id_token_verified New value.
     */
    public void set_id_token_verified(boolean id_token_verified)
    {
        this.id_token_verified = id_token_verified;
    }

    /**
     * Get the nonce value from the Jwt claims.
     *
     * @return The nonce value.
     */
    public String get_nonce()
    {
        return nonce;
    }

    /**
     * Set the nonce value.
     *
     * @param nonce The nonce value.
     */
    public void set_nonce(String nonce)
    {
        this.nonce = nonce;
    }
}
