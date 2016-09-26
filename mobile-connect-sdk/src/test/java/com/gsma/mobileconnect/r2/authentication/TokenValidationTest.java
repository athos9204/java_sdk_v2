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

import com.gsma.mobileconnect.r2.json.JacksonJsonService;
import com.gsma.mobileconnect.r2.json.JsonDeserializationException;
import org.testng.annotations.Test;

import java.security.InvalidKeyException;

import static org.testng.Assert.assertEquals;

/**
 * @since 2.0
 */
public class TokenValidationTest
{
//    private final String nonce = "81991496-48bb-4d13-bd0c-117d994411a6";
//    private final String clientId = "x-ZWRhNjU3OWI3MGIwYTRh";
//    private final String issuer = "https://reference.mobileconnect.io/mobileconnect";
//    private final long maxAge = 300 * 24 * 60 * 60; //seconds

    private String nonce = "1234567890";
    private String clientId = "x-clientid-x";
    private String issuer = "http://mobileconnect.io";
    private final long maxAge = 300 * 24 * 60 * 60; //seconds

    // TODO: 26/09/16 extra tests around claims
    @Test
    public void validateIdTokenShouldReturnInvalidSignature()
        throws JsonDeserializationException, InvalidKeyException
    {
        final String jwksJson =
            "{\"keys\":[{\"alg\":\"RS256\",\"e\":\"AQAB\",\"n\":\"hzr2li5ABVbbQ4BvdDskl6hejaVw0tIDYO-C0GBr5lRA-AXtmCO7bh0CEC9-R6mqctkzUhVnU22Vrj-B1J0JtJoaya9VTC3DdhzI_-7kxtIc5vrHq-ss5wo8-tK7UqtKLSRf9DcyZA0H9FEABbO5Qfvh-cfK4EI_ytA5UBZgO322RVYgQ9Do0D_-jf90dcuUgoxz_JTAOpVNc0u_m9LxGnGL3GhMbxLaX3eUublD40aK0nS2k37dOYOpQHxuAS8BZxLvS6900qqaZ6z0kwZ2WFq-hhk3Imd6fweS724fzqVslY7rHpM5n7z5m7s1ArurU1dBC1Dxw1Hzn6ZeJkEaZQ\",\"kty\":\"RSA\",\"use\":\"sig\"}]}";
        final String idToken =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJub25jZSI6IjEyMzQ1Njc4OTAiLCJhdWQiOiJ4LWNsaWVudGlkLXgiLCJhenAiOiJ4LWNsaWVudGlkLXgiLCJpc3MiOiJodHRwOi8vbW9iaWxlY29ubmVjdC5pbyIsImV4cCI6MjE0NzQ4MzY0NywiYXV0aF90aW1lIjoyMTQ3NDgzNjQ3LCJpYXQiOjE0NzEwMDczMjd9.U9c5iuybG4GIvrbQH5BT9AgllRbPL6SuIzL4Y3MW7VlCVIQOc_HFfkiLa0LNvqZiP-kFlADmnkzuuQxPq7IyaOILVYct20mrcOb_U_zMli4jg-t9P3BxHaq3ds9JlLBjz0oewd01ZQtWHgRnrGymfKAIojzHlde-aePuL1M26Eld5zoKQvCLcKAynZsjKsWF_6YdLk-uhlC5ofMOaOoPirPSPAxYvbj91z3o9XIgSHoU-umN7AJ6UQ4H-ulfftlRGK8hz0Yzpf2MHOy9OHg1u3ayfCaaf8g5zKGngcz0LgK9VAw2B31xJw-RHkPPh0Hz82FgBc4588oEFC1c22GGTw";
        final JacksonJsonService jacksonJsonService = new JacksonJsonService();

        final JWKeyset jwKeyset = jacksonJsonService.deserialize(jwksJson, JWKeyset.class);
        final TokenValidationResult tokenValidationResult = TokenValidation.validateIdToken(idToken, clientId, issuer, nonce, maxAge, jwKeyset, jacksonJsonService);

        assertEquals(tokenValidationResult, TokenValidationResult.InvalidSignature);
    }

