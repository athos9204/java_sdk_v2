package com.gsma.mobileconnect.r2;

import com.gsma.mobileconnect.r2.authentication.AuthenticationOptions;
import com.gsma.mobileconnect.r2.validation.TokenValidationOptions;
import com.gsma.mobileconnect.r2.discovery.DiscoveryOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class MobileConnectRequestOptionsTest
{
    private MobileConnectRequestOptions requestOptions;
    private DiscoveryOptions discoveryOptions;
    private AuthenticationOptions authenticationOptions;
    private TokenValidationOptions validationOptions;

    @BeforeMethod
    public void setUp() throws Exception
    {
        discoveryOptions = new DiscoveryOptions.Builder().build();
        authenticationOptions = new AuthenticationOptions.Builder().build();
        validationOptions = new TokenValidationOptions.Builder().build();
        requestOptions = new MobileConnectRequestOptions.Builder()
            .withDiscoveryOptions(discoveryOptions)
            .withAuthenticationOptions(authenticationOptions)
            .withValidationOptions(validationOptions)
            .witAutoRetrieveIdentitySet(true)
            .build();
    }

    @AfterMethod
    public void tearDown() throws Exception
    {
        requestOptions = null;
    }

    @Test
    public void testGetDiscoveryOptions() throws Exception
    {
        assertEquals(requestOptions.getDiscoveryOptions(), discoveryOptions,
            "Check discovery options");
    }

    @Test
    public void testIsAutoRetrieveIdentitySet() throws Exception
    {
        assertTrue(requestOptions.isAutoRetrieveIdentitySet(), "Check is autoRetrieveIdentitySet");
    }

    @Test
    public void testGetAuthenticationOptions() throws Exception
    {
        assertEquals(requestOptions.getAuthenticationOptions(), authenticationOptions,
            "Check authentication options");
    }

    @Test
    public void testGetValidationOptions() throws Exception
    {
        assertEquals(requestOptions.getValidationOptions(), validationOptions,
            "Check validation options");
    }

    @Test
    public void testGetDiscoveryOptionsBuilder() throws Exception
    {
        assertTrue(requestOptions.getDiscoveryOptionsBuilder() != null);
    }

    @Test
    public void testGetAuthenticationOptionsBuilder() throws Exception
    {
        assertTrue(requestOptions.getAuthenticationOptionsBuilder() != null);
    }

    @Test
    public void testGetValidationOptionsBuilder() throws Exception
    {
        assertTrue(requestOptions.getValidationOptionsBuilder() != null);
    }
}