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

package com.gsma.mobileconnect.discovery;

import com.gsma.mobileconnect.utils.ErrorResponse;
import com.gsma.mobileconnect.utils.KeyValuePair;
import com.gsma.mobileconnect.utils.TimeoutOptions;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Interface for discovering operator end points that are used for OpenID Connect authorization.
 */
public interface IDiscovery
{
    /**
     * Allows an application to conduct discovery based on the predetermined operator/network identified operator semantics.
     * <p>
     * If the operator cannot be identified the function will return the 'operator selection' form of the response.
     * The application can then determine how to proceed i.e. open the operator selection page separately or
     * otherwise handle this.
     * <p>
     * The operator selection functionality will display a series of pages that enables the user to identify an operator,
     * the results are passed back to the current application as parameters on the redirect URL.
     * <p>
     * Valid discovery responses can be cached and this method can return cached data.
     *
     * @param clientId The registered application client id. (Required).
     * @param clientSecret The registered application client secret. (Required).
     * @param discoveryURL The URL of the discovery end point. (Required).
     * @param redirectURL The URL the operator selection functionality redirects to. (Required).
     * @param options Optional parameters. (Optional).
     * @param currentCookies List of current cookies the browser has sent. (Optional).
     * @param callback Used to capture the response. (Required).
     * @throws DiscoveryException
     */
    void startAutomatedOperatorDiscovery(String clientId, String clientSecret, String discoveryURL, String redirectURL,
                                         DiscoveryOptions options, List<KeyValuePair> currentCookies, IDiscoveryResponseCallback callback)
            throws DiscoveryException;

    /**
     * A convenience version of {@link IDiscovery#startAutomatedOperatorDiscovery(String, String, String, String, DiscoveryOptions, List, IDiscoveryResponseCallback)}
     * where the clientId, clientSecret and discoveryURL parameters are read from an IPreferences implementation.
     *
     * @param preferences Instance of IPreferences that provides clientId, clientSecret and discoveryURL. (Required).
     * @param redirectURL The URL the operator selection functionality redirects to. (Required).
     * @param options Optional parameters. (Optional).
     * @param currentCookies List of current cookies the browser has sent. (Optional).
     * @param callback Used to capture the response. (Required).
     * @throws DiscoveryException
     */
    void startAutomatedOperatorDiscovery(IPreferences preferences, String redirectURL,
                                         DiscoveryOptions options, List<KeyValuePair> currentCookies, IDiscoveryResponseCallback callback)
            throws DiscoveryException;

    /**
     * Allows an application to get the URL for the operator selection UI of the discovery service.
     * <p>
     * This will not reference the discovery result cache.
     * <p>
     *  The returned URL will contain a session id created by the discovery server. The URL must be used as-is.
     *
     * @param clientId The registered application client id. (Required).
     * @param clientSecret The registered application client secret. (Required).
     * @param discoveryURL The URL of the discovery end point. (Required).
     * @param redirectURL The URL the operator selection functionality redirects to. (Required).
     * @param options Optional parameters. (Optional).
     * @param callback Used to capture the response. (Required).
     * @throws DiscoveryException
     */
    void getOperatorSelectionURL(String clientId, String clientSecret, String discoveryURL, String redirectURL,
                                 TimeoutOptions options, IDiscoveryResponseCallback callback)
            throws DiscoveryException;

    /**
     * A convenience version of {@link IDiscovery#getOperatorSelectionURL(String, String, String, String, TimeoutOptions, IDiscoveryResponseCallback)}
     * where the clientId, clientSecret and discoveryURL parameters are read from an IPreferences implementation.
     *
     * @param preferences Instance of IPreferences that provides clientId, clientSecret and discoveryURL. (Required).
     * @param redirectURL The URL the operator selection functionality redirects to. (Required).
     * @param options Optional parameters. (Optional).
     * @param callback Used to capture the response. (Required).
     * @throws DiscoveryException
     */
    void getOperatorSelectionURL(IPreferences preferences, String redirectURL,
                                 TimeoutOptions options, IDiscoveryResponseCallback callback)
            throws DiscoveryException;

    /**
     * Allows an application to obtain parameters which have been passed within a discovery redirect URL.
     * <p>
     * The function will parse the redirectURL and parse out the components expected for discovery i.e.
     * <p>
     * <ul>
     *     <li>selectedMCC
     *     <li>selectedMNC
     *     <li>encryptedMSISDN
     * </ul>
     *
     * @param redirectURL  The URL which has been subject to redirection from the discovery service. (Required).
     * @param callback Used to capture the response. (Required).
     * @throws URISyntaxException
     */
    void parseDiscoveryRedirect(String redirectURL, IParsedDiscoveryRedirectCallback callback)
            throws URISyntaxException;