    @Test
    public void validMatchingRSAAlgorithmShouldVerifySignature()
        throws JsonDeserializationException, InvalidKeyException
    {
        final String jwksJson =
            "{\"keys\":[{\"kty\":\"RSA\",\"use\":\"sig\",\"n\":\"ALyIC8vj1tqEIvAvpDMQfgosw13LpBS9Z2lsMmuaLDNJjN_FKIb-HVR2qtMj7AYC0-wYJhGxJpTXJTVRRDz-zLN7uredNxuhVj76vmU1tfvEN0Xq2INYoWeJ3d9fZtkBgKl7Enfkgz858DLAfZuJzDycOzuZXR5r29zXMDstT5F5\",\"e\":\"AQAB\",\"kid\":\"PHPOP-00\"}]}";
        final String idToken =
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IlBIUE9QLTAwIn0.eyJpc3MiOiJodHRwczpcL1wvcmVmZXJlbmNlLm1vYmlsZWNvbm5lY3QuaW9cL21vYmlsZWNvbm5lY3QiLCJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJhdWQiOlsieC1aV1JoTmpVM09XSTNNR0l3WVRSaCJdLCJleHAiOjE0NzQ2MjYzMzAsImlhdCI6MTQ3NDYyNjAzMCwibm9uY2UiOiI4MTk5MTQ5Ni00OGJiLTRkMTMtYmQwYy0xMTdkOTk0NDExYTYiLCJhdF9oYXNoIjoiNTZGMXo3RjZ3eWhUYUhVY1ZGY0xJQSIsImF1dGhfdGltZSI6MTQ3NDYyNjAyMCwiYWNyIjoiMiIsImFtciI6WyJTSU1fUElOIl0sImF6cCI6IngtWldSaE5qVTNPV0kzTUdJd1lUUmgifQ.TYcvfIHeKigkvjYta6fy90EffiA6u6NFCSIPlPM2WxEUi8Kxc5JIrjXnM8l0rFJOLmgNFUBpSqIRhuxwZkUV52KWf8jzswi3jTI8wEjonbjgviz7c6WzlZdb0Pw5kUEWy2xMam7VprESphPaIkHCDor2yR2g6Uq3Wtqyg7MCqek";
        final JacksonJsonService jacksonJsonService = new JacksonJsonService();

        final JWKeyset jwKeyset = jacksonJsonService.deserialize(jwksJson, JWKeyset.class);
        final TokenValidationResult tokenValidationResult =
            TokenValidation.validateIdTokenSignature(idToken, jwKeyset, jacksonJsonService);

        assertEquals(tokenValidationResult, TokenValidationResult.Valid);
    }

    @Test
    public void invalidMatchingRSAAlgorithmShouldThrowInvalidSignature()
        throws JsonDeserializationException, InvalidKeyException
    {
        final String jwksJson =
            "{\"keys\":[{\"kty\":\"RSA\",\"use\":\"sig\",\"n\":\"LyIC8vj1tqEIvAvpDMQfgosw13LpBS9Z2lsMmuaLDNJjN_FKIb-HVR2qtMj7AYC0-wYJhGxJpTXJTVRRDz-zLN7uredNxuhVj76vmU1tfvEN0Xq2INYoWeJ3d9fZtkBgKl7Enfkgz858DLAfZuJzDycOzuZXR5r29zXMDstT5F5\",\"e\":\"AQAB\",\"kid\":\"PHPOP-00\"}]}";
        final String idToken =
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IlBIUE9QLTAwIn0.eyJpc3MiOiJodHRwczpcL1wvcmVmZXJlbmNlLm1vYmlsZWNvbm5lY3QuaW9cL21vYmlsZWNvbm5lY3QiLCJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJhdWQiOlsieC1aV1JoTmpVM09XSTNNR0l3WVRSaCJdLCJleHAiOjE0NzQ2MjYzMzAsImlhdCI6MTQ3NDYyNjAzMCwibm9uY2UiOiI4MTk5MTQ5Ni00OGJiLTRkMTMtYmQwYy0xMTdkOTk0NDExYTYiLCJhdF9oYXNoIjoiNTZGMXo3RjZ3eWhUYUhVY1ZGY0xJQSIsImF1dGhfdGltZSI6MTQ3NDYyNjAyMCwiYWNyIjoiMiIsImFtciI6WyJTSU1fUElOIl0sImF6cCI6IngtWldSaE5qVTNPV0kzTUdJd1lUUmgifQ.TYcvfIHeKigkvjYta6fy90EffiA6u6NFCSIPlPM2WxEUi8Kxc5JIrjXnM8l0rFJOLmgNFUBpSqIRhuxwZkUV52KWf8jzswi3jTI8wEjonbjgviz7c6WzlZdb0Pw5kUEWy2xMam7VprESphPaIkHCDor2yR2g6Uq3Wtqyg7MCqek";
        final JacksonJsonService jacksonJsonService = new JacksonJsonService();

        final JWKeyset jwKeyset = jacksonJsonService.deserialize(jwksJson, JWKeyset.class);
        final TokenValidationResult tokenValidationResult =
            TokenValidation.validateIdTokenSignature(idToken, jwKeyset, jacksonJsonService);

        assertEquals(tokenValidationResult, TokenValidationResult.InvalidSignature);
    }

