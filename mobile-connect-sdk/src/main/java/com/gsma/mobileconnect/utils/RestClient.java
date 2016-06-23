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

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class to make Rest requests.
 */
public class RestClient
{
    /**
     * Build a HttpClientContext that will preemptively authenticate using Basic Authentication
     *
     * @param username Username for credentials
     * @param password Password for credentials
     * @param uriForRealm uri used to determine the authentication realm
     * @return A HttpClientContext that will preemptively authenticate using Basic Authentication
     */
    public HttpClientContext getHttpClientContext(String username, String password, URI uriForRealm)
    {
        String host = URIUtils.extractHost(uriForRealm).getHostName();
        int port = uriForRealm.getPort();
        if(port==-1)
        {
            if(Constants.HTTP_SCHEME.equalsIgnoreCase(uriForRealm.getScheme()))
            {
                port = Constants.HTTP_PORT;
            }
            else if(Constants.HTTPS_SCHEME.equalsIgnoreCase(uriForRealm.getScheme()))
            {
                port = Constants.HTTPS_PORT;
            }
        }

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(host, port), new UsernamePasswordCredentials(username, password));

        AuthCache authCache = new BasicAuthCache();
        authCache.put(new HttpHost(host, port, uriForRealm.getScheme()), new BasicScheme());

        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credentialsProvider);
        context.setAuthCache(authCache);

        return context;
    }

    /**
     * Make the specified request with the specified client context.
     * <p>
     * The specified cookies will be added to the request. A request will be aborted if it exceeds the specified timeout.
     * Non Json responses are converted and thrown as RestExceptions.
     * <p>
     * Ensures that all closable resources are closed.
     *
     * @param httpRequest The request to execute.
     * @param context The context to use when executing the request.
     * @param timeout The maximum time in milliseconds the request is allowed to take.
     * @param cookiesToProxy The cookies to add to the request.
     * @return The Rest response.
     * @throws RestException Thrown if the request exceeds the specified timeout, or a non Json response is received.
     * @throws IOException
     */
    public RestResponse callRestEndPoint(HttpRequestBase httpRequest, HttpClientContext context, int timeout, List<KeyValuePair> cookiesToProxy)
            throws RestException, IOException
    {
        CookieStore cookieStore = buildCookieStore(httpRequest.getURI().getHost(), cookiesToProxy);
        CloseableHttpClient closeableHttpClient = getHttpClient(cookieStore);
        try
        {
            CloseableHttpResponse closeableHttpResponse = executeRequest(closeableHttpClient, httpRequest, context, timeout);
            try
            {
                RestResponse restResponse = buildRestResponse(httpRequest, closeableHttpResponse);
                checkRestResponse(restResponse);
                return restResponse;
            }
            finally
            {
                closeableHttpResponse.close();
            }
        }
        finally
        {
            closeableHttpClient.close();
        }
    }

    /**
     * Build a cookie store that contains the specified cookies.
     * <p>
     * The domain of the cookies is set to the specified host.
     *
     * @param host The domain of the cookies.
     * @param cookiesToProxy The cookies to add to the store.
     * @return The cookie store.
     */
    private CookieStore buildCookieStore(String host, List<KeyValuePair> cookiesToProxy)
    {
        CookieStore cookieStore = new BasicCookieStore();

        if(null == cookiesToProxy)
        {
            return cookieStore;
        }

        for(KeyValuePair cookieToProxy : cookiesToProxy)
        {
            BasicClientCookie cookie = new BasicClientCookie(cookieToProxy.getKey(), cookieToProxy.getValue());
            cookie.setDomain(host);
            cookieStore.addCookie(cookie);
        }

        return cookieStore;
    }

    /**
     * Build a HttpClient that follows redirects and has the specified cookie store.
     *
     * @param cookieStore The cookie store to associate with the client.
     * @return A HttpClient
     */
    private CloseableHttpClient getHttpClient(CookieStore cookieStore)
    {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        return httpClient;
    }

    /**
     * Execute the given request.
     * <p>
     * Abort the request if it exceeds the specified timeout.
     *
     * @param httpClient The client to use.
     * @param request The request to make.
     * @param context The context to use.
     * @param timeout The timeout to use.
     * @return A Http Response.
     * @throws RestException Thrown if the request fails or times out.
     */
    private CloseableHttpResponse executeRequest(CloseableHttpClient httpClient, final HttpRequestBase request, HttpClientContext context, int timeout)
            throws RestException
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                request.abort();
            }
        }, timeout);

        RequestConfig localConfig = RequestConfig.custom()
                                    .setConnectionRequestTimeout(timeout)
                                    .setConnectTimeout(timeout)
                                    .setSocketTimeout(timeout)
                                    .setCookieSpec(CookieSpecs.STANDARD)
                                    .build();
        request.setConfig(localConfig);
        try
        {
            return httpClient.execute(request, context);
        }
        catch (IOException ex)
        {
            String requestUri = request.getURI().toString();
            if(request.isAborted())
            {
                throw new RestException("Rest end point did not respond", requestUri);
            }
            throw new RestException("Rest call failed", requestUri, ex);
        }
    }

    /**
     * Builds a RestResponse from the given HttpResponse
     *
     * @param request The original request.
     * @param closeableHttpResponse The HttpResponse to build the RestResponse for.
     * @return The RestResponse of the HttpResponse
     * @throws IOException
     */
    private RestResponse buildRestResponse(HttpRequestBase request, CloseableHttpResponse closeableHttpResponse)
            throws IOException
    {
        String requestUri = request.getURI().toString();
        int statusCode = closeableHttpResponse.getStatusLine().getStatusCode();

        HeaderIterator headers = closeableHttpResponse.headerIterator();
        List<KeyValuePair> headerList = new ArrayList<KeyValuePair>(3);
        while(headers.hasNext())
        {
            Header header = headers.nextHeader();
            headerList.add(new KeyValuePair(header.getName(), header.getValue()));
        }

        HttpEntity httpEntity = closeableHttpResponse.getEntity();
        String responseData = EntityUtils.toString(httpEntity);

        return new RestResponse(requestUri, statusCode, headerList, responseData);
    }


    /**
     * Checks the Rest Response converting it to an exception if necessary.
     * <p>
     * Non Json responses are converted to exceptions.
     *
     * @param response The Response to check.
     * @throws RestException
     */
    private static void checkRestResponse(RestResponse response)
            throws RestException
    {
        if(!response.isJsonContent())
        {
            throw new RestException("Invalid response", response);
        }
    }
}
