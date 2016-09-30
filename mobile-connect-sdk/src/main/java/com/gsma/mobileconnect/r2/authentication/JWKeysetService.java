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
package com.gsma.mobileconnect.r2.authentication;

import com.gsma.mobileconnect.r2.cache.CacheAccessException;
import com.gsma.mobileconnect.r2.cache.ICache;
import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import com.gsma.mobileconnect.r2.rest.IRestClient;
import com.gsma.mobileconnect.r2.rest.RequestFailedException;
import com.gsma.mobileconnect.r2.rest.RestResponse;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Concrete implementation see {@link IJWKeysetService}
 *
 * @since 2.0
 */
public class JWKeysetService implements IJWKeysetService
{
    final private IRestClient restClient;
    final private ICache iCache;
    // TODO: 9/6/2016 relook at instantiating this, can it be instantiated elsewhere higher up?
    final ExecutorService executorService = Executors.newCachedThreadPool();
    final JacksonJsonService jacksonJsonService = new JacksonJsonService();

    /**
     * Creates an instance of the JWKeysetService with a configured cache
     *
     * @param Builder Builder
     */
    public JWKeysetService(final Builder Builder)
    {
        this.restClient = Builder.restClient;
        this.iCache = Builder.iCache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<JWKeyset> retrieveJwksAsync(final String url)
    {
        return this.executorService.submit(new Callable<JWKeyset>()
        {
            @Override
            public JWKeyset call() throws Exception
            {
                return JWKeysetService.this.retrieveJwks(url);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    // TODO: 9/6/2016 need to think about these exceptions properly
    public JWKeyset retrieveJwks(final String url)
        throws CacheAccessException, RequestFailedException, JsonDeserializationException
    {
        final JWKeyset cachedJWKeyset = retrieveFromCache(url);
        if (cachedJWKeyset != null && !cachedJWKeyset.hasExpired())
        {
            return cachedJWKeyset;
        }
        final RestResponse response = this.restClient.get(URI.create(url), null, null, null, null);
        final JWKeyset jwKeyset =
            this.jacksonJsonService.deserialize(response.getContent(), JWKeyset.class);

        addToCache(url, jwKeyset);

        return jwKeyset;
    }

    private JWKeyset retrieveFromCache(final String url) throws CacheAccessException
    {
        if (this.iCache == null)
        {
            return null;
        }
        return this.iCache.get(url, JWKeyset.class);
    }

    private void addToCache(final String url, final JWKeyset jwKeyset) throws CacheAccessException
    {
        if (this.iCache != null && jwKeyset != null)
        {
            this.iCache.add(url, jwKeyset);
        }
    }

    public static final class Builder
    {
        private IRestClient restClient;
        private ICache iCache;

        public Builder()
        {
        }

        public Builder withRestClient(final IRestClient restClient)
        {
            this.restClient = restClient;
            return this;
        }

        public Builder withICache(final ICache iCache)
        {
            this.iCache = iCache;
            return this;
        }

        public JWKeysetService build()
        {
            return new JWKeysetService(this);
        }
    }
}
