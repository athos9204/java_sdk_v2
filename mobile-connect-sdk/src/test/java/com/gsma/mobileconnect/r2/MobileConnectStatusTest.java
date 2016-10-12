package com.gsma.mobileconnect.r2;

import com.gsma.mobileconnect.r2.authentication.RequestTokenResponse;
import com.gsma.mobileconnect.r2.discovery.DiscoveryResponse;
import com.gsma.mobileconnect.r2.identity.IdentityResponse;
import com.gsma.mobileconnect.r2.json.DiscoveryResponseData;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

public class MobileConnectStatusTest
{
    private MobileConnectStatus mobileConnectStatus;
    private DiscoveryResponse discoveryResponse;
    private IdentityResponse identityResponse;
    private RequestTokenResponse requestTokenResponse;
    private Exception exception;
    private List<String> cookies;

    @BeforeMethod
    public void setUp() throws Exception
    {
        discoveryResponse = new DiscoveryResponse.Builder()
            .withResponseData(new DiscoveryResponseData.Builder().build())
            .build();
        identityResponse = new IdentityResponse.Builder().build();
        requestTokenResponse = new RequestTokenResponse.Builder().build();
        exception = new RuntimeException("Test");
        cookies = new ArrayList<String>();
        cookies.add("a=1");
        cookies.add("b=2");

        mobileConnectStatus = new MobileConnectStatus.Builder()
            .withDiscoveryResponse(discoveryResponse)
            .withErrorCode("errorCode")
            .withErrorMessage("errorMsg")
            .withException(exception)
            .withIdentityResponse(identityResponse)
            .withNonce("nonce")
            .withState("state")
            .withSdkSession("sdkSession")
            .withOutcome("outcome")
            .withUrl("url")
            .withRequestTokenResponse(requestTokenResponse)
            .withResponseType(MobileConnectStatus.ResponseType.COMPLETE)
            .withSetCookie(cookies)
            .build();
    }

    @AfterMethod
    public void tearDown() throws Exception
    {
        discoveryResponse = null;
        identityResponse = null;
        requestTokenResponse = null;
        exception = null;
        cookies = null;
        mobileConnectStatus = null;
    }

    @Test
    public void testError() throws Exception
    {

    }

    @Test
    public void testError1() throws Exception
    {

    }

    @Test
    public void testError2() throws Exception
    {

    }

    @Test
    public void testOperatorSelection() throws Exception
    {

    }

    @Test
    public void testStartAuthentication() throws Exception
    {

    }

    @Test
    public void testStartDiscovery() throws Exception
    {

    }

    @Test
    public void testAuthentication() throws Exception
    {

    }

    @Test
    public void testComplete() throws Exception
    {

    }

    @Test
    public void testComplete1() throws Exception
    {

    }

    @Test
    public void testError3() throws Exception
    {

    }

    @Test
    public void testWithSdkSession() throws Exception
    {

    }

    @Test
    public void testGetResponseType() throws Exception
    {
        assertEquals(mobileConnectStatus.getResponseType(),
            MobileConnectStatus.ResponseType.COMPLETE);
    }

    @Test
    public void testGetErrorCode() throws Exception
    {
        assertEquals(mobileConnectStatus.getErrorCode(), "errorCode");
    }

    @Test
    public void testGetErrorMessage() throws Exception
    {
        assertEquals(mobileConnectStatus.getErrorMessage(), "errorMsg");
    }

    @Test
    public void testGetUrl() throws Exception
    {
        assertEquals(mobileConnectStatus.getUrl(), "url");
    }

    @Test
    public void testGetState() throws Exception
    {
        assertEquals(mobileConnectStatus.getState(), "state");
    }

    @Test
    public void testGetNonce() throws Exception
    {
        assertEquals(mobileConnectStatus.getNonce(), "nonce");
    }

    @Test
    public void testGetSetCookie() throws Exception
    {
        assertEquals(mobileConnectStatus.getSetCookie(), cookies);
    }

    @Test
    public void testGetSdkSession() throws Exception
    {
        assertEquals(mobileConnectStatus.getSdkSession(), "sdkSession");
    }

    @Test
    public void testGetDiscoveryResponse() throws Exception
    {
        assertEquals(mobileConnectStatus.getDiscoveryResponse(), discoveryResponse);
    }

    @Test
    public void testGetRequestTokenResponse() throws Exception
    {
        assertEquals(mobileConnectStatus.getRequestTokenResponse(), requestTokenResponse);
    }

    @Test
    public void testGetIdentityResponse() throws Exception
    {
        assertEquals(mobileConnectStatus.getIdentityResponse(), identityResponse);
    }

    @Test
    public void testGetException() throws Exception
    {
        assertEquals(mobileConnectStatus.getException(), exception);
    }

    @Test
    public void testGetOutcome() throws Exception
    {
        assertEquals(mobileConnectStatus.getOutcome(), "outcome");
    }
}