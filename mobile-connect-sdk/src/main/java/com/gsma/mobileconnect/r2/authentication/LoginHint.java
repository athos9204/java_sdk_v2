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

import com.gsma.mobileconnect.r2.constants.LoginHintPrefixes;
import com.gsma.mobileconnect.r2.discovery.ProviderMetadata;
import com.gsma.mobileconnect.r2.discovery.SupportedVersions;
import com.gsma.mobileconnect.r2.utils.ListUtils;
import com.gsma.mobileconnect.r2.utils.Predicate;
import com.gsma.mobileconnect.r2.utils.StringUtils;

import java.util.ArrayList;

/**
 * Utility methods for working with login hints for the auth login hint parameter
 *
 * @since 2.0
 */
public class LoginHint
{
    private static final SupportedVersions DEFAULT_VERSIONS = new SupportedVersions.Builder().build();
    private static final ArrayList<String> RECOGNISED_HINTS = new ArrayList<String>()
    {{
        add(LoginHintPrefixes.MSISDN);
        add(LoginHintPrefixes.ENCRYPTED_MSISDN);
        add(LoginHintPrefixes.PCR);
    }};

    /**
     * Is login hint with MSISDN supported by the target provider
     * @param providerMetadata Provider Metadata received during the discovery phase
     * @return True if format MSISDN:xxxxxxxxxx is supported
     */
    public static boolean isSupportedForMsisdn(final ProviderMetadata providerMetadata)
    {
        return isSupportedFor(providerMetadata, LoginHintPrefixes.MSISDN);
    }

    /**
     * Is login hint with Encrypted MSISDN (SubscriberId) supported by the target provider
     * @param providerMetadata Provider Metadata received during the discovery phase
     * @return True if format ENCRYPTED_MSISDN:xxxxxxxxxx is supported
     */
    public static boolean isSupportedForEncryptedMsisdn(final ProviderMetadata providerMetadata)
    {
        return isSupportedFor(providerMetadata, LoginHintPrefixes.ENCRYPTED_MSISDN);
    }

    /**
     * Is login hint with PCR (Pseudonymous Customer Reference) supported by the target provider
     * @param providerMetadata Provider Metadata received during the discovery phase
     * @return True if format PCR:xxxxxxxxxx is supported
     */
    public static boolean isSupportedForPcr(final ProviderMetadata providerMetadata)
    {
        return isSupportedFor(providerMetadata, LoginHintPrefixes.PCR);
    }

    /**
     * Is login hint with specified prefix supported by the target provider
     * @param providerMetadata Provider Metadata received during the discovery phase
     * @param prefix Prefix to check for login hint support
     * @return True if format ${prefix}:xxxxxxxxxx is supported
     */
    public static boolean isSupportedFor(final ProviderMetadata providerMetadata, final String prefix)
    {
        if (providerMetadata == null ||
            providerMetadata.getLoginHintMethodsSupported() == null ||
            providerMetadata.getLoginHintMethodsSupported().size() == 0)
        {
            SupportedVersions supportedVersions = (providerMetadata != null) ? providerMetadata.getMobileConnectVersionSupported() : DEFAULT_VERSIONS;

            // if not a recognised prefix, then it is not supported if no data to state it is supported
            if (ListUtils.firstMatch(RECOGNISED_HINTS, new Predicate<String>()
            {
                @Override
                public boolean apply(final String input)
                {
                    return input.equalsIgnoreCase(prefix);
                }
            }) == null)
            {
                return false;
            }

            // If we are on 1.2 or greater then currently all recognised prefixes are assumed supported
            if (supportedVersions.isVersionSupported("1.2"))
            {
                return true;
            }

            // If we aren't at 1.2 or greater then we must be on 1.1 and therefore only MSISDN and encrypted are supported
            if (prefix != LoginHintPrefixes.ENCRYPTED_MSISDN && prefix != LoginHintPrefixes.MSISDN)
            {
                return false;
            }

            return true;
        }
        return ListUtils.firstMatch(providerMetadata.getLoginHintMethodsSupported(), new Predicate<String>()
        {
            @Override
            public boolean apply(String input)
            {
                return input.equalsIgnoreCase(prefix);
            }
        }) != null;
    }

    /**
     * Generates login hint for MSISDN value
     * @param msisdn MSISDN value
     * @return Correctly formatted login hint parameter for MSISDN
     */
    public static String generateForMsisdn(final String msisdn)
    {
        return generateFor(LoginHintPrefixes.MSISDN, msisdn.replaceAll("\\+",""));
    }

    /**
     * Generates login hint for Encrypted MSISDN (SubscriberId) value
     * @param encryptedMsisdn Encrypted MSISDN value
     * @return Correctly formatted login hint parameter for Encrypted MSISDN
     */
    public static String generateForEncryptedMsisdn(final String encryptedMsisdn)
    {
        return generateFor(LoginHintPrefixes.ENCRYPTED_MSISDN, encryptedMsisdn);
    }

    /**
     * Generates login hint for PCR (Pseudonymous Customer Reference) value
     * @param pcr PCR (Pseudonymous Customer Reference)
     * @return Correctly formatted login hint parameter for PCR (Pseudonymous Customer Reference)
     */
    public static String generateForPcr(final String pcr)
    {
        return generateFor(LoginHintPrefixes.PCR, pcr);
    }

    /**
     * Generates a login hint for the specified prefix with the specified value.
     * This method will not check that the prefix is recognised or supported, it is assumed that it is supported.
     * @param prefix Prefix to use
     * @param value Value to use
     * @return Correctly formatted login hint for prefix and value
     */
    public static String generateFor(final String prefix, final String value)
    {
        if (StringUtils.isNullOrEmpty(prefix) || StringUtils.isNullOrEmpty(value))
        {
            return null;
        }
        return String.format("%s:%s", prefix, value);
    }
}
