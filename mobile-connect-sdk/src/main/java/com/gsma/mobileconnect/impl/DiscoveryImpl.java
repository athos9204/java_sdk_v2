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

package com.gsma.mobileconnect.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.gsma.mobileconnect.cache.DiscoveryCacheKey;
import com.gsma.mobileconnect.cache.DiscoveryCacheValue;
import com.gsma.mobileconnect.cache.IDiscoveryCache;
import com.gsma.mobileconnect.discovery.*;
import com.gsma.mobileconnect.utils.*;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

/**
 * An implementation of {@link IDiscovery}.
 * <p>
 * An instance of this class is constructed using the {@link Factory} which allows an implementation of {@link IDiscoveryCache}
 * to be specified.
 */
class DiscoveryImpl implements IDiscovery
{
    final private IDiscoveryCache discoveryCache;

    final private RestClient restClient;

    DiscoveryImpl(IDiscoveryCache discoveryCache, RestClient restClient)
    {
        this.discoveryCache = discoveryCache;
        this.restClient = restClient;
    }

    /**
     *  See {@link IDiscovery#startAutomatedOperatorDiscovery(String, String, String, String, DiscoveryOptions, List, IDiscoveryResponseCallback)}
     */
    public void startAutomatedOperatorDiscovery(IPreferences preferences, String redirectURL, DiscoveryOptions specifiedOptions,
                                                List<KeyValuePair> currentCookies, IDiscoveryResponseCallback callback)
            throws DiscoveryException
    {
        if (null == preferences)
        {
            ValidationUtils.validateParameter(null, "preferences");
            return;
        }
        startAutomatedOperatorDiscovery(preferences.getClientId(), preferences.getClientSecret(), preferences.getDiscoveryURL(),
                redirectURL, specifiedOptions, currentCookies, callback);
    }

    /**
     * See {@link IDiscovery#startAutomatedOperatorDiscovery(IPreferences, String, DiscoveryOptions, List, IDiscoveryResponseCallback)}.
     */
    public void startAutomatedOperatorDiscovery(String clientId, String clientSecret, String discoveryURL, String redirectURL,
                                                DiscoveryOptions specifiedOptions, List<KeyValuePair> currentCookies,
                                                IDiscoveryResponseCallback callback)
            throws DiscoveryException
    {
        validateDiscoveryParameters(clientId, clientSecret, discoveryURL, redirectURL, callback);
        DiscoveryOptions optionsToBeUsed = getOptionsToBeUsed(specifiedOptions);

        DiscoveryCacheKey cacheKey = DiscoveryCacheKey.newWithDetails(optionsToBeUsed.getIdentifiedMCC(), optionsToBeUsed.getIdentifiedMNC());
        DiscoveryResponse cachedValue = getCachedValue(cacheKey);
        if (null != cachedValue)
        {
            callback.completed(cachedValue);
            return;
        }

        RestResponse restResponse = null;
        try
        {
            URI discoveryURI = new URI(discoveryURL);
            HttpGet httpGet = buildHttpGetForOperatorDiscovery(discoveryURI, redirectURL, optionsToBeUsed);

            HttpClientContext context = restClient.getHttpClientContext(clientId, clientSecret, discoveryURI);

            int timeout = optionsToBeUsed.getTimeout();

            List<KeyValuePair> cookiesToProxy = HttpUtils.getCookiesToProxy(optionsToBeUsed.isCookiesEnabled(), currentCookies);

            restResponse = restClient.callRestEndPoint(httpGet, context, timeout, cookiesToProxy);

            DiscoveryResponse discoveryResponse = buildDiscoveryResponse(restResponse.getStatusCode(), restResponse.getHeaders(), JsonUtils.parseJson(restResponse.getResponse()));

            addCachedValue(cacheKey, discoveryResponse);

            callback.completed(discoveryResponse);
        }
        catch(RestException ex)
        {
            throw newDiscoveryExceptionFromRestException("Call to Discovery End Point failed", ex);
        }
        catch (URISyntaxException ex)
        {
            throw newDiscoveryExceptionWithRestResponse("Failed to build discovery URI", null, ex);
        }
        catch (JsonProcessingException ex)
        {
            throw newDiscoveryExceptionWithRestResponse("Failed creating Json object", restResponse, ex);
        }
        catch (IOException ex)
        {
            throw newDiscoveryExceptionWithRestResponse("Calling Discovery service failed", restResponse, ex);
        }
    }

