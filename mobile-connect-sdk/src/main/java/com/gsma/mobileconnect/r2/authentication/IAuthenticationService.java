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

import com.gsma.mobileconnect.r2.InvalidResponseException;
import com.gsma.mobileconnect.r2.discovery.ProviderMetadata;
import com.gsma.mobileconnect.r2.discovery.SupportedVersions;
import com.gsma.mobileconnect.r2.rest.RequestFailedException;
import com.gsma.mobileconnect.r2.rest.HeadlessOperationFailedException;

import java.net.URI;
import java.util.concurrent.Future;

/**
 * Interface for the Mobile Connect Requests
 *
 * @since 2.0
 */
public interface IAuthenticationService
{
    /**
     * Generates an authorisation url based on the supplied options and previous discovery response.
     *
     * @param clientId        The registered application ClientId (Required)
     * @param authorizeUrl    The authorization url returned by the discovery process (Required)
     * @param redirectUrl     On completion or error where the result information is sent using a
     *                        HTTP 302 redirect (Required)
     * @param state           Application specified unique scope value
     * @param nonce           Application specified nonce value. (Required)
     * @param encryptedMSISDN Encrypted MSISDN for user if returned from discovery service
     * @param versions        SupportedVersions from {@link ProviderMetadata} if null default
     *                        supported versions will be used to generate the auth url
     * @param options         Optional parameters
     */
    StartAuthenticationResponse startAuthentication(String clientId, URI authorizeUrl,
        URI redirectUrl, String state, String nonce, String encryptedMSISDN,
        SupportedVersions versions, AuthenticationOptions options);

    /**
     * Synchronous wrapper for
     * {@link IAuthenticationService#requestTokenAsync(String, String, URI, URI, String)}
     *
     * @param clientId        The registered application ClientId (Required)
     * @param clientSecret    The registered application ClientSecret (Required)
     * @param requestTokenUrl The url for token requests recieved from the discovery process
     *                        (Required)
     * @param redirectUrl     Confirms the redirectURI that the application used when the
     *                        authorization request (Required)
     * @param code            The authorization code provided to the application via the call to the
     *                        authentication/authorization API (Required)
     * @throws RequestFailedException   on failure to access endpoint.
     * @throws InvalidResponseException on failure to process response from endpoint.
     */
    RequestTokenResponse requestToken(String clientId, String clientSecret, URI requestTokenUrl,
        URI redirectUrl, String code) throws RequestFailedException, InvalidResponseException;

    /**
     * Allows an application to use the authorization code obtained from
     * authentication/authorization to obtain an access token and related information from the
     * authorization server. <p>This function requires a valid token url from the discovery process
     * and a valid code from the initial authorization call
     *
     * @param clientId        The registered application ClientId (Required)
     * @param clientSecret    The registered application ClientSecret (Required)
     * @param requestTokenUrl The url for token requests recieved from the discovery process
     *                        (Required)
     * @param redirectUrl     Confirms the redirectURI that the application used when the
     *                        authorization request (Required)
     * @param code            The authorization code provided to the application via the call to the
     *                        authentication/authorization API (Required)
     */
    Future<RequestTokenResponse> requestTokenAsync(String clientId, String clientSecret,
        URI requestTokenUrl, URI redirectUrl, String code);

    /**
     * Initiates headless authentication, if authentication is successful a token will be returned.
     * This may be a long running operation as response from the user on their authentication device
     * is required.
     *
     * @param clientId         The application ClientId returned by the discovery process
     *                         (Required)
     * @param clientSecret     The ClientSecret returned by the discovery response (Required)
     * @param authorizationUrl The authorization url returned by the discovery process (Required)
     * @param requestTokenUrl  The token url returned by the discovery process (Required)
     * @param redirectUrl      On completion or error where the result information is sent using a
     *                         HTTP 302 redirect (Required)
     * @param state            Application specified unique state value (Required)
     * @param nonce            Application specified nonce value. (Required)
     * @param encryptedMsisdn  Encrypted MSISDN for user if returned from discovery service
     * @param versions         SupportedVersions from <see cref="ProviderMetadata"/> if null default
     *                         supported versions will be used to generate the auth url
     * @param options          Optional parameters
     * @return Token if headless authentication is successful
     */
    Future<RequestTokenResponse> requestHeadlessAuthentication(String clientId, String clientSecret,
        URI authorizationUrl, URI requestTokenUrl, URI redirectUrl, String state, String nonce,
        String encryptedMsisdn, SupportedVersions versions, AuthenticationOptions options)
        throws RequestFailedException, HeadlessOperationFailedException;
}
