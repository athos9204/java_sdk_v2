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
 * Interface of Mobile Connect SDK
 */
public interface IOIDC
{
    /**
     * Starts the authentication/authorization process based on the supplied options and a previously obtained discovery result.
     * <p>
     * This will form an authentication/authorization request and initiate the request.
     *
     * @param discoveryResult Result from discovery which contains the endpoints and application credentials. (Required).
     * @param redirectURI On completion or error where the result information is sent using a HTTP 302 redirect. (Required).
     * @param state Application specified unique scope value.
     * @param nonce Application specified nonce value. (Required).
     * @param scope Requested scope. (Optional). If omitted or empty defaults to the value "openid".
     * @param maxAge Requested maximum time in seconds since last user authentication. (Optional). If omitted or empty defaults to the value 3600.
     * @param acrValues Requested Authentication Context class Reference (Optional). If omitted or empty defaults to the value "2"	.
     * @param encryptedMSISDN Encrypted MSISDN for user if returned from discovery service. (Optional).
     * @param options Optional parameters. (Optional).
     * @param callback Used to capture the response. (Required).
     * @throws OIDCException
     * @throws DiscoveryResponseExpiredException Thrown if the discovery response has expired.
     */
    void startAuthentication(DiscoveryResponse discoveryResult, String redirectURI, String state, String nonce, String scope,
                             Integer maxAge, String acrValues, String encryptedMSISDN, AuthenticationOptions options,
                             IStartAuthenticationCallback callback)
        throws OIDCException, DiscoveryResponseExpiredException;

    /**
     * Allows an application to obtain parameters which have been passed within an authentication/authorization code response via a redirect URL.
     * <p>
     * The function will parse the redirectURL and parse out the components expected for authentication/authorization:
     * <ul>
     * <li>error
     * <li>error_description
     * <li>error_uri
     * <li>state
     * <li>code
     * </ul>
     *
     * @param redirectURL The URL which has been subject to redirection from the discovery service. (Required).
     * @param callback Used to capture the response. (Required).
     * @throws OIDCException
     */
    void parseAuthenticationResponse(String redirectURL, IParseAuthenticationResponseCallback callback)
        throws OIDCException;

    /**
     * Allows an application to use the authorization code obtained from authentication/authorization to obtain an access token
     * and related information from the authorization server.
     * <p>
     * It is expected the application is in possession of a valid discovery result and authentication code prior to calling
     * this function.
     *
     * @param discoveryResult Result from discovery which contains the endpoints and application credentials. (Required).
     * @param redirectURI Confirms the redirectURI that the application used when the authorization request. (Required).
     * @param code 	The authorization code provided to the application via the call to the authentication/authorization API. (Required).
     * @param options Optional parameters. (Optional).
     * @param callback Used to capture the response. (Required).
     * @throws OIDCException
     * @throws DiscoveryResponseExpiredException Thrown if the discovery response has expired.
     */
    void requestToken(DiscoveryResponse discoveryResult, String redirectURI, String code, TokenOptions options, IRequestTokenCallback callback)
        throws OIDCException, DiscoveryResponseExpiredException;

    /**
     * Allows an application to extract the various components within an ID token and have the signature validated (optionally).
     *
     * @param discoveryResult Result from discovery which contains the endpoints and application credentials. (Required).
     * @param id_token The id_token returned from a request to obtain a token from the authentication/authorization server. (Required).
     * @param options Optional parameters. (Optional).
     * @param callback Used to capture the response.
     * @throws OIDCException
     * @throws DiscoveryResponseExpiredException Thrown if the discovery response has expired.
     */
    void parseIDToken(DiscoveryResponse discoveryResult, String id_token, TokenOptions options, IParseIDTokenCallback callback)
        throws OIDCException, DiscoveryResponseExpiredException;
}
