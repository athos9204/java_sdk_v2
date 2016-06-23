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

import com.gsma.mobileconnect.discovery.CompleteSelectedOperatorDiscoveryOptions;
import com.gsma.mobileconnect.discovery.DiscoveryOptions;
import com.gsma.mobileconnect.discovery.IPreferences;
import com.gsma.mobileconnect.oidc.AuthenticationOptions;
import com.gsma.mobileconnect.oidc.TokenOptions;
import com.gsma.mobileconnect.utils.StringUtils;

/**
 * Class to hold the configuration for Mobile Connect.
 * <p>
 * Only clientId, clientSecret, applicationURL, discoveryURL and discoveryRedirectURL
 * are required everything else is optional.
 * Typically authorization state and authorization nonce would be set
 * <p>
 * This class encapsulates all the parameters (including optional parameters) for the Mobile Connect SDK in to a single object.
 * Methods in the class can then be used to create the optional parameter objects for the methods in the SDK.
 */
public class MobileConnectConfig implements IPreferences
{
    // Required
    private String clientId;
    private String clientSecret;
    private String applicationURL;
    private String discoveryURL;
    private String discoveryRedirectURL;

    private Integer networkTimeout;

    // Discovery Options
    private Boolean manuallySelect;
    private String identifiedMCC;
    private String identifiedMNC;
    private Boolean cookiesEnabled;
    private Boolean usingMobileData;
    private String localClientIP;
    private Boolean shouldClientIPBeAddedToDiscoveryRequest;
    private String clientIP;

    // OIDC Options
    private String authorizationState;
    private String authorizationNonce;
    private String authorizationScope;
    private Integer maxAge;
    private String authorizationAcr;

    private String display;
    private String prompt;
    private String uiLocales;
    private String claimsLocales;
    private String idTokenHint;
    private String loginHint;
    private String dtbs;
    private String screenMode;
    private Integer authorizationTimeout;
    private Boolean idTokenValidationRequired;

    /**
     * Get the registered Mobile Connect client id.
     * <p>
     * Required.
     *
     * @return The client id.
     */
    public String getClientId()
    {
        return clientId;
    }

    /**
     * Set the registered Mobile Connect client id.
     * <p>
     * Required.
     *
     * @param clientId The client id.
     */
    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    /**
     * Get the registered Mobile Connect client secret.
     * <p>
     * Required.
     *
     * @return The client secret.
     */
    public String getClientSecret()
    {
        return clientSecret;
    }

    /**
     * Set the registered Mobile Connect client secret.
     * <p>
     * Required.
     *
     * @param clientSecret The client secret.
     */
    public void setClientSecret(String clientSecret)
    {
        this.clientSecret = clientSecret;
    }

    /**
     * Get the URL of the Mobile Connect Discovery Service end point.
     * <p>
     * Required
     *
     * @return The Discovery Service end point URL.
     */
    public String getDiscoveryURL()
    {
        return discoveryURL;
    }

    /**
     * Set the URL of the Mobile Connect Discovery Service end point.
     * <p>
     * Required.
     *
     * @param discoveryURL The Discovery Service end point URL.
     */
    public void setDiscoveryURL(String discoveryURL)
    {
        this.discoveryURL = discoveryURL;
    }

    /**
     * Get the registered Mobile Connect application URL.
     * <p>
     * Required.
     *
     * @return The application URL.
     */
    public String getApplicationURL()
    {
        return applicationURL;
    }

    /**
     * Set the registered Mobile Connect application URL.
     * <p>
     * Required.
     *
     * @param applicationURL The application URL.
     */
    public void setApplicationURL(String applicationURL)
    {
        this.applicationURL = applicationURL;
    }

    /**
     * The URL the Discovery Service redirects to.
     * <p>
     * Required.
     *
     * @return The URL the Operator Discovery Service is to redirect to
     */
    public String getDiscoveryRedirectURL()
    {
        return discoveryRedirectURL;
    }

    /**
     * Set the URL the Discovery Service redirects to.
     * <p>
     * Required.
     *
     * @param discoveryRedirectURL The URL the Operator Discovery Service is to redirect to.
     */
    public void setDiscoveryRedirectURL(String discoveryRedirectURL)
    {
        this.discoveryRedirectURL = discoveryRedirectURL;
    }