    /**
     * Allows an application to use the selected operator MCC and MNC to obtain the discovery response.
     * <p>
     * In the case there is already a discovery result in the cache and the Selected-MCC/Selected-MNC in the
     * new request are the same as relates to the discovery result for the cached result, the cached result will
     * be returned.
     * <p>
     * If the operator cannot be identified by the discovery service the function will return the 'operator selection'
     * form of the response.
     *
     * @param clientId The registered application client id. (Required).
     * @param clientSecret The registered application client secret. (Required).
     * @param discoveryURL The URL of the discovery end point. (Required).
     * @param redirectURL The URL the operator selection functionality redirects to. If not specified http://localhost is assumed. (Optional).
     * @param selectedMCC The MCC of the selected operator. (Required).
     * @param selectedMNC The MNC of the selected operator. (Required).
     * @param options Optional parameters. (Optional).
     * @param currentCookies List of current cookies the browser has sent. (Optional).
     * @param callback Used to capture the response. (Required).
     * @throws DiscoveryException
     */
    void completeSelectedOperatorDiscovery(String clientId, String clientSecret, String discoveryURL,
                                           String redirectURL, String selectedMCC, String selectedMNC,
                                           CompleteSelectedOperatorDiscoveryOptions options, List<KeyValuePair> currentCookies,
                                           IDiscoveryResponseCallback callback)
            throws DiscoveryException;

    /**
     * A convenience version of {@link IDiscovery#completeSelectedOperatorDiscovery(String, String, String, String, String, String, CompleteSelectedOperatorDiscoveryOptions, List, IDiscoveryResponseCallback)}
     * where the clientId, clientSecret and discoveryURL parameters are read from an IPreferences implementation.
     *
     * @param preferences Instance of IPreferences that provides clientId, clientSecret and discoveryURL. (Required).
     * @param redirectURL The URL the operator selection functionality redirects to. If not specified http://localhost is assumed. (Optional).
     * @param selectedMCC The MCC of the selected operator. (Required).
     * @param selectedMNC The MNC of the selected operator. (Required).
     * @param options Optional parameters. (Optional).
     * @param currentCookies List of current cookies the browser has sent. (Optional).
     * @param callback Used to capture the response. (Required).
     * @throws DiscoveryException
     */
    void completeSelectedOperatorDiscovery(IPreferences preferences,
                                           String redirectURL, String selectedMCC, String selectedMNC,
                                           CompleteSelectedOperatorDiscoveryOptions options, List<KeyValuePair> currentCookies,
                                           IDiscoveryResponseCallback callback)
            throws DiscoveryException;

    /**
     * Simple helper function which inspects the (JSON) returned from a discovery SDK call to check if
     * the operator selection UI should be displayed.
     * <p>
     * Returns true or false value. True is returned only if discoveryResult contains the operator selection URL.
     * False if discoveryResult is null or contains expired discovery data.
     *
     * @param discoveryResult The discovery response to parse. (Required)
     * @return True if the discovery response contains an operator selection URL, false otherwise.
     * @throws IllegalArgumentException Thrown if the discovery response is not recognised.
     */
    boolean isOperatorSelectionRequired(DiscoveryResponse discoveryResult);

    /**
     * Extract the operator selection URL from the discovery response.
     *
     * @param discoveryResult The discovery response to parse.
     * @return The operator selection url or null.
     */
    String extractOperatorSelectionURL(DiscoveryResponse discoveryResult);

    /**
     * Returns true if the discovery response contains an error response
     *
     * @param discoveryResult The discovery response to check
     * @return True if the discovery response contains an error object.
     */
    boolean isErrorResponse(DiscoveryResponse discoveryResult);

    /**
     * Extract the error from the Discovery response if present.
     *
     * @param discoveryResult The discovery response to parse.
     * @return The error response if any, null otherwise.
     */
    ErrorResponse getErrorResponse(DiscoveryResponse discoveryResult);

    /**
     * Simple function which retrieves (if available) from the discovery result cache a discovery result
     * which corresponds with the operator details specified.
     *
     * @param mcc The operator mcc. (Required).
     * @param mnc The operator mnc. (Required).
     * @return A cached entry if available, null otherwise.
     */
    DiscoveryResponse getCachedDiscoveryResult(String mcc, String mnc);

    /**
     * Simple function which clears any result from the discovery cache which corresponds with the various options specified.
     *
     * @param options Optional parameters, if not specified all entries are removed from the cache. (Optional).
     */
    void clearDiscoveryCache(CacheOptions options);
}
