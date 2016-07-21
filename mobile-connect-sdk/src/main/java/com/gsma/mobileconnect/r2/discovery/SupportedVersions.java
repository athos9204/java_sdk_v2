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
package com.gsma.mobileconnect.r2.discovery;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gsma.mobileconnect.r2.MobileConnectVersions;
import com.gsma.mobileconnect.r2.constants.Scopes;
import com.gsma.mobileconnect.r2.utils.IBuilder;
import com.gsma.mobileconnect.r2.utils.ObjectUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Storage for supported mobile connect versions.
 *
 * @see ProviderMetadata#getMobileConnectVersionSupported()
 * @since 2.0
 */
@JsonDeserialize(using = SupportedVersions.JacksonDeserializer.class)
@JsonSerialize(using = SupportedVersions.JacksonSerializer.class)
public class SupportedVersions
{
    private final Map<String, String> versions;

    private SupportedVersions(final Map<String, String> versionSupport)
    {
        this.versions = versionSupport != null
                        ? Collections.unmodifiableMap(new HashMap<String, String>(versionSupport))
                        : MobileConnectVersions.DEFAULT_SUPPORTED_VERSIONS;
    }

    /**
     * Gets the available mobile connect version for the specified scope value.
     * If versions aren't available then configured default versions will be used.
     *
     * @param scope Scope value to retrieve supported version for
     * @return the supported version.
     */
    public String getSupportedVersion(final String scope)
    {
        ObjectUtils.requireNonNull(scope, "scope");

        final String version = ObjectUtils.defaultIfNull(this.versions.get(scope),
            this.versions.get(Scopes.MOBILECONNECT));

        return MobileConnectVersions.coerceVersion(version, scope);
    }

    public static class Builder implements IBuilder<SupportedVersions>
    {
        private final Map<String, String> versions;

        public Builder()
        {
            this.versions = new HashMap<String, String>();
        }

        public Builder(final SupportedVersions versions)
        {
            if (versions != null)
            {
                this.versions = versions.versions;
            }
            else
            {
                this.versions = new HashMap<String, String>();
            }
        }

        public Builder addSupportedVersion(final String scope, final String version)
        {
            ObjectUtils.requireNonNull(scope, "scope");
            ObjectUtils.requireNonNull(version, "version");

            this.versions.put(scope, version);
            return this;
        }

        @Override
        public SupportedVersions build()
        {
            return new SupportedVersions(this.versions);
        }
    }


    protected static class JacksonDeserializer extends JsonDeserializer<SupportedVersions>
    {
        @Override
        public SupportedVersions deserialize(final JsonParser jsonParser,
            final DeserializationContext deserializationContext) throws IOException
        {
            final JsonNode node = jsonParser.getCodec().readTree(jsonParser);

            final SupportedVersions.Builder builder = new SupportedVersions.Builder();

            for (final JsonNode version : node)
            {
                for (final Iterator<Map.Entry<String, JsonNode>> it =
                     version.fields(); it.hasNext(); )
                {
                    final Map.Entry<String, JsonNode> field = it.next();
                    builder.addSupportedVersion(field.getKey(), field.getValue().asText());
                }
            }

            return builder.build();
        }
    }


    protected static class JacksonSerializer extends JsonSerializer<SupportedVersions>
    {
        @Override
        public void serialize(final SupportedVersions versions, final JsonGenerator jsonGenerator,
            final SerializerProvider serializerProvider) throws IOException
        {
            jsonGenerator.writeStartArray();

            for (final Map.Entry<String, String> entry : versions.versions.entrySet())
            {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeObjectField(entry.getKey(), entry.getValue());
                jsonGenerator.writeEndObject();
            }

            jsonGenerator.writeEndArray();
        }
    }
}
