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

import com.gsma.mobileconnect.discovery.DiscoveryResponse;

import java.io.Serializable;

/**
 * Class used by application code to hold state between calls to the SDK.
 */
public class MobileConnectState
    implements Serializable
{
    private static final long serialVersionUID = -1796401958435472342L;

    final private String encryptedMSISDN;
    final private DiscoveryResponse discoveryResponse;
    final private String state;
    final private String nonce;

    /**
     * The encrypted MSISDN,
     * <p>
     * Optional.
     * <p>
     * This is also known as the subscriber_id.
     *
     * @return The encrypted MSISDN.
     */
    public String getEncryptedMSISDN()
    {
        return encryptedMSISDN;
    }

    /**
     * A Discovery Response obtained from {@link com.gsma.mobileconnect.discovery.IDiscovery}
     *
     * @return A Discovery Response
     */
    public DiscoveryResponse getDiscoveryResponse()
    {
        return discoveryResponse;
    }

    /**
     * A state value used in authorization.
     *
     * @return The state value.
     */
    public String getState()
    {
        return state;
    }

    /**
     * A nonce value used in authorization.
     *
     * @return The nonce value.
     */
    public String getNonce()
    {
        return nonce;
    }

    /**
     * Basic constructor allowing all properties to be set.
     *
     * @param discoveryResponse A discovery response.
     * @param encryptedMSISDN An encrypted MSISDN.
     * @param state A state value,
     * @param nonce A nonce value.
     */
    public MobileConnectState(DiscoveryResponse discoveryResponse, String encryptedMSISDN, String state, String nonce)
    {
        this.discoveryResponse = discoveryResponse;
        this.encryptedMSISDN = encryptedMSISDN;
        this.state = state;
        this.nonce = nonce;
    }

    /**
     * Construct a MobileConnectState with the Discovery Response and encrypted MSISDN properties initialised.
     *
     * @param discoveryResponse A Discovery Response.
     * @param encryptedMSISDN An encrypted MSISDN.
     * @return A MobileConnectState.
     */
    static public MobileConnectState withDiscoveryResponseAndEncryptedMSISDN(DiscoveryResponse discoveryResponse, String encryptedMSISDN)
    {
        return new MobileConnectState(discoveryResponse, encryptedMSISDN, null, null);
    }

    /**
     * Merge the current state instance and the state and nonce into a new MobileConnectState instance.
     *
     * @param currentState An instance used as the basis of the new MobileConnectState instance.
     * @param state The state value of the new MobileConnectState instance.
     * @param nonce The nonce value of the new MobileConnectState instance.
     * @return A new MobileConnectState instance.
     */
    static public MobileConnectState mergeStateAndNonce(MobileConnectState currentState, String state, String nonce)
    {
        return new MobileConnectState(currentState.getDiscoveryResponse(), currentState.getEncryptedMSISDN(), state, nonce);
    }
}
