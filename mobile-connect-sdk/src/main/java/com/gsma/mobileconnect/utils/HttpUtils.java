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

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class to hold utility functions.
 */
public class HttpUtils
{
    private static final Set<String> mobileConnectCookieNames;

    static {
        mobileConnectCookieNames = new HashSet<String>(3);
        mobileConnectCookieNames.add(Constants.MOST_RECENT_SELECTED_OPERATOR_COOKIE_NAME);
        mobileConnectCookieNames.add(Constants.MOST_RECENT_SELECTED_OPERATOR_EXPIRY_COOKIE_NAME);
        mobileConnectCookieNames.add(Constants.ENUM_NONCE_COOKIE_NAME);
    }

    /**
     * Add a boolean parameter to the URI.
     * <p>
     * Adds the string value of the boolean value to the URI.
     *
     * @param uri The URI to add the parameter to.
     * @param parameterName The name of the parameter to add.
     * @param value The value of the parameter to add.
     */
    public static void addParameter(URIBuilder uri, String parameterName, boolean value)
    {
        uri.addParameter(parameterName, String.valueOf(value));
    }

    /**
     * Add the string value of the value as a parameter to the URI.
     * <p>
     * If the value is null then no parameter is added.
     *
     * @param uri The URI to add the parameter to.
     * @param parameterName The name of the parameter to add.
     * @param value The value of the parameter to add.
     */
    public static void addParameter(URIBuilder uri, String parameterName, Object value)
    {
        if(null != value)
        {
            uri.addParameter(parameterName, String.valueOf(value));
        }
    }

    /**
     * Examine the list of cookies passed looking for ones that should be forwarded.
     *
     * @param isCookiesEnabled If false an empty list is returned.
     * @param currentCookies The list of cookies.
     * @return The list of cookies to be forwarded.
     */
    public static List<KeyValuePair> getCookiesToProxy(boolean isCookiesEnabled, List<KeyValuePair> currentCookies)
    {
        List<KeyValuePair> cookiesToProxy = new ArrayList<KeyValuePair>(3);

        if(!isCookiesEnabled || null == currentCookies)
        {
            return cookiesToProxy;
        }

        for(KeyValuePair kv : currentCookies)
        {
            if(mobileConnectCookieNames.contains(kv.getKey()))
            {
                cookiesToProxy.add(kv);
            }
        }
        return cookiesToProxy;
    }

    /**
     * Examine the list of NameValuePairs looking for an entry whose name matches the passed parameter.
     *
     * @param pairs List of NameValuePairs to examine.
     * @param parameter The name of the entry to look for.
     * @return Value of a matching entry if found, null otherwise.
     */
    public static String getParameterValue(List<NameValuePair> pairs, String parameter)
    {
        if(null == pairs)
        {
            return null;
        }

        for(NameValuePair nvp : pairs)
        {
            if(nvp.getName().equals(parameter))
            {
                return nvp.getValue();
            }
        }
        return null;
    }

    /**
     * Extract the parameters from the passed URL.
     *
     * @param url The URL to extract the parameters from.
     * @return The list of parameters, as a list of NameValuePairs.
     * @throws URISyntaxException
     */
    public static List<NameValuePair> extractParameters(String url)
            throws URISyntaxException
    {
        if(StringUtils.isNullOrEmpty(url))
        {
            return new ArrayList<NameValuePair>(0);
        }
        URI uri = new URI(url);
        return URLEncodedUtils.parse(uri.getQuery(), null);
    }
}