    // TODO: 23/09/16 test more signature error cases  -> unsupported algorithm and key misinformed

    // TODO: 23/09/16 test correct hmac key and invalid hmac key
    // TODO: 23/09/16 fix this test
//    @Test
//    public void validMatchingHMACAlgorithmShouldVerifySignature()
//        throws JsonDeserializationException, InvalidKeyException
//    {
//        final String jwksJson =
//            "{\"keys\":[{\"alg\":\"RS256\",\"e\":\"AQAB\",\"n\":\"hzr2li5ABVbbQ4BvdDskl6hejaVw0tIDYO-C0GBr5lRA-AXtmCO7bh0CEC9-R6mqctkzUhVnU22Vrj-B1J0JtJoaya9VTC3DdhzI_-7kxtIc5vrHq-ss5wo8-tK7UqtKLSRf9DcyZA0H9FEABbO5Qfvh-cfK4EI_ytA5UBZgO322RVYgQ9Do0D_-jf90dcuUgoxz_JTAOpVNc0u_m9LxGnGL3GhMbxLaX3eUublD40aK0nS2k37dOYOpQHxuAS8BZxLvS6900qqaZ6z0kwZ2WFq-hhk3Imd6fweS724fzqVslY7rHpM5n7z5m7s1ArurU1dBC1Dxw1Hzn6ZeJkEaZQ\",\"kty\":\"RSA\",\"use\":\"sig\",\"kid\":\"test\"},{\"e\":\"AQAB\",\"n\":\"sj_E_-OM6We6kM3Zl8LFQCbp4J1GA_RArvFo8Y0jLXR1xK20nJ0UIhCR1u4a3WD9dSwDRmcDa-3nT_1g5mzMOjBBO1I0VFDG61LyTkrbHhaz-VtRKjMcZMaVPHGC-nRogg92984s-ahO-Q4hkE05tiO96u3xj4S8_A3bsMIQCLQRYKS9_ovl_HxEJne3NFRkSZiiTym5g0H_nOrl2RlBYfV7GPst8Vzq45Mn1uDtCHocSeM3zunLG8TNOz0t6U_s0hAd0gKukoxgaGc1JDSsRWNs8r9SPniRMclKkcMWpdZQbLdT9ARsEB7i6w4x4C1p9i75PloXhwE-EOZ9kCeOtw\",\"kty\":\"RSA\",\"kid\":\"nottest\"}]}";
//        final String idToken =
//            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InRlc3QifQ.eyJhenAiOiJNekZsWmpreFpHSXRPV1UyTlMwMFpURm1MVGt3TXpjdE5UUXpOamRrTURCa016Y3pPbTl3WlhKaGRHOXlMV0U9IiwiYXV0aF90aW1lIjoxNDcwMzI2ODIwLCJhdWQiOlsiTXpGbFpqa3haR0l0T1dVMk5TMDBaVEZtTFRrd016Y3ROVFF6Tmpka01EQmtNemN6T205d1pYSmhkRzl5TFdFPSJdLCJhbXIiOlsiU0lNX1BJTiJdLCJub25jZSI6ImFkNjVmOGUwNzA3MTRlYTU5Yzc2NDRlZjE1OGM1MjM3IiwiaWF0IjoxNDcwMzI2ODIwLCJpc3MiOiJpbnRlZ3JhdGlvbjIuc2FuZGJveC5tb2JpbGVjb25uZWN0LmlvIiwiYWNyIjoiMiIsImV4cCI6MTQ3MDMzMDQyMCwic3ViIjoiYzIzMjQ2N2MtNDliMi0xMWU2LTlhYTgtMDI0MmFjMTEwMDAzIn0.PKN_cBANpXLegnmu6My4yhqcdbZaRVRLlseQJ4y1gMyFzLfRfYFHhbQC4xrIaN6ryxIsgJvFZ-047WfMwyptIhcP87exuYt6253k9gddndmjJtLuT9d5DB9bjiKkK49IdVsu91xyT1bXBHiWnZ-alFgnC4NfsCN3ec9TAynlivhzlBwghfdc6T8V27ewHWKg1ds0ZZbLQYZ0PtuLd0PW_SEOAnajVICBN7xm0rgxf9CTgOs5mBnKVCgPu1sJ-6bdcfA2VpLGLleuDHb9J9t6kbMytEMUjs4eDjdgxlogIUBOvY4MWfuu4l85GPZPMJ29aGmvAbns9e5Pufm8nO9DEA";
//        final JacksonJsonService jacksonJsonService = new JacksonJsonService();
//
//        final JWKeyset jwKeyset = jacksonJsonService.deserialize(jwksJson, JWKeyset.class);
//        final TokenValidationResult tokenValidationResult =
//            TokenValidation.validateIdTokenSignature(idToken, jwKeyset, jacksonJsonService);
//
//        assertEquals(tokenValidationResult, TokenValidationResult.Valid);
//    }

