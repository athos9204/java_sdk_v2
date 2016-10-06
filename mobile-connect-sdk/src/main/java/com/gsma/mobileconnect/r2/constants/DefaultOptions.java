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

import java.util.concurrent.TimeUnit;

/**
 * @since 2.0
 */
public interface DefaultOptions
{
    String MC_V1_1 = "mc_v1.1";
    String MC_V1_2 = "mc_v1.2";

    long TIMEOUT_MS = TimeUnit.SECONDS.toMillis(300L);
    boolean MANUAL_SELECT = false;
    boolean COOKIES_ENABLED = true;
    String DISPLAY = "page";
    boolean CHECK_ID_TOKEN_SIGNATURE = true;
    long MIN_TTL_MS = TimeUnit.SECONDS.toMillis(300L);
    long MAX_TTL_MS = TimeUnit.DAYS.toMillis(180L);
    String AUTHENTICATION_ACR_VALUES = "2";
    String AUTHENTICATION_SCOPE = Scope.OPENID;
    long AUTHENTICATION_MAX_AGE = TimeUnit.HOURS.toSeconds(1L);
    String AUTHENTICATION_RESPONSE_TYPE = "code";
    String AUTHENTICATION_DEFAULT_VERSION = MC_V1_1;
    String GRANT_TYPE_AUTH_CODE = "authorization_code";
    String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
    long PROVIDER_METADATA_TTL_MS = TimeUnit.SECONDS.toMillis(9L);
    String VERSION_MOBILECONNECT = MC_V1_1;
    String VERSION_MOBILECONNECTAUTHN = MC_V1_1;
    String VERSION_MOBILECONNECTAUTHZ = MC_V1_2;
    String VERSION_MOBILECONNECTIDENTITY = MC_V1_2;
    int THREAD_POOL_SIZE = 100;

    String PROMPT = "mobile";

    // Since the wait time is 5 seconds & the maximum timeout = 2 mins
    long MAX_REDIRECTS = 24;
    long WAIT_TIME = 5000L; // 5 seconds
}