    /**
     * See {@link IDiscovery#getOperatorSelectionURL(IPreferences, String, TimeoutOptions, IDiscoveryResponseCallback)}
     */
    public void getOperatorSelectionURL(IPreferences preferences, String redirectURL,
                                        TimeoutOptions specifiedOptions, IDiscoveryResponseCallback callback)
            throws DiscoveryException
    {
        if(null == preferences)
        {
            ValidationUtils.validateParameter(null, "preferences");
            return;
        }
        getOperatorSelectionURL(preferences.getClientId(), preferences.getClientSecret(), preferences.getDiscoveryURL(), redirectURL, specifiedOptions, callback);
    }

    /**
     * See {@link IDiscovery#getOperatorSelectionURL(String, String, String, String, TimeoutOptions, IDiscoveryResponseCallback)}
     */
    public void getOperatorSelectionURL(String clientId, String clientSecret, String discoveryURL, String redirectURL,
                                        TimeoutOptions specifiedOptions, IDiscoveryResponseCallback callback)
            throws DiscoveryException
    {
        validateDiscoveryParameters(clientId, clientSecret, discoveryURL, redirectURL, callback);
        DiscoveryOptions optionsToBeUsed = getOptionsToBeUsed(specifiedOptions);

        RestResponse restResponse = null;
        try
        {
            URI discoveryURI = new URI(discoveryURL);
            HttpGet httpGet = buildHttpGetForOperatorDiscovery(discoveryURI, redirectURL, optionsToBeUsed);

            HttpClientContext context = restClient.getHttpClientContext(clientId, clientSecret, discoveryURI);

            int timeout = optionsToBeUsed.getTimeout();

            restResponse = restClient.callRestEndPoint(httpGet, context, timeout, null);

            callback.completed(buildDiscoveryResponse(restResponse.getStatusCode(), restResponse.getHeaders(), JsonUtils.parseJson(restResponse.getResponse())));
        }
        catch(RestException ex)
        {
            throw newDiscoveryExceptionFromRestException("Call to Discovery end point failed", ex);
        }
        catch (URISyntaxException ex)
        {
            throw newDiscoveryExceptionWithRestResponse("Failed to build discovery URI", null, ex);
        }
        catch (JsonProcessingException ex)
        {
            throw newDiscoveryExceptionWithRestResponse("Failed creating Json object", restResponse, ex);
        }
        catch (IOException ex)
        {
            throw newDiscoveryExceptionWithRestResponse("Calling Discovery service failed", restResponse, ex);
        }
    }

    /**
     * See {@link IDiscovery#parseDiscoveryRedirect(String, IParsedDiscoveryRedirectCallback)}
     */
    public void parseDiscoveryRedirect(String redirectURL, IParsedDiscoveryRedirectCallback callback)
            throws URISyntaxException
    {
        ValidationUtils.validateParameter(redirectURL, "redirectURL");
        ValidationUtils.validateParameter(callback, "callback");

        URI uri = new URI(redirectURL);
        String query = uri.getQuery();
        if(null == query)
        {
            callback.completed(new ParsedDiscoveryRedirect(null, null, null));
            return;
        }
        List<NameValuePair> parameters = URLEncodedUtils.parse(uri.getQuery(), null);
        String mcc_mnc = HttpUtils.getParameterValue(parameters, Constants.MCC_MNC_PARAMETER_NAME);
        String subscriber_id = HttpUtils.getParameterValue(parameters, Constants.SUBSCRIBER_ID_PARAMETER_NAME);

        String mcc = null;
        String mnc = null;
        if(null != mcc_mnc)
        {
            String[] parts = mcc_mnc.split("_");
            if(parts.length == 2)
            {
                mcc = parts[0];
                mnc = parts[1];
            }
        }
        callback.completed(new ParsedDiscoveryRedirect(mcc, mnc, subscriber_id));
    }

