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

public class AuthenticationOptions
{
    /**
     * Default value of the display property.
     */
    private static final String DEFAULT_DISPLAY = "page";

    /**
     * The default value of the timeout property.
     */
    private static final int DEFAULT_TIMEOUT = 300000;

    /**
     * The default value of the screen mode property.
     */
    private static final String DEFAULT_SCREEN_MODE = "overlay";

    private String display;

    private String prompt;

    private String uiLocales;

    private String claimsLocales;

    private String idTokenHint;

    private String loginHint;

    private String dtbs;

    private int timeout;

    private String screenMode;

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
     *
     * @param uiLocales Preferred languages list.
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
     *
     * @param dtbs The data to be signed.
     */
    public void setDtbs(String dtbs)
    {
        this.dtbs = dtbs;
    }

    /**
     * The timeout applied by the SDK to network requests.
     *
     * @return The timeout of network requests.
     */
    public int getTimeout()
    {
        return timeout;
    }
    /**
     * The timeout applied by the SDK to network requests.
     *
     * @param timeout The timeout of network requests.
     */
    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    public String getScreenMode()
    {
        return screenMode;
    }

    public void setScreenMode(String screenMode)
    {
        this.screenMode = screenMode;
    }

    public AuthenticationOptions()
    {
        this.display = DEFAULT_DISPLAY;
        this.timeout = DEFAULT_TIMEOUT;
        this.screenMode = DEFAULT_SCREEN_MODE;
    }
}