    /**
     * The timeout applied by the SDK to network requests.
     * <p>
     * Optional.
     *
     * @return The timeout of network requests.
     */
    public Integer getNetworkTimeout()
    {
        return networkTimeout;
    }

    /**
     * Set the timeout applied by the SDK to network requests.
     * <p>
     * Optional.
     *
     * @param timeout The timeout for network requests.
     */
    public void setNetworkTimeout(int timeout)
    {
        this.networkTimeout = timeout;
    }

    /**
     * The timeout applied by the SDK to authorization network requests.
     * <p>
     * Optional.
     *
     * @return The timeout of authorization network requests.
     */
    public Integer getAuthorizationTimeout()
    {
        return authorizationTimeout;
    }

    /**
     * Set the timeout applied by the SDK to authorization network requests.
     * <p>
     * Optional.
     *
     * @param timeout The timeout for authorization network requests.
     */
    public void setAuthorizationTimeout(int timeout)
    {
        this.authorizationTimeout = timeout;
    }

    /**
     * In case the user wishes to over-ride automated operator discovery the setting of
     * this parameter bypasses all automated mechanisms.
     * <p>
     * Optional.
     * <p>
     * Used for Operator Discovery.
     *
     * @return True if to manually select.
     */
    public Boolean isManuallySelect()
    {
        return manuallySelect;
    }

    /**
     * In case the user wishes to over-ride automated operator discovery the setting of
     * this parameter bypasses all automated mechanisms.
     * <p>
     * Optional.
     * <p>
     * Used for Operator Discovery.
     *
     * @param manuallySelect New value.
     */
    public void setManuallySelect(boolean manuallySelect)
    {
        this.manuallySelect = manuallySelect;
    }

    /**
     * Confirms the actual MCC retrieved from the device OS when available.
     * <p>
     * Optional.
     * <p>
     * Used for Operator Discovery.
     *
     * @return The identified MCC.
     */
    public String getIdentifiedMCC()
    {
        return identifiedMCC;
    }

    /**
     * Confirms the actual MCC retrieved from the device OS when available.
     * <p>
     * Optional.
     * <p>
     * Used for Operator Discovery.
     *
     * @param identifiedMCC The identified MCC.
     */
    public void setIdentifiedMCC(String identifiedMCC)
    {
        this.identifiedMCC = identifiedMCC;
    }

    /**
     * Confirms the actual MNC retrieved from the device OS when available.
     * <p>
     * Optional.
     * <p>
     * Used for Operator Discovery.
     *
     * @return The identified MNC.
     */
    public String getIdentifiedMNC()
    {
        return identifiedMNC;
    }

    /**
     * Confirms the actual MNC retrieved from the device OS when available.
     * <p>
     * Optional.
     * <p>
     * Used for Operator Discovery.
     *
     * @param identifiedMNC The identified MNC.
     */
    public void setIdentifiedMNC(String identifiedMNC)
    {
        this.identifiedMNC = identifiedMNC;
    }

    /**
     * Allows sending/storing of Discovery Service Cookies.
     * <p>
     * Optional.
     * <p>
     * Used for Operator Discovery.
     *
     * @return Whether for send/store cookies.
     */
    public Boolean isCookiesEnabled()
    {
        return cookiesEnabled;
    }

    /**
     * Allows sending/storing of Discovery Service Cookies.
     * <p>
     * Optional.
     * <p>
     * Used for Operator Discovery.
     *
     * @param cookiesEnabled Whether to send/store cookies.
     */
    public void setCookiesEnabled(boolean cookiesEnabled)
    {
        this.cookiesEnabled = cookiesEnabled;
    }

    /**
     * Set to "true" if your application is able to determine that the user is accessing the service via mobile data.
     * This tells Discovery Service to discover using the mobile-network.
     * <p>
     * Optional.
     * <p>
     * Used for Operator Discovery.
     *
     * @return True if the device is connected to the mobile data network.
     */
    public Boolean isUsingMobileData()
    {
        return usingMobileData;
    }

    /**
     * Set to "true" if your application is able to determine that the user is accessing the service via mobile data.
     * This tells Discovery Service to discover using the mobile-network.
     * <p>
     * Optional.
     * <p>
     * Used for Operator Discovery.
     *
     * @param usingMobileData Set to true if the device is connected to the mobile data network.
     */
    public void setUsingMobileData(boolean usingMobileData)
    {
        this.usingMobileData = usingMobileData;
    }