    /**
     * See {@link IDiscovery#completeSelectedOperatorDiscovery(String, String, String, String, String, String, CompleteSelectedOperatorDiscoveryOptions, List, IDiscoveryResponseCallback)}
     */
    public void completeSelectedOperatorDiscovery(IPreferences preferences, String redirectURL, String selectedMCC, String selectedMNC,
                                                  CompleteSelectedOperatorDiscoveryOptions specifiedOptions,
                                                  List<KeyValuePair> currentCookies, IDiscoveryResponseCallback callback)
            throws DiscoveryException
    {
        if(null == preferences)
        {
            ValidationUtils.validateParameter(null, "preferences");
            return;
        }
        completeSelectedOperatorDiscovery(preferences.getClientId(), preferences.getClientSecret(), preferences.getDiscoveryURL(),
                redirectURL, selectedMCC, selectedMNC, specifiedOptions, currentCookies, callback);
    }

    /**
     * See {@link IDiscovery#completeSelectedOperatorDiscovery(IPreferences, String, String, String, CompleteSelectedOperatorDiscoveryOptions, List, IDiscoveryResponseCallback)}
     */
    public void completeSelectedOperatorDiscovery(String clientId, String clientSecret, String discoveryURL,
                                                  String redirectURL, String selectedMCC, String selectedMNC,
                                                  CompleteSelectedOperatorDiscoveryOptions specifiedOptions, List<KeyValuePair> currentCookies,
                                                  IDiscoveryResponseCallback callback)
            throws DiscoveryException
    {
        if(StringUtils.isNullOrEmpty(redirectURL))
        {
            redirectURL = Constants.DEFAULT_REDIRECT_URL;
        }
        validateDiscoveryParameters(clientId, clientSecret, discoveryURL, selectedMCC, selectedMNC, callback);

        DiscoveryCacheKey cacheKey = DiscoveryCacheKey.newWithDetails(selectedMCC, selectedMNC);
        DiscoveryResponse cachedValue = getCachedValue(cacheKey);
        if (null != cachedValue)
        {
            callback.completed(cachedValue);
            return;
        }

        RestResponse restResponse = null;
        CompleteSelectedOperatorDiscoveryOptions optionsToBeUsed = getOptionsToBeUsed(specifiedOptions);
        try
        {
            URI discoveryURI = new URI(discoveryURL);
            HttpGet httpGet = buildHttpGetForCompleteSelectedOperatorDiscovery(discoveryURI, redirectURL, selectedMCC, selectedMNC);

            HttpClientContext context = restClient.getHttpClientContext(clientId, clientSecret, discoveryURI);

            int timeout = optionsToBeUsed.getTimeout();

            List<KeyValuePair> cookiesToProxy = HttpUtils.getCookiesToProxy(optionsToBeUsed.isCookiesEnabled(), currentCookies);

            restResponse = restClient.callRestEndPoint(httpGet, context, timeout, cookiesToProxy);

            DiscoveryResponse discoveryResponse = buildDiscoveryResponse(restResponse.getStatusCode(), restResponse.getHeaders(), JsonUtils.parseJson(restResponse.getResponse()));

            addCachedValue(cacheKey, discoveryResponse);

            callback.completed(discoveryResponse);
        }
        catch(RestException ex)
        {
            throw newDiscoveryExceptionFromRestException("Call to Discovery end point failed", ex);
        }
        catch (URISyntaxException ex)
        {
            throw newDiscoveryExceptionWithRestResponse("Failed to build discovery URI", null, ex);
        }
        catch (JsonProcessingException ex)
        {
            throw newDiscoveryExceptionWithRestResponse("Failed creating Json object", restResponse, ex);
        }
        catch (IOException ex)
        {
            throw newDiscoveryExceptionWithRestResponse("Calling Discovery service failed", restResponse, ex);
        }
    }

    /**
     * See {@link IDiscovery#extractOperatorSelectionURL(DiscoveryResponse)}
     */
    public String extractOperatorSelectionURL(DiscoveryResponse discoveryResult)
    {
        if(!isValidDiscoveryResponse(discoveryResult))
        {
            return null;
        }
        return JsonUtils.extractUrl(discoveryResult.getResponseData(), Constants.OPERATOR_SELECTION_REL);
    }