    // TODO: 23/09/16 test no/null key
    @Test
    public void validateIdTokenSignatureShouldNotValidateWhenKeysetNull()
        throws JsonDeserializationException, InvalidKeyException
    {
        final String jwksJson = null;
        final String idToken =
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IlBIUE9QLTAwIn0.eyJpc3MiOiJodHRwczpcL1wvcmVmZXJlbmNlLm1vYmlsZWNvbm5lY3QuaW9cL21vYmlsZWNvbm5lY3QiLCJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJhdWQiOlsieC1aV1JoTmpVM09XSTNNR0l3WVRSaCJdLCJleHAiOjE0NzQ2MjYzMzAsImlhdCI6MTQ3NDYyNjAzMCwibm9uY2UiOiI4MTk5MTQ5Ni00OGJiLTRkMTMtYmQwYy0xMTdkOTk0NDExYTYiLCJhdF9oYXNoIjoiNTZGMXo3RjZ3eWhUYUhVY1ZGY0xJQSIsImF1dGhfdGltZSI6MTQ3NDYyNjAyMCwiYWNyIjoiMiIsImFtciI6WyJTSU1fUElOIl0sImF6cCI6IngtWldSaE5qVTNPV0kzTUdJd1lUUmgifQ.TYcvfIHeKigkvjYta6fy90EffiA6u6NFCSIPlPM2WxEUi8Kxc5JIrjXnM8l0rFJOLmgNFUBpSqIRhuxwZkUV52KWf8jzswi3jTI8wEjonbjgviz7c6WzlZdb0Pw5kUEWy2xMam7VprESphPaIkHCDor2yR2g6Uq3Wtqyg7MCqek";
        JacksonJsonService jacksonJsonService = new JacksonJsonService();

        final JWKeyset jwKeyset = jacksonJsonService.deserialize(jwksJson, JWKeyset.class);
        final TokenValidationResult tokenValidationResult =
            TokenValidation.validateIdTokenSignature(idToken, jwKeyset, jacksonJsonService);

        assertEquals(tokenValidationResult, TokenValidationResult.JWKSError);
    }

