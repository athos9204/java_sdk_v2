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

import com.gsma.mobileconnect.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.oidc.ParsedAuthorizationResponse;
import com.gsma.mobileconnect.oidc.RequestTokenResponse;
import com.gsma.mobileconnect.utils.StringUtils;

/**
 * Class to hold the response from calling a MobileConnectInterface method see {@link MobileConnectInterface}.
 * <p>
 * The return of the methods can be one of several types. Depending on the type of response
 * various properties are available.
 */
public class MobileConnectStatus
{
    private static final String SUCCESS_RESPONSE_STATUS = "success";
    private static final String ERROR_RESPONSE_STATUS = "error";

    private static final String OPERATOR_SELECTION_ACTION = "operator_selection";
    private static final String AUTHORIZATION_ACTION = "authorization";
    private static final String DISCOVERY_ACTION = "discovery";

    private enum ResponseType
    {
        ERROR, OPERATOR_SELECTION, START_DISCOVERY, START_AUTHORIZATION, AUTHORIZATION, COMPLETE
    }

    /**
     * Internal class used to generate JSON object responses.
     */
    public class ResponseJson
    {
        final private String status;

        final private String action;

        final private String parameter1;

        final private String parameter2;

        public String getStatus()
        {
            return status;
        }

        public String getAction()
        {
            return action;
        }

        public String getParameter1()
        {
            return parameter1;
        }

        public String getParameter2()
        {
            return parameter2;
        }

        ResponseJson(String status, String action, String parameter1)
        {
            this.status = status;
            this.action = action;
            this.parameter1 = parameter1;
            this.parameter2 = null;
        }

        ResponseJson(String status, String action, String parameter1, String parameter2)
        {
            this.status = status;
            this.action = action;
            this.parameter1 = parameter1;
            this.parameter2 = parameter2;
        }
    }

    private ResponseType responseType;

    private String error;
    private String description;
    private Exception exception;
    private String url;
    private DiscoveryResponse discoveryResponse;
    private ResponseJson responseJson;
    private ParsedAuthorizationResponse parsedAuthorizationResponse;
    private RequestTokenResponse requestTokenResponse;
    private String screenMode;

    /**
     * There was an error when a Mobile Connect Interface method was called.
     * <p>
     * Error {@link MobileConnectStatus#getError()}, Description {@link MobileConnectStatus#getDescription()}
     * are available.
     * <p>
     * Exception {@link MobileConnectStatus#getException()}, DiscoveryResponse {@link MobileConnectStatus#getDiscoveryResponse()},
     * ParsedAuthorizationResponse {@link MobileConnectStatus#getParsedAuthorizationResponse()} and
     * RequestTokenResponse {@link MobileConnectStatus#getRequestTokenResponse()} may be available.
     *
     * @return True if there was an error
     */
    public boolean isError()
    {
        return responseType.equals(ResponseType.ERROR);
    }

    /**
     * Operator selection is required.
     * <p>
     * Url {@link MobileConnectStatus#getUrl()} is available.
     *
     * @return True if operator selection is required.
     */
    public boolean isOperatorSelection()
    {
        return responseType.equals(ResponseType.OPERATOR_SELECTION);
    }

    /**
     * The authorization process can start
     * <p>
     * DiscoveryResponse {@link MobileConnectStatus#getDiscoveryResponse()} is available.
     *
     * @return True if the authorization process can start.
     */
    public boolean isStartAuthorization()
    {
        return responseType.equals(ResponseType.START_AUTHORIZATION);
    }

    /**
     * The discovery process needs to started.
     * <p>
     * No additional properties are available.
     *
     * @return True if the discovery process needs to be started.
     */
    public boolean isStartDiscovery()
    {
        return responseType.equals(ResponseType.START_DISCOVERY);
    }

    /**
     * The authorization process can start with the operator.
     * <p>
     * Url {@link MobileConnectStatus#getUrl()} is available
     *
     * @return True if the authorization process with the operator can start
     */
    public boolean isAuthorization()
    {
        return responseType.equals(ResponseType.AUTHORIZATION);
    }

    /**
     * Authorization has completed successfully.
     * <p>
     * The ParsedAuthorizationResponse {@link MobileConnectStatus#getParsedAuthorizationResponse()} and
     * RequestTokenResponse {@link MobileConnectStatus#getRequestTokenResponse()} is available.
     *
     * @return True if the authorization process has completed successfully.
     */
    public boolean isComplete()
    {
        return responseType.equals(ResponseType.COMPLETE);
    }