    /**
     * See {@link IDiscovery#isOperatorSelectionRequired(DiscoveryResponse)}.
     */
    public boolean isOperatorSelectionRequired(DiscoveryResponse discoveryResult)
    {
        validateDiscoveryResponse(discoveryResult);
        return !StringUtils.isNullOrEmpty(extractOperatorSelectionURL(discoveryResult));
    }

    /**
     * See {@link IDiscovery#isErrorResponse(DiscoveryResponse)}
     */
    public boolean isErrorResponse(DiscoveryResponse discoveryResult)
    {
        return getErrorResponse(discoveryResult) != null;
    }

    /**
     * See {@link IDiscovery#getErrorResponse(DiscoveryResponse)}
     */
    public ErrorResponse getErrorResponse(DiscoveryResponse discoveryResult)
    {
        if(null == discoveryResult || null == discoveryResult.getResponseData())
        {
            return null;
        }
        return JsonUtils.getErrorResponse(discoveryResult.getResponseData());
    }

    /**
     * See {@link IDiscovery#getCachedDiscoveryResult(String, String)}
     */
    public DiscoveryResponse getCachedDiscoveryResult(String mcc, String mnc)
    {
        ValidationUtils.validateParameter(mcc, "mcc");
        ValidationUtils.validateParameter(mnc, "mnc");

        return getCachedValue(DiscoveryCacheKey.newWithDetails(mcc, mnc));
    }

    /**
     * See {@link IDiscovery#clearDiscoveryCache(CacheOptions)}
     */
    public void clearDiscoveryCache(CacheOptions options)
    {
        if(null == discoveryCache)
        {
            return;
        }

        if(null == options)
        {
            discoveryCache.clear();
            return;
        }

        DiscoveryCacheKey key = DiscoveryCacheKey.newWithDetails(options.getMCC(), options.getMNC());
        if(null == key)
        {
            return;
        }

        discoveryCache.remove(key);
    }

    private void validateDiscoveryParameters(String clientId, String clientSecret, String discoveryURL, String redirectURL, IDiscoveryResponseCallback callback)
    {
        ValidationUtils.validateParameter(clientId, "clientId");
        ValidationUtils.validateParameter(clientSecret, "clientSecret");
        ValidationUtils.validateParameter(discoveryURL, "discoveryURL");
        ValidationUtils.validateParameter(redirectURL, "redirectURL");
        ValidationUtils.validateParameter(callback, "callback");
    }

    private void validateDiscoveryParameters(String clientId, String clientSecret, String discoveryURL, String selectedMCC, String selectedMNC, IDiscoveryResponseCallback callback)
    {
        ValidationUtils.validateParameter(clientId, "clientId");
        ValidationUtils.validateParameter(clientSecret, "clientSecret");
        ValidationUtils.validateParameter(discoveryURL, "discoveryURL");
        ValidationUtils.validateParameter(selectedMCC, "selectedMCC");
        ValidationUtils.validateParameter(selectedMNC, "selectedMNC");
        ValidationUtils.validateParameter(callback, "callback");
    }

    private void validateDiscoveryResponse(DiscoveryResponse discoveryResult)
    {
        ValidationUtils.validateParameter(discoveryResult, "discoveryResult");
        if(!isValidDiscoveryResponse(discoveryResult))
        {
            throw new IllegalArgumentException("Not a valid discoveryResult");
        }
    }

    /**
     * Does the Discovery Response hold valid data?
     *
     * @param discoveryResult The discovery response to examine.
     * @return True is there is data and it is from the cache or the response code indicates it is valid.
     */
    private boolean isValidDiscoveryResponse(DiscoveryResponse discoveryResult)
    {
        if(null == discoveryResult || null == discoveryResult.getResponseData())
        {
            return false;
        }

        if(discoveryResult.isCached())
        {
            return true;
        }

        int responseCode = discoveryResult.getResponseCode();
        return responseCode == Constants.OPERATOR_IDENTIFIED_RESPONSE || responseCode == Constants.OPERATOR_NOT_IDENTIFIED_RESPONSE;
    }