    @Test
    public void validateIdTokenSignatureShouldNotValidateWhenNoMatchingKey()
        throws JsonDeserializationException, InvalidKeyException
    {
        final String jwksJson =
            "{\"keys\":[{\"kty\":\"AAA\",\"use\":\"sig\",\"n\":\"ALyIC8vj1tqEIvAvpDMQfgosw13LpBS9Z2lsMmuaLDNJjN_FKIb-HVR2qtMj7AYC0-wYJhGxJpTXJTVRRDz-zLN7uredNxuhVj76vmU1tfvEN0Xq2INYoWeJ3d9fZtkBgKl7Enfkgz858DLAfZuJzDycOzuZXR5r29zXMDstT5F5\",\"e\":\"AQAB\",\"kid\":\"PHPOP-99\"}]}";
        final String idToken =
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IlBIUE9QLTAwIn0.eyJpc3MiOiJodHRwczpcL1wvcmVmZXJlbmNlLm1vYmlsZWNvbm5lY3QuaW9cL21vYmlsZWNvbm5lY3QiLCJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJhdWQiOlsieC1aV1JoTmpVM09XSTNNR0l3WVRSaCJdLCJleHAiOjE0NzQ2MjYzMzAsImlhdCI6MTQ3NDYyNjAzMCwibm9uY2UiOiI4MTk5MTQ5Ni00OGJiLTRkMTMtYmQwYy0xMTdkOTk0NDExYTYiLCJhdF9oYXNoIjoiNTZGMXo3RjZ3eWhUYUhVY1ZGY0xJQSIsImF1dGhfdGltZSI6MTQ3NDYyNjAyMCwiYWNyIjoiMiIsImFtciI6WyJTSU1fUElOIl0sImF6cCI6IngtWldSaE5qVTNPV0kzTUdJd1lUUmgifQ.TYcvfIHeKigkvjYta6fy90EffiA6u6NFCSIPlPM2WxEUi8Kxc5JIrjXnM8l0rFJOLmgNFUBpSqIRhuxwZkUV52KWf8jzswi3jTI8wEjonbjgviz7c6WzlZdb0Pw5kUEWy2xMam7VprESphPaIkHCDor2yR2g6Uq3Wtqyg7MCqek";
        final JacksonJsonService jacksonJsonService = new JacksonJsonService();

        final JWKeyset jwKeyset = jacksonJsonService.deserialize(jwksJson, JWKeyset.class);
        final TokenValidationResult tokenValidationResult =
            TokenValidation.validateIdTokenSignature(idToken, jwKeyset, jacksonJsonService);

        assertEquals(tokenValidationResult, TokenValidationResult.NoMatchingKey);
    }

    @Test
    public void validateIdTokenSignatureShouldNotValidateWhenSignatureMissing()
        throws JsonDeserializationException, InvalidKeyException
    {
        final String jwksJson =
            "{\"keys\":[{\"kty\":\"RSA\",\"use\":\"sig\",\"n\":\"ALyIC8vj1tqEIvAvpDMQfgosw13LpBS9Z2lsMmuaLDNJjN_FKIb-HVR2qtMj7AYC0-wYJhGxJpTXJTVRRDz-zLN7uredNxuhVj76vmU1tfvEN0Xq2INYoWeJ3d9fZtkBgKl7Enfkgz858DLAfZuJzDycOzuZXR5r29zXMDstT5F5\",\"e\":\"AQAB\",\"kid\":\"PHPOP-00\"}]}";
        final String idToken =
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IlBIUE9QLTAwIn0.eyJpc3MiOiJodHRwczpcL1wvcmVmZXJlbmNlLm1vYmlsZWNvbm5lY3QuaW9cL21vYmlsZWNvbm5lY3QiLCJzdWIiOiI0MTE0MjFCMC0zOEQ2LTY1NjgtQTUzQS1ERjk5NjkxQjdFQjYiLCJhdWQiOlsieC1aV1JoTmpVM09XSTNNR0l3WVRSaCJdLCJleHAiOjE0NzQ2MjYzMzAsImlhdCI6MTQ3NDYyNjAzMCwibm9uY2UiOiI4MTk5MTQ5Ni00OGJiLTRkMTMtYmQwYy0xMTdkOTk0NDExYTYiLCJhdF9oYXNoIjoiNTZGMXo3RjZ3eWhUYUhVY1ZGY0xJQSIsImF1dGhfdGltZSI6MTQ3NDYyNjAyMCwiYWNyIjoiMiIsImFtciI6WyJTSU1fUElOIl0sImF6cCI6IngtWldSaE5qVTNPV0kzTUdJd1lUUmgifQ.";
        final JacksonJsonService jacksonJsonService = new JacksonJsonService();

        final JWKeyset jwKeyset = jacksonJsonService.deserialize(jwksJson, JWKeyset.class);
        final TokenValidationResult tokenValidationResult =
            TokenValidation.validateIdTokenSignature(idToken, jwKeyset, jacksonJsonService);

        assertEquals(tokenValidationResult, TokenValidationResult.InvalidSignature);
    }
}