    /**
     * The current local IP address of the client application i.e. the actual IP address
     * currently allocated to the device running the application.
     * <p>
     * Optional.
     * <p>
     * Used for Operator Discovery.
     *
     * @return The IP address of the device running the application.
     */
    public String getLocalClientIP()
    {
        return localClientIP;
    }

    /**
     * The current local IP address of the client application i.e. the actual IP address
     * currently allocated to the device running the application.
     * <p>
     * Optional.
     * <p>
     * Used for Operator Discovery.
     *
     * @param localClientIP The IP address of the device running the application.
     */
    public void setLocalClientIP(String localClientIP)
    {
        this.localClientIP = localClientIP;
    }

    /**
     * Allows a server application to indicate the 'public IP address' of the connection from
     * a client application/ mobile browser to the server.
     * <p>
     * Note this will usually differ from the Local-Client-IP address, and the public IP address
     * detected by the application server should not be used for the Local-Client-IP address.
     * <p>
     * Optional.
     * <p>
     * Used for Operator Discovery.
     *
     * @return The client IP address detected by the server.
     */
    public String getClientIP()
    {
        return clientIP;
    }

    /**
     * Allows a server application to indicate the 'public IP address' of the connection from
     * a client application/ mobile browser to the server.
     * <p>
     * Note this will usually differ from the Local-Client-IP address, and the public IP address
     * detected by the application server should not be used for the Local-Client-IP address.
     * <p>
     * Optional.
     * <p>
     * Used for Operator Discovery.
     *
     * @param clientIP The client IP address detected by the server.
     */
    public void setClientIP(String clientIP)
    {
        this.clientIP = clientIP;
    }

    /**
     * Specify that the SDK should determine client IP value, see {@link MobileConnectConfig#getClientIP()}.
     * <p>
     * Optional.
     * <p>
     * Used for Operator Discovery.
     *
     * @return True if the SDK should determine the client IP value.
     */
    public Boolean isShouldClientIPBeAddedToDiscoveryRequest()
    {
        return shouldClientIPBeAddedToDiscoveryRequest;
    }

    /**
     * Specify that the SDK should determine client IP value, see {@link MobileConnectConfig#getClientIP()}.
     * <p>
     * Optional.
     * <p>
     * Used for Operator Discovery.
     *
     * @param shouldClientIPBeAddedToDiscoveryRequest Should the SDK determine the client IP value.
     */
    public void setShouldClientIPBeAddedToDiscoveryRequest(boolean shouldClientIPBeAddedToDiscoveryRequest)
    {
        this.shouldClientIPBeAddedToDiscoveryRequest = shouldClientIPBeAddedToDiscoveryRequest;
    }

    /**
     * Value used by the client to maintain state between request and callback.
     * <p>
     * A security mechanism as well, if a cryptographic binding is done with the browser cookie, to prevent Cross-Site Request Forgery.
     * <p>
     * Typically this value should be set.
     * <p>
     * Used for Authorization.
     *
     * @return The authorization state.
     */
    public String getAuthorizationState()
    {
        return authorizationState;
    }

    /**
     * Value used by the client to maintain state between request and callback.
     * <p>
     * A security mechanism as well, if a cryptographic binding is done with the browser cookie, to prevent Cross-Site Request Forgery.
     * <p>
     * Typically this value should be set.
     * <p>
     * Used for Authorization.
     *
     * @param authorizationState The authorization state.
     */
    public void setAuthorizationState(String authorizationState)
    {
        this.authorizationState = authorizationState;
    }

    /**
     * String value used to associate a client session with the ID Token.
     * <p>
     * It is passed unmodified from Authorization Request to ID Token. The value SHOULD be unique per session to mitigate
     * replay attacks.
     * <p>
     * Typically this value should be set.
     * <p>
     * Used for Authorization.
     *
     * @return The authorization nonce.
     */
    public String getAuthorizationNonce()
    {
        return authorizationNonce;
    }

    /**
     * String value used to associate a client session with the ID Token.
     * <p>
     * It is passed unmodified from Authorization Request to ID Token. The value SHOULD be unique per session to mitigate
     * replay attacks.
     * <p>
     * Typically this value should be set.
     * <p>
     * Used for Authorization.
     *
     * @param authorizationNonce The authorization nonce.
     */
    public void setAuthorizationNonce(String authorizationNonce)
    {
        this.authorizationNonce = authorizationNonce;
    }