    /**
     * Return the options to be used for the startAutomatedOperatorDiscovery call.
     * <p>
     * Use caller provided values if passed, use defaults otherwise.
     *
     * @param originalOptions The caller specified options if any.
     * @return The options to be used.
     */
    private DiscoveryOptions getOptionsToBeUsed(DiscoveryOptions originalOptions)
    {
        if(null == originalOptions)
        {
            return new DiscoveryOptions();
        }
        else
        {
            return originalOptions;
        }
    }

    /**
     * Return the options to be used for the getOperatorSelectionURL call.
     * <p>
     * Use caller provided values if passed, use defaults otherwise.
     *
     * @param originalOptions The caller specified options if any.
     * @return The options to be used.
     */
    private DiscoveryOptions getOptionsToBeUsed(TimeoutOptions originalOptions)
    {
        DiscoveryOptions optionsToBeUsed = new DiscoveryOptions();
        optionsToBeUsed.setManuallySelect(true);
        optionsToBeUsed.setIdentifiedMCC(null);
        optionsToBeUsed.setIdentifiedMNC(null);
        optionsToBeUsed.setUsingMobileData(false);
        optionsToBeUsed.setCookiesEnabled(true);
        optionsToBeUsed.setLocalClientIP(null);

        TimeoutOptions tmpOptions = originalOptions;
        if(null == tmpOptions)
        {
            tmpOptions = new TimeoutOptions();
        }
        optionsToBeUsed.setTimeout(tmpOptions.getTimeout());

        return optionsToBeUsed;
    }

    /**
     * Return the options to be used for the CompleteSelectedOperatorDiscovery call.
     * <p>
     * Use caller provided values if passed, use defaults otherwise.
     *
     * @param originalOptions The caller specified options if any.
     * @return The options to be used.
     */
    private CompleteSelectedOperatorDiscoveryOptions getOptionsToBeUsed(CompleteSelectedOperatorDiscoveryOptions originalOptions)
    {
        CompleteSelectedOperatorDiscoveryOptions tmpOptions = originalOptions;
        if(null == tmpOptions)
        {
            tmpOptions = new CompleteSelectedOperatorDiscoveryOptions();
        }

        return tmpOptions;
    }

    /**
     * Return a value from the cache.
     *
     * @param key Identifies the value to return.
     * @return The cached value if available, null otherwise.
     */
    private DiscoveryResponse getCachedValue(DiscoveryCacheKey key)
    {
        if (null == discoveryCache)
        {
            return null;
        }
        if(null == key)
        {
            return null;
        }
        DiscoveryCacheValue value = discoveryCache.get(key);
        if(null == value)
        {
            return null;
        }
        return buildDiscoveryResponse(value);
    }

    /**
     * Add a value to the cache.
     * <p>
     * Only operator identified responses are cached.
     *
     * @param key The cache key.
     * @param value The value to cache.
     */
    void addCachedValue(DiscoveryCacheKey key, DiscoveryResponse value)
    {
        if(null == discoveryCache)
        {
            return;
        }
        if(null == key || null == value)
        {
            return;
        }
        if(value.getResponseCode() != Constants.OPERATOR_IDENTIFIED_RESPONSE)
        {
            return;
        }
        if(null == value.getTtl() || null == value.getResponseData())
        {
            return;
        }
        discoveryCache.add(key, new DiscoveryCacheValue(value.getTtl(), value.getResponseData()));
    }

