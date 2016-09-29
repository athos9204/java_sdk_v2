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
 * @since 2.0
 */
public interface Parameters
{
    // Required param for discovery
     String REDIRECT_URL = "Redirect_URL";

    // Optional params for discovery
     String MANUALLY_SELECT = "Manually-Select";
     String IDENTIFIED_MCC = "Identified-MCC";
     String IDENTIFIED_MNC = "Identified-MNC";
     String SELECTED_MCC = "Selected-MCC";
     String SELECTED_MNC = "Selected-MNC";
     String USING_MOBILE_DATA = "Using-Mobile-Data";
     String LOCAL_CLIENT_IP = "Local-Client-IP";
     String MSISDN = "MSISDN";

     String MCC_MNC = "mcc_mnc";
     String SUBSCRIBER_ID = "subscriber_id";

    // Required params for authentication
     String CLIENT_ID = "client_id";
     String RESPONSE_TYPE = "response_type";
     String AUTHENTICATION_REDIRECT_URI = "redirect_uri";
     String SCOPE = "scope";
     String ACR_VALUES = "acr_values";
     String STATE = "state";
     String NONCE = "nonce";
     String VERSION = "version";

    // Optional params for authentication
     String DISPLAY = "display";
     String PROMPT = "prompt";
     String MAX_AGE = "max-age";
     String UI_LOCALES = "ui-locales";
     String CLAIMS_LOCALES = "claims_locales";
     String ID_TOKEN_HINT = "id_token_hint";
     String LOGIN_HINT = "login_hint";
     String DTBS = "dtbs";
     String CLAIMS = "claims";

    // Required params for authorization
     String CLIENT_NAME = "client_name";
     String CONTEXT = "context";
     String BINDING_MESSAGE = "binding_message";

    // Params for AuthorizationResponse
     String ERROR = "error";
     String ERROR_DESCRIPTION = "error_description";
     String DESCRIPTION = "description";
     String ERROR_URI = "error_uri";
     String CODE = "code";

    // Params for Token
     String GRANT_TYPE = "grant_type";
}
