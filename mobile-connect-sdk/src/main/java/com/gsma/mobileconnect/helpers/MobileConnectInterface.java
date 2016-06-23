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

package com.gsma.mobileconnect.helpers;

import com.gsma.mobileconnect.discovery.*;
import com.gsma.mobileconnect.oidc.*;
import com.gsma.mobileconnect.utils.ErrorResponse;
import com.gsma.mobileconnect.utils.KeyValuePair;
import com.gsma.mobileconnect.utils.MobileConnectState;
import com.gsma.mobileconnect.utils.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Class to wrap the calls to the Mobile Connect SDK.
 * <p>
 * This class assumes access to HttpServletRequest and HttpServletResponse it should be usable in most java
 * web frameworks.
 * <p>
 * It expects resources similar to the files in resources/public and resources/META-INF
 */
public class MobileConnectInterface
{
    private static final String MOBILE_CONNECT_SESSION_LOCK = "gsma:mc:session_lock";
    private static final String MOBILE_CONNECT_SESSION_KEY = "gsma:mc:session_key";
    private static final Object LOCK_OBJECT = new Object();

    private static final String X_FORWARDED_FOR_HEADER = "X-FORWARDED-FOR";
    private static final String SET_COOKIE_HEADER = "set-cookie";

    private static final int HTTP_OK = 200;
    private static final int HTTP_ACCEPTED = 202;
    private static final String INTERNAL_ERROR_CODE = "internal error";

    /**
     * This method is called to initiate the Mobile Connect process.
     * <p>
     * Optionally proxy cookies between the client and the discovery service. Data not to be sent back to the client is
     * stored in the session.
     * <p>
     * The discovery response for an identified operator is stored in the session.
     * <p>
     * The return is either an 'error', 'operator selection is required' or 'authorization can start' (the operator has been identified).
     *
     * @param discovery A discovery SDK instance
     * @param config Mobile Connect Configuration instance
     * @param servletRequest The servlet request.
     * @param servletResponse The servlet response.
     * @return A status object
     */
    public static MobileConnectStatus callMobileConnectForStartDiscovery(IDiscovery discovery, MobileConnectConfig config,
                                                                  HttpServletRequest servletRequest, HttpServletResponse servletResponse)
    {
        removeMobileConnectState(servletRequest);

        DiscoveryResponse discoveryResponse;
        try
        {
            DiscoveryOptions options = config.getDiscoveryOptions(getClientIP(servletRequest));

            List<KeyValuePair> currentCookies = getCurrentCookies(options.isCookiesEnabled(), servletRequest);

            CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();
            discovery.startAutomatedOperatorDiscovery(config, config.getDiscoveryRedirectURL(),
                    options, currentCookies, captureDiscoveryResponse);
            discoveryResponse = captureDiscoveryResponse.getDiscoveryResponse();

            proxyCookies(options.isCookiesEnabled(), captureDiscoveryResponse.getDiscoveryResponse().getHeaders(), servletResponse);
        }
        catch(DiscoveryException ex)
        {
            return MobileConnectStatus.error(INTERNAL_ERROR_CODE, "Failed to obtain operator details.", ex);
        }

        if(!discoveryResponse.isCached())
        {
            if(!isSuccessResponseCode(discoveryResponse.getResponseCode()))
            {
                ErrorResponse errorResponse = getErrorResponse(discovery, discoveryResponse);
                return MobileConnectStatus.error(errorResponse.get_error(), errorResponse.get_error_description(), discoveryResponse);
            }
        }

        // Is operator selection required?
        // The DiscoveryResponse may contain the operator endpoints in which case we can proceed to authorization with an operator.
        String operatorSelectionURL = discovery.extractOperatorSelectionURL(discoveryResponse);
        if(!StringUtils.isNullOrEmpty(operatorSelectionURL))
        {
            return MobileConnectStatus.operatorSelection(operatorSelectionURL);
        }
        else
        {
            updateMobileConnectState(servletRequest, MobileConnectState.withDiscoveryResponseAndEncryptedMSISDN(discoveryResponse, null));
            return MobileConnectStatus.startAuthorization(discoveryResponse);
        }
    }

