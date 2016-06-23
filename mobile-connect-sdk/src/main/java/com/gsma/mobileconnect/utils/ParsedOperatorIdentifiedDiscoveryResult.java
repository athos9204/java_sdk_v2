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

/**
 * Class to hold parts of an operator identified discovery result.
 */
public class ParsedOperatorIdentifiedDiscoveryResult
{
    private String clientId;

    private String clientSecret;

    private String authorizationHref;

    private String tokenHref;

    private String userInfoHref;

    private String premiumInfoHref;

    /**
     * The application client id.
     *
     * @return The application client id.
     */
    public String getClientId()
    {
        return clientId;
    }

    /**
     * Set the application client id.
     *
     * @param clientId The application client id.
     */
    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    /**
     * The application client secret.
     *
     * @return The application client secret.
     */
    public String getClientSecret()
    {
        return clientSecret;
    }

    /**
     * Set the application client secret.
     *
     * @param clientSecret The application client secret.
     */
    public void setClientSecret(String clientSecret)
    {
        this.clientSecret = clientSecret;
    }

    /**
     * The authorization end point if present.
     *
     * @return The authorization end point or null.
     */
    public String getAuthorizationHref()
    {
        return authorizationHref;
    }

    /**
     * Set the authorization end point.
     *
     * @param authorizationHref The authorization end point.
     */
    public void setAuthorizationHref(String authorizationHref)
    {
        this.authorizationHref = authorizationHref;
    }

    /**
     * The token end point if present.
     *
     * @return The token end point or null.
     */
    public String getTokenHref()
    {
        return tokenHref;
    }

    /**
     * Set the token end point.
     *
     * @param tokenHref The token end point.
     */
    public void setTokenHref(String tokenHref)
    {
        this.tokenHref = tokenHref;
    }

    /**
     * The user info end point if present.
     *
     * @return The user info end point or null.
     */
    public String getUserInfoHref()
    {
        return userInfoHref;
    }

    /**
     * Set the user info end point
     *
     * @param userInfoHref The user info end point.
     */
    public void setUserInfoHref(String userInfoHref)
    {
        this.userInfoHref = userInfoHref;
    }

    /**
     * The premium info end point if present.
     *
     * @return The premium info end point or null.
     */
    public String getPremiumInfoHref()
    {
        return premiumInfoHref;
    }

    /**
     * Set the premium info end point
     *
     * @param premiumInfoHref The premium info end point.
     */
    public void setPremiumInfoHref(String premiumInfoHref)
    {
        this.premiumInfoHref = premiumInfoHref;
    }
}