    /**
     * Space delimited and case-sensitive list of ASCII strings for OAuth 2.0 scope values.
     * <p>
     * OIDC Authorization request MUST contain the scope value "openid".
     * The other optional values for scope in OIDC are: "profile", "email", "address", "phone" and "offline_access".
     * <p>
     * If not set a value of "openid" is assumed.
     * <p>
     * Used for Authorization.
     *
     * @return The authorization scope.
     */
    public String getAuthorizationScope()
    {
        return authorizationScope;
    }

    /**
     * Space delimited and case-sensitive list of ASCII strings for OAuth 2.0 scope values.
     * <p>
     * OIDC Authorization request MUST contain the scope value "openid".
     * The other optional values for scope in OIDC are: "profile", "email", "address", "phone" and "offline_access".
     * <p>
     * If not set a value of "openid" is assumed.
     * <p>
     * Used for Authorization.
     *
     * @param authorizationScope The authorization scope
     */
    public void setAuthorizationScope(String authorizationScope)
    {
        this.authorizationScope = authorizationScope;
    }

    /**
     * Specifies the maximum elapsed time in seconds since last authentication of the user.
     * <p>
     * If the elapsed time is greater than this value, a reauthentication MUST be done. When this parameter is
     * used in the request, the ID Token MUST contain the auth_time claim value.
     * <p>
     * Defaults to 3600 seconds.
     * <p>
     * Used for Authorization.
     *
     * @return The max age.
     */
    public Integer getMaxAge()
    {
        return maxAge;
    }

    /**
     * Specifies the maximum elapsed time in seconds since last authentication of the user.
     * <p>
     * If the elapsed time is greater than this value, a reauthentication MUST be done. When this parameter is
     * used in the request, the ID Token MUST contain the auth_time claim value.
     * <p>
     * Defaults to 3600 seconds.
     * <p>
     * Used for Authorization.
     *
     * @param maxAge The max age.
     */
    public void setMaxAge(Integer maxAge)
    {
        this.maxAge = maxAge;
    }

    /**
     * Authentication Context class Reference.
     * <p>
     * Space separated string that specifies the Authentication Context Reference to be used during authentication processing.
     * The LOA required by the RP/Client for the use case can be used here. The values appear as order of preference.
     * The acr satisfied during authentication is returned as acr claim value. The recommended values are the LOAs as
     * specified in ISO/IEC 29115 Clause 6 - 1, 2, 3, 4 - representing the LOAs of LOW, MEDIUM, HIGH and VERY HIGH.
     * <p>
     * The acr_values are indication of what authentication methods to used by the IDP. The authentication methods
     * to be used are linked to the LOA value passed in the acr_values. The IDP configures the authentication method
     * selection logic based on the acr_values.
     * <p>
     * Defaults to "2".
     * <p>
     * Used for Authorization.
     *
     * @return The authentication context class reference.
     */
    public String getAuthorizationAcr()
    {
        return authorizationAcr;
    }

    /**
     * Authentication Context class Reference.
     * <p>
     * Space separated string that specifies the Authentication Context Reference to be used during authentication processing.
     * The LOA required by the RP/Client for the use case can be used here. The values appear as order of preference.
     * The acr satisfied during authentication is returned as acr claim value. The recommended values are the LOAs as
     * specified in ISO/IEC 29115 Clause 6 - 1, 2, 3, 4 - representing the LOAs of LOW, MEDIUM, HIGH and VERY HIGH.
     * <p>
     * The acr_values are indication of what authentication methods to used by the IDP. The authentication methods
     * to be used are linked to the LOA value passed in the acr_values. The IDP configures the authentication method
     * selection logic based on the acr_values.
     * <p>
     * Defaults to "2".
     * <p>
     * Used for Authorization.
     *
     * @param authorizationAcr The authentication context class reference.
     */
    public void setAuthorizationAcr(String authorizationAcr)
    {
        this.authorizationAcr = authorizationAcr;
    }