    /**
     * This method is called to extract the response from the operator selection process and then determine what to do next.
     * <p>
     * Optionally proxy cookies between the client and the discovery service. Data not to be sent back to the client is
     * stored in the session.
     * <p>
     * The discovery response for an identified operator is stored in the session.
     * <p>
     * The return is either an 'error', 'start the discovery process again' or 'authorization can start' (the operator has been identified).
     *
     * @param discovery A discovery SDK instance
     * @param config Mobile Connect Configuration instance
     * @param servletRequest The servlet request.
     * @param servletResponse The servlet response
     * @return A status object
     */
    public static MobileConnectStatus callMobileConnectOnDiscoveryRedirect(IDiscovery discovery, MobileConnectConfig config,
                                                                    HttpServletRequest servletRequest, HttpServletResponse servletResponse)
    {
        removeMobileConnectState(servletRequest);

        CaptureParsedDiscoveryRedirect captureParsedDiscoveryRedirect = new CaptureParsedDiscoveryRedirect();
        try
        {
            discovery.parseDiscoveryRedirect(rebuildURL(servletRequest), captureParsedDiscoveryRedirect);
        }
        catch (URISyntaxException ex)
        {
            return MobileConnectStatus.error(INTERNAL_ERROR_CODE, "Cannot parse the redirect parameters.", ex);
        }

        ParsedDiscoveryRedirect parsedDiscoveryRedirect = captureParsedDiscoveryRedirect.getParsedDiscoveryRedirect();
        if(!parsedDiscoveryRedirect.hasMCCAndMNC())
        {
            // The operator has not been identified, need to start again.
            return MobileConnectStatus.startDiscovery();
        }

        DiscoveryResponse discoveryResponse;
        try
        {
            CompleteSelectedOperatorDiscoveryOptions options = config.getCompleteSelectedOperatorDiscoveryOptions();
            CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();
            List<KeyValuePair> currentCookies = getCurrentCookies(options.isCookiesEnabled(), servletRequest);

            // Obtain the discovery information for the selected operator
            discovery.completeSelectedOperatorDiscovery(config, config.getDiscoveryRedirectURL(),
                    parsedDiscoveryRedirect.getSelectedMCC(),
                    parsedDiscoveryRedirect.getSelectedMNC(),
                    options, currentCookies, captureDiscoveryResponse);
            discoveryResponse = captureDiscoveryResponse.getDiscoveryResponse();
            proxyCookies(options.isCookiesEnabled(), discoveryResponse.getHeaders(), servletResponse);
        }
        catch (DiscoveryException ex)
        {
            return MobileConnectStatus.error(INTERNAL_ERROR_CODE, "Failed to obtain operator details.", ex);
        }

        if(!discoveryResponse.isCached())
        {
            if(!isSuccessResponseCode(discoveryResponse.getResponseCode()))
            {
                ErrorResponse errorResponse = getErrorResponse(discovery, discoveryResponse);
                return MobileConnectStatus.error(errorResponse.get_error(), errorResponse.get_error_description(), discoveryResponse);
            }
        }

        if(discovery.isOperatorSelectionRequired(discoveryResponse))
        {
            return MobileConnectStatus.startDiscovery();
        }

        updateMobileConnectState(servletRequest, MobileConnectState.withDiscoveryResponseAndEncryptedMSISDN(discoveryResponse,
                parsedDiscoveryRedirect.getEncryptedMSISDN()));

        return MobileConnectStatus.startAuthorization(discoveryResponse);
    }

