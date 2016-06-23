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

public class Constants
{

    public static final String ERROR_DESCRIPTION_NAME = "error_description";
    public static final String ERROR_DESCRIPTION_ALT_NAME = "description";
    public static final String ERROR_NAME = "error";
    public static final String ERROR_URI_NAME = "error_uri";

    public static final String ACR_VALUES_PARAMETER_NAME = "acr_values";
    public static final String CLAIMS_LOCALES_PARAMETER_NAME = "claims_locales";
    public static final String CLIENT_ID_PARAMETER_NAME = "client_id";
    public static final String CODE_PARAMETER_NAME = "code";
    public static final String DISPLAY_PARAMETER_NAME = "display";
    public static final String DTBS_PARAMETER_NAME = "dtbs";
    public static final String GRANT_TYPE_PARAMETER_NAME = "grant_type";
    public static final String GRANT_TYPE_PARAMETER_VALUE = "authorization_code";
    public static final String ID_TOKEN_HINT_PARAMETER_NAME = "id_token_hint";
    public static final String IDENTIFIED_MCC_PARAMETER_NAME = "Identified-MCC";
    public static final String IDENTIFIED_MNC_PARAMETER_NAME = "Identified-MNC";
    public static final String LOCAL_CLIENT_IP_PARAMETER_NAME = "Local-Client-IP";
    public static final String LOGIN_HINT_PARAMETER_NAME = "login_hint";
    public static final String MANUALLY_SELECT_PARAMETER_NAME = "Manually-Select";
    public static final String MAX_AGE_PARAMETER_NAME = "max-age";
    public static final String MCC_MNC_PARAMETER_NAME = "mcc_mnc";
    public static final String NONCE_PARAMETER_NAME = "nonce";
    public static final String PROMPT_PARAMETER_NAME = "prompt";
    public static final String RESPONSE_TYPE_PARAMETER_NAME = "response_type";
    public static final String RESPONSE_TYPE_PARAMETER_VALUE = "code";
    public static final String REDIRECT_URI_PARAMETER_NAME = "redirect_uri";
    public static final String REDIRECT_URL_PARAMETER_NAME = "Redirect_URL";
    public static final String SELECTED_MCC_PARAMETER_NAME = "Selected-MCC";
    public static final String SELECTED_MNC_PARAMETER_NAME = "Selected-MNC";
    public static final String SCOPE_PARAMETER_NAME = "scope";
    public static final String STATE_PARAMETER_NAME = "state";
    public static final String SUBSCRIBER_ID_PARAMETER_NAME = "subscriber_id";
    public static final String UI_LOCALES_PARAMETER_NAME = "ui-locales";
    public static final String USING_MOBILE_DATA_PARAMETER_NAME = "Using-Mobile-Data";

    public static final String ACCEPT_HEADER_NAME = "Accept";
    public static final String ACCEPT_JSON_HEADER_VALUE = "application/json";
    public static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    public static final String CONTENT_TYPE_HEADER_VALUE = "application/x-www-form-urlencoded";
    public static final String X_SOURCE_IP_HEADER_NAME = "X-Source-IP";

    public static final String MOST_RECENT_SELECTED_OPERATOR_EXPIRY_COOKIE_NAME = "Most-Recent-Selected-Operator-Expiry";
    public static final String MOST_RECENT_SELECTED_OPERATOR_COOKIE_NAME = "Most-Recent-Selected-Operator";
    public static final String ENUM_NONCE_COOKIE_NAME = "Enum-Nonce";

    public static final String DEFAULT_REDIRECT_URL = "http://localhost/";
    public static final String DEFAULT_SCOPE_VALUE = "openid";
    public static final int DEFAULT_MAXAGE_VALUE = 3600;
    public static final String DEFAULT_ACRVALUES_VALUE = "2";

    public static final String ACCESS_TOKEN_FIELD_NAME = "access_token";
    public static final String ALG_FIELD_NAME = "alg";
    public static final String APIS_FIELD_NAME = "apis";
    public static final String CLIENT_ID_FIELD_NAME = "client_id";
    public static final String CLIENT_SECRET_FIELD_NAME = "client_secret";
    public static final String EXPIRES_IN_FIELD_NAME = "expires_in";
    public static final String HREF_FIELD_NAME = "href";
    public static final String ID_TOKEN_FIELD_NAME = "id_token";
    public static final String LINK_FIELD_NAME = "link";
    public static final String LINKS_FIELD_NAME = "links";
    public static final String OPERATORID_FIELD_NAME = "operatorid";
    public static final String REFRESH_TOKEN_FIELD_NAME = "refresh_token";
    public static final String REL_FIELD_NAME = "rel";
    public static final String RESPONSE_FIELD_NAME = "response";
    public static final String SUBSCRIBER_ID_FIELD_NAME = "subscriber_id";
    public static final String TOKEN_TYPE_FIELD_NAME = "token_type";
    public static final String TTL_FIELD_NAME = "ttl";
    public static final String TYP_FIELD_NAME = "typ";

    public static final String AUTHORIZATION_REL = "authorization";
    public static final String USER_INFO_REL = "userinfo";
    public static final String PREMIUM_INFO_REL = "premiuminfo";
    public static final String OPERATOR_SELECTION_REL = "operatorSelection";
    public static final String TOKEN_REL = "token";

    public static final String NONCE_CLAIM_KEY = "nonce";
    public static final String SUB_CLAIM_KEY = "sub";

    public static final String ENCRYPTED_MSISDN_PREFIX = "ENCR_MSISDN:";

    public static final String INTERNAL_ERROR_CODE = "internal error";

    public static final int OPERATOR_IDENTIFIED_RESPONSE = 200;
    public static final int OPERATOR_NOT_IDENTIFIED_RESPONSE = 202;

    public static final long MINIMUM_TTL_MS = 5*60*1000; // 5 minutes
    public static final long MAXIMUM_TTL_MS = 180L*24*60*60*1000; // 180 days

    public static final String HTTP_SCHEME = "http";
    public static final int HTTP_PORT = 80;
    public static final String HTTPS_SCHEME = "https";
    public static final int HTTPS_PORT = 443;
}
