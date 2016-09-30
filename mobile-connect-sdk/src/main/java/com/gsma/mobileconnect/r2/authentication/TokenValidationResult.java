/*
* SOFTWARE USE PERMISSION
*
* By downloading and accessing this software and associated documentation files ("Software") you are granted the
* unrestricted right to deal in the Software, including, without limitation the right to use, copy, modify, publish,
* sublicense and grant such rights to third parties, subject to the following conditions:
*
* The following copyright notice and this permission notice shall be included in all copies, modifications or
* substantial portions of this Software: Copyright © 2016 GSM Association.
*
* THE SOFTWARE IS PROVIDED "AS IS," WITHOUT WARRANTY OF ANY KIND, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
* MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
* ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. YOU AGREE TO
* INDEMNIFY AND HOLD HARMLESS THE AUTHORS AND COPYRIGHT HOLDERS FROM AND AGAINST ANY SUCH LIABILITY.
*/
package com.gsma.mobileconnect.r2.authentication;

/**
 * Enum for available token validation results
 *
 * @since 2.0
 */
public enum TokenValidationResult
{
    /**
     * No validation has occurred
     */
    None,

    /**
     * Token when signed does not match signature
     */
    InvalidSignature,

    /**
     * Token passed all validation steps
     */
    Valid,

    /**
     * Key was not retrieved from the jwks url or a jwks url was not present
     */
    JWKSError,

    /**
     * The alg claim in the id token header does not match the alg requested or the default alg of
     * RS256
     */
    IncorrectAlgorithm,

    /**
     * Neither the azp nor the aud claim in the id token match the client id used to make the auth
     * request
     */
    InvalidAudAndAzp,

    /**
     * The iss claim in the id token does not match the expected issuer
     */
    InvalidIssuer,

    /**
     * The IdToken has expired
     */
    IdTokenExpired,

    /**
     * No key matching the requested key id was found
     */
    NoMatchingKey,

    /**
     * Key does not contain the required information to validate against the requested algorithm
     */
    KeyMisformed,

    /**
     * Algorithm is unsupported for validation
     */
    UnsupportedAlgorithm,

    /**
     * The access token has expired
     */
    AccessTokenExpired,


    /**
     * The access token is null or empty in the token response
     */
    AccessTokenMissing,

    /**
     * The id token is null or empty in the token response
     */
    IdTokenMissing,

    /**
     * The id token is older than the max age specified in the auth stage
     */
    MaxAgePassed,

    /**
     * A longer time than the configured limit has passed since the token was issued
     */
    TokenIssueTimeLimitPassed,

    /**
     * The nonce in the id token claims does not match the nonce specified in the auth stage
     */
    InvalidNonce,

    /**
     * The token response is null or missing required data
     */
    IncompleteTokenResponse
}