    /**
     * ASCII String value to specify the user interface display for the Authentication and Consent flow.
     * <p>
     * The values can be:
     * <ul>
     *  <li>page: Default value, if the display parameter is not added. The UI SHOULD be consistent with a full page view
     *    of the User-Agent.
     *  <li>popup: The popup window SHOULD be 450px X 500px [wide X tall].
     *  <li>touch: The Authorization Server SHOULD display the UI consistent with a "touch" based interface.
     *  <li>wap: The UI SHOULD be consistent with a "feature-phone" device display.
     * </ul>
     * <p>
     *  Defaults to "page".
     * <p>
     * Used for Authorization.
     *
     * @return The display type to be used.
     */
    public String getDisplay()
    {
        return display;
    }

    /**
     * ASCII String value to specify the user interface display for the Authentication and Consent flow.
     * <p>
     * The values can be:
     * <ul>
     *  <li>page: Default value, if the display parameter is not added. The UI SHOULD be consistent with a full page view
     *    of the User-Agent.
     *  <li>popup: The popup window SHOULD be 450px X 500px [wide X tall].
     *  <li>touch: The Authorization Server SHOULD display the UI consistent with a "touch" based interface.
     *  <li>wap: The UI SHOULD be consistent with a "feature-phone" device display.
     * </ul>
     * <p>
     *  Defaults to "page".
     * <p>
     * Used for Authorization.
     *
     * @param display The display type to be used.
     */
    public void setDisplay(String display)
    {
        this.display = display;
    }

    /**
     * Space delimited, case-sensitive ASCII string values to specify to the Authorization Server whether to prompt or
     * not for reauthentication and consent.
     * <p>
     * The values can be:
     * <ul>
     * <li>none: MUST NOT display any UI for reauthentication or consent to the user. If the user is not authenticated already,
     *   or authentication or consent is needed to process the Authorization Request, a login_required error is returned.
     *   This can be used as a mechanism to check existing authentication or consent.
     * <li>login: SHOULD prompt the user for reauthentication or consent. In case it cannot be done, an error MUST be returned.
     * <li>consent: SHOULD display a UI to get consent from the user.
     * <li>select_account: In the situations, where the user has multiple accounts with the IDP/Authorization Server,
     *   this SHOULD prompt the user to select the account. If it cannot be done, an error MUST be returned.
     * </ul>
     * <p>
     * Optional.
     * <p>
     * Used for Authorization.
     *
     * @return The prompt value to be used.
     */
    public String getPrompt()
    {
        return prompt;
    }

    /**
     * Space delimited, case-sensitive ASCII string values to specify to the Authorization Server whether to prompt or
     * not for reauthentication and consent.
     * <p>
     * The values can be:
     * <ul>
     * <li>none: MUST NOT display any UI for reauthentication or consent to the user. If the user is not authenticated already,
     *   or authentication or consent is needed to process the Authorization Request, a login_required error is returned.
     *   This can be used as a mechanism to check existing authentication or consent.
     * <li>login: SHOULD prompt the user for reauthentication or consent. In case it cannot be done, an error MUST be returned.
     * <li>consent: SHOULD display a UI to get consent from the user.
     * <li>select_account: In the situations, where the user has multiple accounts with the IDP/Authorization Server,
     *   this SHOULD prompt the user to select the account. If it cannot be done, an error MUST be returned.
     * </ul>
     * <p>
     * Optional.
     * <p>
     * Used for Authorization.
     *
     * @param prompt The prompt value to be used.
     */
    public void setPrompt(String prompt)
    {
        this.prompt = prompt;
    }

    /**
     * Space separated list of user preferred languages and scripts for the UI as per RFC5646.
     * <p>
     * This parameter is for guidance only and in case the locales are not supported, error SHOULD NOT be returned.
     * <p>
     * Optional.
     * <p>
     * Used for Authorization.
     *
     * @return Preferred languages list.
     */
    public String getUiLocales()
    {
        return uiLocales;
    }

    /**
     * Space separated list of user preferred languages and scripts for the UI as per RFC5646.
     * <p>
     * This parameter is for guidance only and in case the locales are not supported, error SHOULD NOT be returned.
     * <p>
     * Optional.
     * <p>
     * Used for Authorization.
     *
     * @param uiLocales Preferred languages list
     */
    public void setUiLocales(String uiLocales)
    {
        this.uiLocales = uiLocales;
    }

    /**
     * Space separated list of user preferred languages and scripts for the Claims being returned as per RFC5646.
     * <p>
     * This parameter is for guidance only and in case the locales are not supported, error SHOULD NOT be returned.
     * <p>
     * Optional.
     * <p>
     * Used for Authorization.
     *
     * @return Preferred languages list.
     */
    public String getClaimsLocales()
    {
        return claimsLocales;
    }