    /**
     * This method is called to start the operator authorization process after the operator has been identified.
     * <p>
     * The discovery response for an identified operator is expected to be found in the session.
     *
     * @param oidc An instance of the OIDC SDK
     * @param config The configuration to be used.
     * @param servletRequest The request, used to access then session
     * @return A status object encoding an error, start operator discovery or url to redirect to.
     */
    public static MobileConnectStatus callMobileConnectForStartAuthorization(IOIDC oidc, MobileConnectConfig config,
                                                                      HttpServletRequest servletRequest)
    {
        MobileConnectState mobileConnectState = getMobileConnectState(servletRequest);
        if (!hasDiscoveryResponse(mobileConnectState))
        {
            return MobileConnectStatus.startDiscovery();
        }

        AuthenticationOptions options = config.getAuthenticationOptions();

        String state = config.getAuthorizationState();
        String nonce = config.getAuthorizationNonce();
        String scope = config.getAuthorizationScope();
        Integer maxAge = config.getMaxAge();
        String acrValues = config.getAuthorizationAcr();

        mobileConnectState = updateMobileConnectState(servletRequest, MobileConnectState.mergeStateAndNonce(mobileConnectState, state, nonce));

        StartAuthenticationResponse startAuthenticationResponse;
        try
        {
            CaptureStartAuthenticationResponse captureStartAuthenticationResponse = new CaptureStartAuthenticationResponse();
            oidc.startAuthentication(mobileConnectState.getDiscoveryResponse(),
                    config.getApplicationURL(), state, nonce, scope,
                    maxAge, acrValues, mobileConnectState.getEncryptedMSISDN(), options, captureStartAuthenticationResponse);
            startAuthenticationResponse = captureStartAuthenticationResponse.getStartAuthenticationResponse();
        }
        catch (OIDCException ex)
        {
            return MobileConnectStatus.error(INTERNAL_ERROR_CODE, "Failed to start authorization.", ex);
        }
        catch(DiscoveryResponseExpiredException ex)
        {
            return MobileConnectStatus.startDiscovery();
        }

        return MobileConnectStatus.authorization(mobileConnectState.getDiscoveryResponse(), startAuthenticationResponse.getUrl(),
                startAuthenticationResponse.getScreenMode());
    }

    /**
     * This method is called via the redirect from the operator authorization page.
     * <p>
     * The values encoded in the URL are used to obtain an authorization token from the operator.
     *
     * @param oidc An instance of the OIDC SDK
     * @param config The config to be used.
     * @param servletRequest The servlet request, used to access the session.
     * @return A status object that is either an error, start discovery or complete.
     */
    public static MobileConnectStatus callMobileConnectOnAuthorizationRedirect(IOIDC oidc, MobileConnectConfig config,
                                                                        HttpServletRequest servletRequest)
    {
        MobileConnectState mobileConnectState = getMobileConnectState(servletRequest);
        if (!hasDiscoveryResponse(mobileConnectState))
        {
            return MobileConnectStatus.startDiscovery();
        }

        try
        {
            CaptureParsedAuthorizationResponse captureParsedAuthorizationResponse = new CaptureParsedAuthorizationResponse();
            oidc.parseAuthenticationResponse(rebuildURL(servletRequest), captureParsedAuthorizationResponse);
            ParsedAuthorizationResponse parsedAuthorizationResponse = captureParsedAuthorizationResponse.getParsedAuthorizationResponse();

            if(!StringUtils.isNullOrEmpty(parsedAuthorizationResponse.get_error()))
            {
                return MobileConnectStatus.error(parsedAuthorizationResponse.get_error(), parsedAuthorizationResponse.get_error_description(),
                        parsedAuthorizationResponse, null);
            }

            if(!hasMatchingState(parsedAuthorizationResponse.get_state(), mobileConnectState.getState()))
            {
                return MobileConnectStatus.error( "Invalid authentication response", "State values do not match", parsedAuthorizationResponse, null);
            }

            TokenOptions tokenOptions = config.getTokenOptions();
            CaptureRequestTokenResponse captureRequestTokenResponse = new CaptureRequestTokenResponse();
            oidc.requestToken(mobileConnectState.getDiscoveryResponse(),
                    config.getApplicationURL(),
                    parsedAuthorizationResponse.get_code(), tokenOptions, captureRequestTokenResponse);
            RequestTokenResponse requestTokenResponse = captureRequestTokenResponse.getRequestTokenResponse();

            if(!isSuccessResponseCode(requestTokenResponse.getResponseCode()))
            {
                ErrorResponse errorResponse = getErrorResponse(requestTokenResponse);
                return MobileConnectStatus.error(errorResponse.get_error(), errorResponse.get_error_description(), parsedAuthorizationResponse,
                        requestTokenResponse);
            }

            ErrorResponse errorResponse = requestTokenResponse.getErrorResponse();
            if(null != errorResponse)
            {
                return MobileConnectStatus.error(errorResponse.get_error(), errorResponse.get_error_description(), parsedAuthorizationResponse,
                        requestTokenResponse);
            }

            return MobileConnectStatus.complete(parsedAuthorizationResponse, requestTokenResponse);
        }
        catch(OIDCException ex)
        {
            return MobileConnectStatus.error("Failed to obtain a token.", "Failed to obtain an authentication token from the operator.", ex);
        }
        catch (DiscoveryResponseExpiredException ex)
        {
            return MobileConnectStatus.startDiscovery();
        }
    }

