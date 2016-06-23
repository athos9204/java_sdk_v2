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

import com.gsma.mobileconnect.discovery.DiscoveryException;
import com.gsma.mobileconnect.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.discovery.IDiscovery;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Series of tests to check that the discovery sdk is working.
 */

public class DiscoverySanityTest
{

    @Test
    public void startAutomatedOperatorDiscovery_withDefaults_shouldSucceed() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null);
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();
        Config config = new Config();

        // WHEN
        discovery.startAutomatedOperatorDiscovery(
                config,
                Config.REDIRECT_URL,
                null,
                null,
                captureDiscoveryResponse);

        // THEN
        DiscoveryResponse discoveryResponse = captureDiscoveryResponse.getDiscoveryResponse();
        assertFalse("Discovery Response is an error", discovery.isErrorResponse(discoveryResponse));
        assertTrue("Discovery Response does not specify operation selection", discovery.isOperatorSelectionRequired(discoveryResponse));
    }

    @Test
    public void getOperatorSelectionURL_withDefaults_shouldSucceed() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null);
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();
        Config config = new Config();

        // WHEN
        discovery.getOperatorSelectionURL(
                config,
                Config.REDIRECT_URL,
                null,
                captureDiscoveryResponse);

        // THEN
        DiscoveryResponse discoveryResponse = captureDiscoveryResponse.getDiscoveryResponse();
        assertFalse("Discovery Response is an error", discovery.isErrorResponse(discoveryResponse));
        assertTrue("Discovery Response does not specify operation selection", discovery.isOperatorSelectionRequired(discoveryResponse));
    }

    @Test
    public void completeSelectedOperatorDiscovery_withDefaults_shouldSucceed() throws DiscoveryException
    {
        // GIVEN
        IDiscovery discovery = Factory.getDiscovery(null);
        CaptureDiscoveryResponse captureDiscoveryResponse = new CaptureDiscoveryResponse();
        Config config = new Config();

        // WHEN
        discovery.completeSelectedOperatorDiscovery(
                config,
                Config.REDIRECT_URL,
                "901", "01",
                null, null,
                captureDiscoveryResponse);

        // THEN
        DiscoveryResponse discoveryResponse = captureDiscoveryResponse.getDiscoveryResponse();
        assertFalse("Discovery Response is an error", discovery.isErrorResponse(discoveryResponse));
        assertFalse("Discovery Response specifies that operation selection", discovery.isOperatorSelectionRequired(discoveryResponse));
    }

}