    /**
     * Space separated list of user preferred languages and scripts for the Claims being returned as per RFC5646.
     * <p>
     * This parameter is for guidance only and in case the locales are not supported, error SHOULD NOT be returned.
     * <p>
     * Optional.
     * <p>
     * Used for Authorization.
     *
     * @param claimsLocales Preferred languages list.
     */
    public void setClaimsLocales(String claimsLocales)
    {
        this.claimsLocales = claimsLocales;
    }

    /**
     * Generally used in conjunction with prompt=none to pass the previously issued ID Token as a hint for the current
     * or past authentication session.
     * <p>
     * If the ID Token is still valid and the user is logged in then the server returns
     * a positive response, otherwise SHOULD return a login_error response. For the ID Token, the server need not be
     * listed as audience, when included in the id_token_hint.
     * <p>
     * Optional.
     * <p>
     * Used for Authorization.
     *
     * @return The token hint.
     */
    public String getIdTokenHint()
    {
        return idTokenHint;
    }

    /**
     * Generally used in conjunction with prompt=none to pass the previously issued ID Token as a hint for the current
     * or past authentication session.
     * <p>
     * If the ID Token is still valid and the user is logged in then the server returns
     * a positive response, otherwise SHOULD return a login_error response. For the ID Token, the server need not be
     * listed as audience, when included in the id_token_hint.
     * <p>
     * Optional.
     * <p>
     * Used for Authorization.
     *
     * @param idTokenHint The token hint.
     */
    public void setIdTokenHint(String idTokenHint)
    {
        this.idTokenHint = idTokenHint;
    }

    /**
     * An indication to the IDP/Authorization Server on what ID to use for login.
     * <p>
     * The login_hint can contain the MSISDN or the Encrypted MSISDN and SHOULD be tagged as MSISDN:&lt;Value&gt;
     * and ENCR_MSISDN:&lt;Value&gt; respectively - in case MSISDN or Encrypted MSISDN is passed in login_hint.
     * Encrypted MSISDN value is returned by Discovery API in the form of "subscriber_id"
     * <p>
     * If known this will default to the encrypted MSISDN value.
     * <p>
     * Used for Authorization.
     *
     * @return The login hint.
     */
    public String getLoginHint()
    {
        return loginHint;
    }

    /**
     * An indication to the IDP/Authorization Server on what ID to use for login.
     * <p>
     * The login_hint can contain the MSISDN or the Encrypted MSISDN and SHOULD be tagged as MSISDN:&lt;Value&gt;
     * and ENCR_MSISDN:&lt;Value&gt; respectively - in case MSISDN or Encrypted MSISDN is passed in login_hint.
     * Encrypted MSISDN value is returned by Discovery API in the form of "subscriber_id"
     * <p>
     * If known this will default to the encrypted MSISDN value.
     * <p>
     * Used for Authorization.
     *
     * @param loginHint The login hint.
     */
    public void setLoginHint(String loginHint)
    {
        this.loginHint = loginHint;
    }

    /**
     * Data To Be signed.
     * <p>
     * The Data/String to be signed by the private key owned by the end-user.
     * The signed data is returned in the ID Claim, as private JWT claims for this profile.
     * <p>
     * Optional.
     * <p>
     * Used for Authorization.
     *
     * @return The data to be signed.
     */
    public String getDtbs()
    {
        return dtbs;
    }

    /**
     * Data To Be signed.
     * <p>
     * The Data/String to be signed by the private key owned by the end-user.
     * The signed data is returned in the ID Claim, as private JWT claims for this profile.
     * <p>
     * Optional.
     * <p>
     * Used for Authorization.
     *
     * @param dtbs The data to be signed.
     */
    public void setDtbs(String dtbs)
    {
        this.dtbs = dtbs;
    }

    public String getScreenMode()
    {
        return screenMode;
    }

    public void setScreenMode(String screenMode)
    {
        this.screenMode = screenMode;
    }

    /**
     * Is the Jwt to be validated?
     * <p>
     * Optional.
     *
     * @return True if the Jwt is to be validated.
     */
    public Boolean isIdTokenValidationRequired()
    {
        return idTokenValidationRequired;
    }

