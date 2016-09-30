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

import com.gsma.mobileconnect.r2.claims.Claims;
import com.gsma.mobileconnect.r2.claims.ClaimsConstants;
import com.gsma.mobileconnect.r2.encoding.IMobileConnectEncodeDecoder;
import com.gsma.mobileconnect.r2.exceptions.MobileConnectInvalidJWKException;
import com.gsma.mobileconnect.r2.json.IJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.utils.JsonWebTokens;
import com.gsma.mobileconnect.r2.utils.ListUtils;
import com.gsma.mobileconnect.r2.utils.Predicate;
import com.gsma.mobileconnect.r2.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility methods for token validation
 *
 * @since 2.0
 */
public class TokenValidation
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenValidation.class);

    private TokenValidation()
    {
    }

    /**
     * Validates an id token against the mobile connect validation requirements, this includes
     * validation of some claims and validation of the signature
     *
     * @param idToken                     IDToken to validate
     * @param clientId                    ClientId that is validated against the aud and azp claims
     * @param issuer                      Issuer that is validated against the iss claim
     * @param nonce                       Nonce that is validated against the nonce claim
     * @param maxAge                      MaxAge that is used to validate the auth_time claim (if
     *                                    supplied)
     * @param keyset                      Keyset retrieved from the jwks url, used to validate the
     *                                    token signature
     * @param iMobileConnectEncodeDecoder
     * @return TokenValidationResult that specifies if the token is valid, or if not why it is not
     * valid
     */
    public static TokenValidationResult validateIdToken(final String idToken, final String clientId,
        final String issuer, final String nonce, final long maxAge, final JWKeyset keyset,
        final IJsonService jsonService,
        final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder)
        throws JsonDeserializationException
    {
        if (StringUtils.isNullOrEmpty(idToken))
        {
            return TokenValidationResult.IdTokenMissing;
        }

        TokenValidationResult result =
            validateIdTokenClaims(idToken, clientId, issuer, nonce, maxAge, jsonService,
                iMobileConnectEncodeDecoder);
        if (result != TokenValidationResult.Valid)
        {
            return result;
        }

        return validateIdTokenSignature(idToken, keyset, jsonService, iMobileConnectEncodeDecoder);
    }

    /**
     * Validates an id token signature by signing the id token payload and comparing the result with
     * the signature
     *
     * @param idToken                     IDToken to validate
     * @param keyset                      Keyset retrieved from the jwks url, used to validate the
     *                                    token signature. If null the token will not be validated
     *                                    and {@link TokenValidationResult#JWKSError}
     * @param jsonService                 Json service to be used deserialising strings to objects
     * @param iMobileConnectEncodeDecoder
     * @return TokenValidationResult that specifies if the token signature is valid, or if not why
     * it is not valid
     */
    public static TokenValidationResult validateIdTokenSignature(final String idToken,
        final JWKeyset keyset, final IJsonService jsonService,
        final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder)
        throws JsonDeserializationException
    {
        if (keyset == null)
        {
            return TokenValidationResult.JWKSError;
        }
        JWKey jwKeyDeserialized;
        try
        {
            jwKeyDeserialized = jsonService.deserialize(
                JsonWebTokens.Part.HEADER.decode(idToken, iMobileConnectEncodeDecoder),
                JWKey.class);
            final String alg = jwKeyDeserialized.getAlgorithm();
            final String keyId = jwKeyDeserialized.getKeyId();

            JWKey jwKey = ListUtils.firstMatch(keyset.getKeys(), new Predicate<JWKey>()
            {
                @Override
                public boolean apply(JWKey input)
                {
                    if (input.getKeyId() == null)
                    {
                        return keyId == null && (StringUtils.isNullOrEmpty(input.getAlgorithm())
                            || input.getAlgorithm().equals(alg));
                    }
                    return input.getKeyId().equals(keyId) && (StringUtils.isNullOrEmpty(
                        input.getAlgorithm()) || input.getAlgorithm().equals(alg));
                }
            });

            if (jwKey == null)
            {
                return TokenValidationResult.NoMatchingKey;
            }

            final int lastSplitIndex = idToken.lastIndexOf('.');
            if (lastSplitIndex < 0 || lastSplitIndex == idToken.length() - 1)
            {
                return TokenValidationResult.InvalidSignature;
            }

            final String dataToSign = idToken.substring(0, lastSplitIndex);
            final String signature = idToken.substring(lastSplitIndex + 1);


            return verifySignature(jwKey, dataToSign, signature, alg);
        }
        catch (JsonDeserializationException e)
        {
            LOGGER.warn("Error deserializing idToken");
            throw new JsonDeserializationException(JWKey.class, idToken, e);
        }

    }

    private static TokenValidationResult verifySignature(final JWKey jwKey, final String dataToSign,
        final String signature, final String alg)
    {
        boolean isValid;
        try
        {
            isValid = jwKey.verify(dataToSign, signature, alg);
            return isValid ? TokenValidationResult.Valid : TokenValidationResult.InvalidSignature;
        }
        catch (MobileConnectInvalidJWKException e)
        {
            LOGGER.warn("Id Token validation failed", e);
            return TokenValidationResult.KeyMisformed;
        }
        catch (InvalidKeySpecException e)
        {
            LOGGER.warn("Key specification invalid", e);
            return TokenValidationResult.KeyMisformed;
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.warn("No matching algorithm found", e);
            return TokenValidationResult.IncorrectAlgorithm;
        }
    }

    /**
     * Validates an id tokens claims using validation requirements from the mobile connect and open
     * id connect specification
     *
     * @param idToken                     IDToken to validate
     * @param clientId                    ClientId that is validated against the aud and azp claims
     * @param issuer                      Issuer that is validated against the iss claim
     * @param expectedNonce               Nonce that is validated against the nonce claim
     * @param maxAge                      MaxAge that is used to validate the auth_time claim (if
     *                                    supplied)
     * @param jsonService
     * @param iMobileConnectEncodeDecoder
     * @return TokenValidationResult that specifies if the token claims are valid, or if not why
     * they are not valid
     */
    public static TokenValidationResult validateIdTokenClaims(final String idToken,
        final String clientId, final String issuer, final String expectedNonce, final long maxAge,
        final IJsonService jsonService,
        final IMobileConnectEncodeDecoder iMobileConnectEncodeDecoder)
        throws JsonDeserializationException
    {
        String claimsJson = JsonWebTokens.Part.CLAIMS.decode(idToken, iMobileConnectEncodeDecoder);
        Claims claims = jsonService.deserialize(claimsJson, Claims.class);

        if (expectedNonce != null && !expectedNonce.equals(
            claims.get(ClaimsConstants.NONCE).getValue().toString()))
        {
            return TokenValidationResult.InvalidNonce;
        }

        if (!claims.get(ClaimsConstants.ISSUER).getValue().toString().equals(issuer))
        {
            return TokenValidationResult.InvalidIssuer;
        }

        if (!doesAudOrAzpClaimMatchClientId(claims, clientId))
        {
            return TokenValidationResult.InvalidAudAndAzp;
        }

        if (tokenHasExpired(claims))
        {
            return TokenValidationResult.IdTokenExpired;
        }

        if (maxAgeHasPassed(claims, maxAge))
        {
            return TokenValidationResult.MaxAgePassed;
        }
        return TokenValidationResult.Valid;
    }

    private static boolean maxAgeHasPassed(final Claims claims, final long maxAge)
    {
        return (Long.valueOf(claims.get(ClaimsConstants.ISSUED_AT_TIME).getValue().toString())
            * 1000) + (maxAge * 1000) < Calendar.getInstance().getTimeInMillis();
    }

    private static boolean tokenHasExpired(final Claims claims)
    {
        return (Long.valueOf(claims.get(ClaimsConstants.EXPIRED).getValue().toString()) * 1000)
            < Calendar.getInstance().getTimeInMillis();
    }

    private static boolean doesAudOrAzpClaimMatchClientId(final Claims claims,
        final String clientId)
    {
        return clientId.equals(claims.get(ClaimsConstants.AUD).getValue()) || clientId.equals(
            claims.get(ClaimsConstants.AZP).getValue());
    }

    /**
     * Validates the access token contained in the token response data
     *
     * @param tokenResponse Response data containing the access token and accompanying parameters
     * @return TokenValidationResult that specifies if the access token is valid, or if not why it
     * is not valid
     */
    public static TokenValidationResult validateAccessToken(
        final RequestTokenResponseData tokenResponse)
    {
        if (StringUtils.isNullOrEmpty(tokenResponse.getAccessToken()))
        {
            return TokenValidationResult.AccessTokenMissing;
        }

        if (tokenResponse.getExpiry() != null && tokenResponse
            .getExpiry()
            .before(new Date(Calendar.getInstance().getTimeInMillis())))
        {
            return TokenValidationResult.AccessTokenExpired;
        }
        return TokenValidationResult.Valid;
    }
}
