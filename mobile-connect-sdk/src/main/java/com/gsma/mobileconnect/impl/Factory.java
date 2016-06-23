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

import com.gsma.mobileconnect.cache.DiscoveryCacheHashMapImpl;
import com.gsma.mobileconnect.cache.IDiscoveryCache;
import com.gsma.mobileconnect.discovery.IDiscovery;
import com.gsma.mobileconnect.oidc.IOIDC;
import com.gsma.mobileconnect.utils.RestClient;

public class Factory
{
    /**
     * Factory method to create an implementation of the IDiscovery interface.
     * <p>
     * If cache is not specified no cache will be used.
     *
     * @param cache The cache to be used. (Optional).
     * @return An implementation of {@link IDiscovery}
     */
    static public IDiscovery getDiscovery(IDiscoveryCache cache)
    {
        return new DiscoveryImpl(cache, new RestClient());
    }

    /**
     * Factory method to create an implementation of the IDiscovery interface.
     * <p>
     * This version is primarily for testing.
     *
     * @param cache The cache to be used. (Optional).
     * @param restClient The rest client to be used. (Required).
     * @return An implementation of {@link IDiscovery}
     */
    static public IDiscovery getDiscovery(IDiscoveryCache cache, RestClient restClient)
    {
        return new DiscoveryImpl(cache, restClient);
    }

    /**
     * Factory method to create an implementation of the {@link IOIDC} interface.
     *
     * @return An implementation of {@link IOIDC}
     */
    static public IOIDC getOIDC()
    {
        return getOIDC(new RestClient());
    }

    /**
     * Factory method to create an implementation of the {@link IOIDC} interface.
     * <p>
     * This version is primarily for testing.
     *
     * @param restClient The rest client to be used. (Required).
     * @return An implementation of {@link IOIDC}
     */
    static public IOIDC getOIDC(RestClient restClient)
    {
        return new OIDCImpl(restClient);
    }

    /**
     * Factory method to create a default implementation of {@link IDiscoveryCache} that uses a {@link java.util.concurrent.ConcurrentHashMap}
     * as a backing store.
     *
     * @return A default implementation of {@link IDiscoveryCache}
     */
    static public IDiscoveryCache getDefaultDiscoveryCache()
    {
        return new DiscoveryCacheHashMapImpl();
    }
}