    /**
     * Generate a unique string with the optional prefix.
     *
     * @param prefix Optional prefix for the generated string.
     * @return A unique string.
     */
    public static String generateUniqueString(String prefix)
    {
        if(null == prefix)
        {
            prefix = "";
        }
        return prefix + UUID.randomUUID().toString();
    }

    /**
     * Extract an error response from a discovery response, create a generic error if the discovery response does not
     * contain an error response.
     *
     * @param discovery Implementation of the Discovery SDK.
     * @param discoveryResponse The discovery response to check.
     * @return The extracted error response, or a generic error.
     */
    static ErrorResponse getErrorResponse(IDiscovery discovery, DiscoveryResponse discoveryResponse)
    {
        ErrorResponse errorResponse = discovery.getErrorResponse(discoveryResponse);
        if(null == errorResponse)
        {
            errorResponse = new ErrorResponse();
            errorResponse.set_error(INTERNAL_ERROR_CODE);
            errorResponse.set_error_description("End point failed.");
        }
        return errorResponse;
    }

    /**
     * Extract an error response from a request token response, create a generic error if the request token response does not
     * contain an error response.
     *
     * @param requestTokenResponse The request token response to query
     * @return The extracted error or a generic error
     */
    static ErrorResponse getErrorResponse(RequestTokenResponse requestTokenResponse)
    {
        ErrorResponse errorResponse = requestTokenResponse.getErrorResponse();
        if(null == errorResponse)
        {
            errorResponse = new ErrorResponse();
            errorResponse.set_error(INTERNAL_ERROR_CODE);
            errorResponse.set_error_description("End point failed.");
        }
        return errorResponse;
    }

    /**
     * Check if the response code indicates a successful response.
     *
     * @param responseCode The response code to check.
     * @return True if the request was successful.
     */
    static boolean isSuccessResponseCode(int responseCode)
    {
        return HTTP_OK == responseCode || HTTP_ACCEPTED == responseCode;
    }

    /**
     * Test whether the mobileConnectState has a discovery response.
     *
     * @param mobileConnectState The mobile connect state to check.
     * @return True if there is a discovery response present.
     */
    static boolean hasDiscoveryResponse(MobileConnectState mobileConnectState)
    {
        return (null != mobileConnectState && null != mobileConnectState.getDiscoveryResponse());
    }

    /**
     * Rebuild the request URI.
     *
     * @param request The request.
     * @return The URI of the request.
     */
    static String rebuildURL(HttpServletRequest request)
    {
        String queryString = request.getQueryString();
        return request.getRequestURL().append("?").append(queryString).toString();
    }

    /**
     * Test whether the state values in the Authorization request and the Authorization response match.
     * <p>
     * States match if both are null or the values equal each other.
     *
     * @param responseState The state contained in the response.
     * @param requestState The state contained in the request.
     * @return True if the states match, false otherwise.
     */
    static boolean hasMatchingState(String responseState, String requestState)
    {
        if(StringUtils.isNullOrEmpty(requestState) && StringUtils.isNullOrEmpty(responseState))
        {
            return true;
        }
        else if(StringUtils.isNullOrEmpty(requestState) || StringUtils.isNullOrEmpty(responseState))
        {
            return false;
        }
        else
        {
            return requestState.equals(responseState);
        }
    }

