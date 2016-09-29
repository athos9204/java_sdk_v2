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
package com.gsma.mobileconnect.r2.json;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gsma.mobileconnect.r2.utils.IBuilder;
import com.gsma.mobileconnect.r2.utils.ListUtils;

import java.util.List;

/**
 * Object for deserialization of Discovery Response content.
 *
 * @since 2.0
 */
@JsonDeserialize(builder = DiscoveryResponseData.Builder.class)
public class DiscoveryResponseData
{
    private final long ttl;
    private final String error;
    private final String description;
    private final List<Link> links;
    private final Response response;
    private String subscriberId;
    private String applicationShortName;

    private DiscoveryResponseData(final Builder builder)
    {
        this.ttl = builder.ttl;
        this.subscriberId = builder.subscriberId;
        this.error = builder.error;
        this.description = builder.description;
        this.links = builder.links;
        this.response = builder.response;
        this.applicationShortName = builder.applicationShortName;
    }

    public long getTtl()
    {
        return this.ttl;
    }

    public String getSubscriberId()
    {
        return this.subscriberId;
    }

    public void clearSubscriberId()
    {
        this.subscriberId = null;
    }

    public String getError()
    {
        return this.error;
    }

    public String getDescription()
    {
        return this.description;
    }

    public List<Link> getLinks()
    {
        return this.links;
    }

    public Response getResponse()
    {
        return this.response;
    }

    public String getApplicationShortName()
    {
        return applicationShortName;
    }

    public static final class Builder implements IBuilder<DiscoveryResponseData>
    {
        private long ttl = 0L;
        private String subscriberId = null;
        private String error = null;
        private String description = null;
        private List<Link> links = null;
        private Response response = null;
        private String applicationShortName = null;

        public Builder()
        {
            // default constructor
        }

        public Builder(final DiscoveryResponseData responseData)
        {
            if (responseData != null)
            {
                this.ttl = responseData.ttl;
                this.subscriberId = responseData.subscriberId;
                this.error = responseData.error;
                this.description = responseData.description;
                this.links = responseData.links;
                this.response = responseData.response;
                this.applicationShortName = responseData.applicationShortName;
            }
        }

        public Builder withTtl(final long val)
        {
            this.ttl = val;
            return this;
        }

        public Builder withSubscriberId(final String val)
        {
            this.subscriberId = val;
            return this;
        }

        public Builder withError(final String val)
        {
            this.error = val;
            return this;
        }

        public Builder withDescription(final String val)
        {
            this.description = val;
            return this;
        }

        public Builder withLinks(final List<Link> val)
        {
            this.links = ListUtils.immutableList(val);
            return this;
        }

        public Builder withResponse(final Response val)
        {
            this.response = val;
            return this;
        }

        public Builder withApplicationShortName(final String val)
        {
            this.applicationShortName = val;
            return this;
        }

        @Override
        public DiscoveryResponseData build()
        {
            if (this.links == null && this.response != null)
            {
                final Apis apis = this.response.getApis();
                if (apis != null)
                {
                    final OperatorId operatorId = apis.getOperatorId();
                    if (operatorId != null)
                    {
                        this.links = operatorId.getLink();
                    }
                }
            }

            if (this.applicationShortName == null && this.response != null)
            {
                this.applicationShortName = this.response.getApplicationShortName();
            }

            return new DiscoveryResponseData(this);
        }
    }
}