    /**
     * Set whether Jwt is to be validated.
     * <p>
     * Optional.
     *
     * @param idTokenValidationRequired New value.
     */
    public void setIdTokenValidationRequired(Boolean idTokenValidationRequired)
    {
        this.idTokenValidationRequired = idTokenValidationRequired;
    }

    /**
     * Build a DiscoveryOptions from the config data.
     *
     * @param overrideClientIP The client IP as determined by the server. This value is used if clientIP has not been
     *                         specified but shouldClientIPBeAddedToDiscoveryRequest has been enabled.
     * @return A DiscoveryOptions instance.
     */
    public DiscoveryOptions getDiscoveryOptions(String overrideClientIP)
    {
        DiscoveryOptions options = new DiscoveryOptions();

        if(hasValue(clientIP))
        {
            options.setClientIP(clientIP);
        }
        else if(isTrue(shouldClientIPBeAddedToDiscoveryRequest) && hasValue(overrideClientIP))
        {
            options.setClientIP(overrideClientIP);
        }
        if(hasValue(cookiesEnabled))
        {
            options.setCookiesEnabled(cookiesEnabled);
        }
        if(hasValue(identifiedMCC))
        {
            options.setIdentifiedMCC(identifiedMCC);
        }
        if(hasValue(identifiedMNC))
        {
            options.setIdentifiedMNC(identifiedMNC);
        }
        if(hasValue(localClientIP))
        {
            options.setLocalClientIP(localClientIP);
        }
        if(hasValue(manuallySelect))
        {
            options.setManuallySelect(manuallySelect);
        }
        if(hasValue(networkTimeout))
        {
            options.setTimeout(networkTimeout);
        }
        if(hasValue(usingMobileData))
        {
            options.setUsingMobileData(usingMobileData);
        }

        return options;
    }

    /**
     * Build a CompleteSelectedOperatorDiscoveryOptions instance from the config.
     *
     * @return A CompleteSelectedOperatorDiscoveryOptions instance.
     */
    public CompleteSelectedOperatorDiscoveryOptions getCompleteSelectedOperatorDiscoveryOptions()
    {
        CompleteSelectedOperatorDiscoveryOptions options = new CompleteSelectedOperatorDiscoveryOptions();
        if(hasValue(networkTimeout))
        {
            options.setTimeout(networkTimeout);
        }
        if(hasValue(cookiesEnabled))
        {
            options.setCookiesEnabled(cookiesEnabled);
        }

        return options;
    }

    /**
     * Build an AuthenticationOptions instance from the config.
     *
     * @return An AuthenticationOptions instance.
     */
    public AuthenticationOptions getAuthenticationOptions()
    {
        AuthenticationOptions authenticationOptions = new AuthenticationOptions();

        if(hasValue(uiLocales))
        {
            authenticationOptions.setUiLocales(uiLocales);
        }
        if(hasValue(screenMode))
        {
            authenticationOptions.setScreenMode(screenMode);
        }
        if(hasValue(prompt))
        {
            authenticationOptions.setPrompt(prompt);
        }
        if(hasValue(loginHint))
        {
            authenticationOptions.setLoginHint(loginHint);
        }
        if(hasValue(idTokenHint))
        {
            authenticationOptions.setIdTokenHint(idTokenHint);
        }
        if(hasValue(claimsLocales))
        {
            authenticationOptions.setClaimsLocales(claimsLocales);
        }
        if(hasValue(display))
        {
            authenticationOptions.setDisplay(display);
        }
        if(hasValue(dtbs))
        {
            authenticationOptions.setDtbs(dtbs);
        }
        if(hasValue(authorizationTimeout))
        {
            authenticationOptions.setTimeout(authorizationTimeout);
        }

        return  authenticationOptions;
    }

    /**
     * Build a TokenOptions instance from the config.
     *
     * @return A TokenOptions instance.
     */
    public TokenOptions getTokenOptions()
    {
        TokenOptions options = new TokenOptions();

        if(hasValue(idTokenValidationRequired))
        {
            options.setCheckIdTokenSignature(idTokenValidationRequired);
        }
        if(hasValue(networkTimeout))
        {
            options.setTimeout(networkTimeout);
        }

        return options;
    }

    private boolean isTrue(Boolean value)
    {
        if(hasValue(value) && value.booleanValue())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean hasValue(String str)
    {
        return !StringUtils.isNullOrEmpty(str);
    }

    private boolean hasValue(Object object)
    {
        return null != object;
    }
}