    /**
     * Error type.
     * <p>
     * Available when {@link MobileConnectStatus#isError()} returns true.
     *
     * @return The error.
     */
    public String getError()
    {
        return error;
    }

    /**
     * Error description.
     * <p>
     * Available when {@link MobileConnectStatus#isError()} returns true.
     *
     * @return A description of the error.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * An optional Exception causing an error.
     * <p>
     * Optionally available when {@link MobileConnectStatus#isError()} returns true.
     *
     * @return An exception that caused the error.
     */
    public Exception getException()
    {
        return  exception;
    }

    /**
     * Url to be redirected to.
     * <p>
     * Available when {@link MobileConnectStatus#isOperatorSelection()} or {@link MobileConnectStatus#isAuthorization()} return true.
     *
     * @return Url to be redirected to.
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * Discovery Response of the selected operator.
     * <p>
     * Available when {@link MobileConnectStatus#isStartAuthorization()} or {@link MobileConnectStatus#isAuthorization()} return true.
     *
     * @return Discovery Response for the selected operator.
     */
    public DiscoveryResponse getDiscoveryResponse()
    {
        return discoveryResponse;
    }

    /**
     * The ParsedAuthorizationResponse
     * <p>
     * Available when {@link MobileConnectStatus#isComplete()} returns true
     *
     * @return The ParsedAuthorizationResponse
     */
    public ParsedAuthorizationResponse getParsedAuthorizationResponse()
    {
        return parsedAuthorizationResponse;
    }

    /**
     * The RequestTokenResponse
     * <p>
     * Available when {@link MobileConnectStatus#isComplete()} returns true
     *
     * @return The authorization token response
     */
    public RequestTokenResponse getRequestTokenResponse()
    {
        return requestTokenResponse;
    }

    public String getScreenMode()
    {
        return screenMode;
    }

    /**
     * Serialize the response to Json for the client javascript.
     *
     * @return Json string for client javascript.
     */
    public ResponseJson getResponseJson()
    {
        if(null == responseJson)
        {
            throw new IllegalStateException("Cannot generate Json for the current state");
        }
        return  responseJson;
    }

    private static String ensureStringHasValue(String input)
    {
        if(StringUtils.isNullOrEmpty(input))
        {
            return "";
        }
        else
        {
            return input;
        }
    }

    /**
     * Create and initialize an error type object.
     *
     * @param error The error.
     * @param description The error description.
     * @param exception Optional exception causing the error.
     * @return An error type object.
     */
    static MobileConnectStatus error(String error, String description, Exception exception)
    {
        error = ensureStringHasValue(error);
        description = ensureStringHasValue(description);
        MobileConnectStatus mobileConnectStatus = new MobileConnectStatus();
        mobileConnectStatus.responseType = ResponseType.ERROR;
        mobileConnectStatus.error = error;
        mobileConnectStatus.description = description;
        mobileConnectStatus.exception = exception;
        mobileConnectStatus.responseJson = mobileConnectStatus.new ResponseJson(ERROR_RESPONSE_STATUS, null, error, description);
        return mobileConnectStatus;
    }

    /**
     * Create and initialize an error type object.
     *
     * @param error The error.
     * @param description The error description.
     * @param discoveryResponse The discovery response
     * @return An error type object.
     */
    static MobileConnectStatus error(String error, String description, DiscoveryResponse discoveryResponse)
    {
        error = ensureStringHasValue(error);
        description = ensureStringHasValue(description);
        MobileConnectStatus mobileConnectStatus = new MobileConnectStatus();
        mobileConnectStatus.responseType = ResponseType.ERROR;
        mobileConnectStatus.error = error;
        mobileConnectStatus.description = description;
        mobileConnectStatus.discoveryResponse = discoveryResponse;
        mobileConnectStatus.responseJson = mobileConnectStatus.new ResponseJson(ERROR_RESPONSE_STATUS, null, error, description);
        return mobileConnectStatus;
    }