    /**
     * Build a HttpGet for the Discovery Service.
     *
     * @param discoveryURI URI of the discovery service.
     * @param redirectURL The redirect URL to pass as a parameter.
     * @param options Optional parameters to the get.
     * @return HttpGet to the Discovery Service to identify an operator.
     * @throws URISyntaxException
     */
    private HttpGet buildHttpGetForOperatorDiscovery(URI discoveryURI, String redirectURL,
                                                              DiscoveryOptions options)
            throws URISyntaxException
    {
        URIBuilder uri = new URIBuilder(discoveryURI);
        HttpUtils.addParameter(uri, Constants.MANUALLY_SELECT_PARAMETER_NAME, options.isManuallySelect());
        HttpUtils.addParameter(uri, Constants.IDENTIFIED_MCC_PARAMETER_NAME, options.getIdentifiedMCC());
        HttpUtils.addParameter(uri, Constants.IDENTIFIED_MNC_PARAMETER_NAME, options.getIdentifiedMNC());
        HttpUtils.addParameter(uri, Constants.USING_MOBILE_DATA_PARAMETER_NAME, options.isUsingMobileData());
        HttpUtils.addParameter(uri, Constants.LOCAL_CLIENT_IP_PARAMETER_NAME, options.getLocalClientIP());
        HttpUtils.addParameter(uri, Constants.REDIRECT_URL_PARAMETER_NAME, redirectURL);

        HttpGet httpGet = new HttpGet(uri.build());
        httpGet.setHeader(Constants.ACCEPT_HEADER_NAME, Constants.ACCEPT_JSON_HEADER_VALUE);

        if(!StringUtils.isNullOrEmpty(options.getClientIP()))
        {
            httpGet.setHeader(Constants.X_SOURCE_IP_HEADER_NAME, options.getClientIP());
        }

        return httpGet;
    }

    /**
     * Build a HttpGet for the CompleteSelectedOperatorDiscovery method.
     *
     * @param discoveryURI The URI of the discovery service.
     * @param redirectURL The redirect URL to pass as a parameter.
     * @param selectedMCC The MCC of the selected operator.
     * @param selectedMNC The MNC of the selected operator.
     * @return HttpGet to the Discovery Service to identify an operator.
     * @throws URISyntaxException
     */
    private HttpGet buildHttpGetForCompleteSelectedOperatorDiscovery(URI discoveryURI, String redirectURL, String selectedMCC, String selectedMNC)
            throws URISyntaxException
    {
        URIBuilder uri = new URIBuilder(discoveryURI);
        HttpUtils.addParameter(uri, Constants.REDIRECT_URL_PARAMETER_NAME, redirectURL);
        HttpUtils.addParameter(uri, Constants.SELECTED_MCC_PARAMETER_NAME, selectedMCC);
        HttpUtils.addParameter(uri, Constants.SELECTED_MNC_PARAMETER_NAME, selectedMNC);

        HttpGet httpGet = new HttpGet(uri.build());
        httpGet.setHeader(Constants.ACCEPT_HEADER_NAME, Constants.ACCEPT_JSON_HEADER_VALUE);
        return httpGet;
    }

    private DiscoveryException newDiscoveryExceptionFromRestException(String message, RestException restException)
    {
        return new DiscoveryException(message, restException.getUri(), restException.getStatusCode(), restException.getHeaders(), restException.getContents(), restException);
    }

    private DiscoveryException newDiscoveryExceptionWithRestResponse(String message, RestResponse restResponse, Throwable ex)
    {
        if(null == restResponse)
        {
            return new DiscoveryException(message, ex);
        }
        else
        {
            return new DiscoveryException(message, restResponse.getUri(), restResponse.getStatusCode(), restResponse.getHeaders(), restResponse.getResponse(), ex);
        }
    }

    private DiscoveryResponse buildDiscoveryResponse(int responseCode, List<KeyValuePair> headers, JsonNode jsonDoc)
    {
        Date ttl = determineTtl(JsonUtils.getDiscoveryResponseTtl(jsonDoc));
        return new DiscoveryResponse(false, ttl, responseCode, headers, jsonDoc);
    }

    private DiscoveryResponse buildDiscoveryResponse(DiscoveryCacheValue value)
    {
        return new DiscoveryResponse(true, value.getTtl(), 0, null, value.getValue());
    }

    /**
     * Determine the time-to-live for a discovery response.
     * <p>
     * Ensure the ttl is between a minimum and maximum value.
     *
     * @param ttlTime The ttl value from the discovery result.
     * @return The ttl to use.
     */
    Date determineTtl(Long ttlTime)
    {
        Long now = new Date().getTime();
        Date min = new Date(now+Constants.MINIMUM_TTL_MS);
        Date max = new Date(now+Constants.MAXIMUM_TTL_MS);

        if(null == ttlTime)
        {
            return min;
        }

        Date currentTtl = new Date(ttlTime);

        if(currentTtl.before(min))
        {
            return min;
        }

        if(currentTtl.after(max))
        {
            return max;
        }

        return currentTtl;
    }
}
