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
package com.gsma.mobileconnect.r2.constants;

/**
 * Constants for links provided from the discovery process.
 *
 * @since 2.0
 */
public interface LinkRels
{
    /**
     * Key for authorization url
     */
    String AUTHORIZATION = "authorization";

    /**
     * Key for token url
     */
    String TOKEN = "token";

    /**
     * Key for userinfo url
     */
    String USERINFO = "userinfo";

    /**
     * Key for premiuminfo url
     */
    String PREMIUMINFO = "premiuminfo";

    /**
     * Key for token revoke url
     */
    String TOKENREVOKE = "tokenrevoke";

    /**
     * Key for token refresh url
     */
    String TOKENREFRESH = "tokenrefresh";

    /**
     * Key for jwks url
     */
    String JWKS = "jwks";
    /**
     * Key for applicationShortName
     */
    String APPLICATION_SHORT_NAME = "applicationShortName";

    /**
     * Key for openid-configuration
     */
    String OPENID_CONFIGURATION = "openid-configuration";

    /**
     * Key for operator selection
     */
    String OPERATOR_SELECTION = "operatorSelection";
}