    /**
     * Create and initialize an error type object.
     *
     * @param error The error
     * @param description The description of the error
     * @param parsedAuthorizationResponse The parsed authorization response
     * @param requestTokenResponse The authorization token
     * @return An error type response
     */
    static MobileConnectStatus error(String error, String description, ParsedAuthorizationResponse parsedAuthorizationResponse, RequestTokenResponse requestTokenResponse)
    {
        error = ensureStringHasValue(error);
        description = ensureStringHasValue(description);
        MobileConnectStatus mobileConnectStatus = new MobileConnectStatus();
        mobileConnectStatus.responseType = ResponseType.ERROR;
        mobileConnectStatus.error = error;
        mobileConnectStatus.description = description;
        mobileConnectStatus.parsedAuthorizationResponse = parsedAuthorizationResponse;
        mobileConnectStatus.requestTokenResponse = requestTokenResponse;
        mobileConnectStatus.responseJson = mobileConnectStatus.new ResponseJson(ERROR_RESPONSE_STATUS, null, error, description);
        return mobileConnectStatus;
    }

    /**
     * Create and initialise an operator selection type object.
     *
     * @param url The operator selection url
     * @return An operator selection type object.
     */
    static MobileConnectStatus operatorSelection(String url)
    {
        MobileConnectStatus mobileConnectStatus = new MobileConnectStatus();
        mobileConnectStatus.responseType = ResponseType.OPERATOR_SELECTION;
        mobileConnectStatus.url = url;
        mobileConnectStatus.responseJson = mobileConnectStatus.new ResponseJson(SUCCESS_RESPONSE_STATUS, OPERATOR_SELECTION_ACTION, url);
        return mobileConnectStatus;
    }

    /**
     * Create and initialise a start authorization type object
     *
     * @param discoveryResponse The DiscoveryResponse for the selected operator.
     * @return A start authorization type object.
     */
    static MobileConnectStatus startAuthorization(DiscoveryResponse discoveryResponse)
    {
        MobileConnectStatus mobileConnectStatus = new MobileConnectStatus();
        mobileConnectStatus.responseType = ResponseType.START_AUTHORIZATION;
        mobileConnectStatus.discoveryResponse = discoveryResponse;
        mobileConnectStatus.responseJson = mobileConnectStatus.new ResponseJson(SUCCESS_RESPONSE_STATUS, AUTHORIZATION_ACTION, null);
        return mobileConnectStatus;
    }

    /**
     * Create and initialize a start discovery type object.
     *
     * @return A start discovery type object
     */
    static MobileConnectStatus startDiscovery()
    {
        MobileConnectStatus mobileConnectStatus = new MobileConnectStatus();
        mobileConnectStatus.responseType = ResponseType.START_DISCOVERY;
        mobileConnectStatus.responseJson = mobileConnectStatus.new ResponseJson(SUCCESS_RESPONSE_STATUS, DISCOVERY_ACTION, null);
        return mobileConnectStatus;
    }

    /**
     * Create and initialise an authorization type object.
     *
     * @param discoveryResponse The operator discovery response
     * @param url The url to use
     * @param screenMode Indication on how to display the page
     * @return An authorization type object
     */
    static MobileConnectStatus authorization(DiscoveryResponse discoveryResponse, String url, String screenMode)
    {
        MobileConnectStatus mobileConnectStatus = new MobileConnectStatus();
        mobileConnectStatus.responseType = ResponseType.AUTHORIZATION;
        mobileConnectStatus.discoveryResponse = discoveryResponse;
        mobileConnectStatus.url = url;
        mobileConnectStatus.screenMode = screenMode;
        mobileConnectStatus.responseJson = mobileConnectStatus.new ResponseJson(SUCCESS_RESPONSE_STATUS, AUTHORIZATION_ACTION, url, screenMode);
        return mobileConnectStatus;
    }

    /**
     * Create and initialize a complete type object
     *
     * @param parsedAuthorizationResponse The authorization response
     * @param requestTokenResponse The authorization token
     * @return A complete type object
     */
    static MobileConnectStatus complete(ParsedAuthorizationResponse parsedAuthorizationResponse, RequestTokenResponse requestTokenResponse)
    {
        MobileConnectStatus mobileConnectStatus = new MobileConnectStatus();
        mobileConnectStatus.responseType = ResponseType.COMPLETE;
        mobileConnectStatus.parsedAuthorizationResponse = parsedAuthorizationResponse;
        mobileConnectStatus.requestTokenResponse = requestTokenResponse;
        return mobileConnectStatus;
    }
}
