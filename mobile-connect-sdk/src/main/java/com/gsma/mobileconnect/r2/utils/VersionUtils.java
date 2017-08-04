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
package com.gsma.mobileconnect.r2.utils;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;


/**
 * @since 2.0
 */
public class VersionUtils
{

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionUtils.class);

    private static String CURRENT_SDK_VERSION = getJavaSdkVersion();

    /**
     * Default Constructor
     */
    private VersionUtils()
    {
        /*
        Private Default Constructor
         */
    }
    /**
     * Compares two version numbers in format "n.m" (may be up to four degrees)
     * @param ver1 version 1
     * @param ver2 version 2
     * @return 1 if version 1 is higher, -1 if version 2 is higher, otherwise 0
     */
    public static int versionCompare(final String ver1, final String ver2)
    {
        List<String> vals1 = Arrays.asList(ver1.split("\\."));
        List<String> vals2 = Arrays.asList(ver2.split("\\."));
        int i = 0;
        // set index to first non-equal ordinal or length of shortest version string
        while (i < vals1.size() && i < vals2.size() && vals1.get(i).equals(vals2.get(i))) {
            i++;
        }
        // compare first non-equal ordinal number
        if (i < vals1.size() && i < vals2.size()) {
            int diff = Integer.valueOf(vals1.get(i).replaceAll("[\\D]", "")).compareTo(Integer.valueOf(vals2.get(i).replaceAll("[\\D]", "")));
            return Integer.signum(diff);
        }
        // if one version has more spaces than another, the summation of numbers should be bigger
        // except v1.1.0 should be equal to v1.1 - summing and taking signum solves issue
        return Integer.signum(ListUtils.sum(vals1) - ListUtils.sum(vals2));
    }

    /**
     * Looks current SDK version.
     * @return version of sdk in parent of pom.xml file.
     */
    public static String getCurrentSdkVersion() {
        if (!CURRENT_SDK_VERSION.contains("Android")) {
            CURRENT_SDK_VERSION = getJavaSdkVersion();
        }
        LOGGER.info(String.format("Current SDK version: %s.", CURRENT_SDK_VERSION));
        return CURRENT_SDK_VERSION;
    }

    private static String getJavaSdkVersion() {
        final String pomFile = "pom.xml";
        final String pomFilePath = System.getProperty("user.dir") +
                File.separator + pomFile;
        String sdkVersion;
        try {
            Model model = new MavenXpp3Reader().read(new FileReader(pomFilePath));
            sdkVersion = prepareVersionFormat("Java", new MavenProject(model).getVersion());
        }catch(NoClassDefFoundError | Exception e){
            sdkVersion = null;
            LOGGER.error("Unable to find {} file", pomFilePath);
            e.printStackTrace();
        }
        return sdkVersion;
    }

    public static String prepareVersionFormat(final String platform, final String version) {
        return String.format("%s-%s", platform, version);
    }

    /**
     * Use only outside this code
     * @param version
     */
    public static void setCurrentSdkVersion(final String version) {
        CURRENT_SDK_VERSION = version;
    }
}
