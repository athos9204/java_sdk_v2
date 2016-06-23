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

package com.gsma.mobileconnect.discovery;

import java.util.List;

/**
 * Optional parameters for the {@link IDiscovery#startAutomatedOperatorDiscovery(IPreferences, String, DiscoveryOptions, List, IDiscoveryResponseCallback)} method.
 * <p>
 * All the values are optional.
 */
public class DiscoveryOptions
{
    /**
     * Default timeout for the Rest call in milliseconds.
     */
    private static final int DEFAULT_TIMEOUT = 30000;

    /**
     * Default value for manually select.
     */
    private static final boolean DEFAULT_MANUALLY_SELECT = false;

    /**
     * Default value for cookies enabled.
     */
    private static final boolean DEFAULT_COOKIES_ENABLED = true;

    private boolean manuallySelect;
    private String identifiedMCC;
    private String identifiedMNC;
    private boolean cookiesEnabled;
    private Boolean usingMobileData;
    private String localClientIP;
    private int timeout;
    private String clientIP;

    /**
     * Get manually select.
     *
     * @return True if manual select requested.
     */
    public boolean isManuallySelect()
    {
        return manuallySelect;
    }

    /**
     * Set manually select.
     *
     * @param newValue New manually select value.
     */
    public void setManuallySelect(boolean newValue)
    {
        manuallySelect = newValue;
    }

    /**
     * Return the Identified Mobile Country Code.
     *
     * @return The Mobile Country Code value.
     */
    public String getIdentifiedMCC()
    {
        return identifiedMCC;
    }

    /**
     * Set the Identified Mobile Country Code.
     *
     * @param newValue New Mobile Country Code value.
     */
    public void setIdentifiedMCC(String newValue)
    {
        identifiedMCC = newValue;
    }

    /**
     * Return the Identified Mobile Network Code.
     *
     * @return The Mobile Network Code value.
     */
    public String getIdentifiedMNC()
    {
        return identifiedMNC;
    }

    /**
     * Set the Identified Mobile Network Code.
     *
     * @param newValue New Mobile Network Code value.
     */
    public void setIdentifiedMNC(String newValue)
    {
        identifiedMNC = newValue;
    }

    /**
     * Are cookies to be stored/sent.
     *
     * @return True if cookies are to be sent.
     */
    public boolean isCookiesEnabled()
    {
        return cookiesEnabled;
    }

    /**
     * Are cookies to be stored/sent.
     *
     * @param newValue True if cookies are to be sent.
     */
    public void setCookiesEnabled(boolean newValue)
    {
        cookiesEnabled = newValue;
    }

    /**
     * Return whether the application is using mobile data.
     *
     * @return True if the application is using mobile data.
     */
    public Boolean isUsingMobileData()
    {
        return usingMobileData;
    }

    /**
     * Specify whether the application is using mobile data.
     * <p>
     * Set to "true" if your application is able to determine that the user is accessing the service via mobile data.
     * This tells the Discovery Service to discover using the mobile-network.
     *
     * @param newValue New value.
     */
    public void setUsingMobileData(Boolean newValue)
    {
        usingMobileData = newValue;
    }

    /**
     * The current local IP address of the client application i.e. the actual IP address
     * currently allocated to the device running the application.
     * <p>
     * This can be used within header injection processes from the MNO to confirm the application is directly using
     * a mobile data connection from the consumption device rather than MiFi/WiFi to mobile hotspot.
     *
     * @return The actual IP address of the client application.
     */
    public String getLocalClientIP()
    {
        return localClientIP;
    }

    /**
     * The current local IP address of the client application i.e. the actual IP address
     * currently allocated to the device running the application.
     * <p>
     * This can be used within header injection processes from the MNO to confirm the application is directly using
     * a mobile data connection from the consumption device rather than MiFi/WiFi to mobile hotspot.
     *
     * @param newValue The actual IP address of the client application.
     */
    public void setLocalClientIP(String newValue)
    {
        localClientIP = newValue;
    }

    /**
     * Return the timeout value used by the SDK for the Discovery Rest request.
     *
     * @return The timeout to be used.
     */
    public int getTimeout()
    {
        return timeout;
    }

    /**
     * Set the timeout of a Discovery request.
     *
     * The timeout (in milliseconds) to be used by the SDK when making a Discovery request.
     *
     * @param newValue New timeout value.
     */
    public void setTimeout(int newValue)
    {
        timeout = newValue;
    }

    /**
     *  Allows a server application to indicate the 'public IP address' of the connection from
     *  a client application/mobile browser to the server.
     *  <p>
     *  This is used in place of the public IP address normally detected by the discovery service. Note this will usually differ
     *  from the Local-Client-IP address, and the public IP address detected by the
     *  application server should not be used for the Local-Client-IP address.
     *
     * @return The client IP
     */
    public String getClientIP()
    {
        return clientIP;
    }

    /**
     *  Allows a server application to indicate the 'public IP address' of the connection from
     *  a client application/mobile browser to the server.
     *  <p>
     *  This is used in place of the public IP address normally detected by the discovery service. Note this will usually differ
     *  from the Local-Client-IP address, and the public IP address detected by the
     *  application server should not be used for the Local-Client-IP address.
     *
     * @param clientIP The client IP as detected by the server.
     */
    public void setClientIP(String clientIP)
    {
        this.clientIP = clientIP;
    }

    public DiscoveryOptions()
    {
        timeout = DEFAULT_TIMEOUT;
        manuallySelect = DEFAULT_MANUALLY_SELECT;
        cookiesEnabled = DEFAULT_COOKIES_ENABLED;
    }
}