    /**
     * Utility method to get the MobileConnectState from the session.
     *
     * @param request The request, used to access the session.
     * @return The MobileConnectSession.
     */
    static MobileConnectState getMobileConnectState(HttpServletRequest request)
    {
        synchronized (getSessionLock(request))
        {
            return (MobileConnectState) request.getSession().getAttribute(MOBILE_CONNECT_SESSION_KEY);
        }
    }

    /**
     * Utility method to update the MobileConnectState in the session.
     *
     * @param request The request, used to access the session.
     * @param mobileConnectState The new value of MobileConnectState.
     * @return The new value of MobileConnectState.
     */
    static MobileConnectState updateMobileConnectState(HttpServletRequest request, MobileConnectState mobileConnectState)
    {
        synchronized (getSessionLock(request))
        {
            request.getSession().setAttribute(MOBILE_CONNECT_SESSION_KEY, mobileConnectState);
        }
        return mobileConnectState;
    }

    /**
     * Utility method to remove the MobileConnectState from the session.
     *
     * @param request The request, used to access the session.
     */
    static void removeMobileConnectState(HttpServletRequest request)
    {
        synchronized (getSessionLock(request))
        {
            request.getSession().removeAttribute(MOBILE_CONNECT_SESSION_KEY);
        }
    }

    /**
     * Utility method to access the object used to synchronize access to the session.
     *
     * @param request The request, used access the session.
     * @return The object to synchronize on to access the session.
     */
    static Object getSessionLock(HttpServletRequest request)
    {
        Object lock = request.getSession().getAttribute(MOBILE_CONNECT_SESSION_LOCK);
        if(null == lock)
        {
            synchronized (LOCK_OBJECT)
            {
                lock = request.getSession().getAttribute(MOBILE_CONNECT_SESSION_LOCK);
                if(null == lock)
                {
                    lock = new Object();
                    request.getSession().setAttribute(MOBILE_CONNECT_SESSION_LOCK, lock);
                }
            }
        }
        return lock;
    }

    /**
     * Determine the client ip of a request.
     * <p>
     * Check for proxy header.
     *
     * @param request The request
     * @return The client ip of the request.
     */
    static String getClientIP(HttpServletRequest request)
    {
        String ipAddress = request.getHeader(X_FORWARDED_FOR_HEADER);
        if(null == ipAddress)
        {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    /**
     * Optionally extract the list of cookies in the request.
     *
     * @param isCookiesEnabled Only extract the cookies if true.
     * @param servletRequest The request to extract the cookies from.
     * @return The list of cookies in the request, or an empty list.
     */
    static List<KeyValuePair> getCurrentCookies(boolean isCookiesEnabled, HttpServletRequest servletRequest)
    {
        List<KeyValuePair> currentCookies = new ArrayList<KeyValuePair>();

        Cookie[] cookies = servletRequest.getCookies();
        if(!isCookiesEnabled || null == cookies)
        {
            return currentCookies;
        }

        for(Cookie cookie : cookies)
        {
            currentCookies.add(new KeyValuePair(cookie.getName(), cookie.getValue()));
        }

        return currentCookies;
    }

    /**
     * Optionally add cookies to the response.
     *
     * @param isCookiesEnabled Only add the cookies if this is true.
     * @param headers The list of headers containing cookies.
     * @param servletResponse The response to add the cookies to.
     */
    static void proxyCookies(boolean isCookiesEnabled, List<KeyValuePair> headers, HttpServletResponse servletResponse)
    {
        if(!isCookiesEnabled || null == headers)
        {
            return;
        }

        for(KeyValuePair header : headers)
        {
            if(SET_COOKIE_HEADER.equalsIgnoreCase(header.getKey()))
            {
                servletResponse.addHeader(header.getKey(), header.getValue());
            }
        }
    }
}
