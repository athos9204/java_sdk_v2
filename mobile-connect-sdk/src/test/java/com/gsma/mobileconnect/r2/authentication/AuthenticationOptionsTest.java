package com.gsma.mobileconnect.r2.authentication;

import com.gsma.mobileconnect.r2.claims.ClaimsParameter;
import com.gsma.mobileconnect.r2.constants.DefaultOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.*;

public class AuthenticationOptionsTest
{
    private AuthenticationOptions authenticationOptions;

    private String clientId = "clientId";
    private URI redirectUrl = null;
    private String nonce = "nonce";
    private String state = "state";
    private String prompt = "mobile";
    private String uiLocales = "uiLocales";
    private String claimsLocales = "claimsLocales";
    private String idTokenHint = "idTokenHint";
    private String loginHint = "loginHint";
    private String dbts = "dbts";
    private String clientName = "clientName";
    private String context = "context";
    private String bindingMessage = "bindingMessage";
    private String claimsJson = "claimsJson";
    private ClaimsParameter claims;

    @BeforeMethod
    public void setUp() throws Exception
    {
        authenticationOptions = new AuthenticationOptions.Builder()
            .withClientId(clientId)
            .withClientName(clientName)
            .withRedirectUrl(redirectUrl)
            .withNonce(nonce)
            .withState(state)
            .withPrompt(prompt)
            .withUiLocales(uiLocales)
            .withClaimsLocales(claimsLocales)
            .withIdTokenHint(idTokenHint)
            .withLoginHint(loginHint)
            .withDbts(dbts)
            .withContext(context)
            .withBindingMessage(bindingMessage)
            .withClaimsJson(claimsJson)
            .withClaims(claims)
            .build();
    }

    @AfterMethod
    public void tearDown() throws Exception
    {
        authenticationOptions = null;
    }

    @Test
    public void testGetClientId() throws Exception
    {
        assertEquals(authenticationOptions.getClientId(), clientId);
    }

    @Test
    public void testGetRedirectUrl() throws Exception
    {
        assertEquals(authenticationOptions.getRedirectUrl(), redirectUrl);
    }

    @Test
    public void testGetAcrValues() throws Exception
    {
        assertEquals(authenticationOptions.getAcrValues(),
            DefaultOptions.AUTHENTICATION_ACR_VALUES);
    }

    @Test
    public void testGetScope() throws Exception
    {
        assertEquals(authenticationOptions.getScope(), DefaultOptions.AUTHENTICATION_SCOPE);
    }

    @Test
    public void testGetNonce() throws Exception
    {
        assertEquals(authenticationOptions.getNonce(),nonce);
    }

    @Test
    public void testGetState() throws Exception
    {
        assertEquals(authenticationOptions.getState(), state);
    }

    @Test
    public void testGetMaxAge() throws Exception
    {
        assertEquals(authenticationOptions.getMaxAge(), DefaultOptions.AUTHENTICATION_MAX_AGE);
    }

    @Test
    public void testGetDisplay() throws Exception
    {
        assertEquals(authenticationOptions.getDisplay(), DefaultOptions.DISPLAY);
    }

    @Test
    public void testGetPrompt() throws Exception
    {
        assertEquals(authenticationOptions.getPrompt(), prompt);
    }

    @Test
    public void testGetUiLocales() throws Exception
    {
        assertEquals(authenticationOptions.getUiLocales(), uiLocales);
    }

    @Test
    public void testGetClaimsLocales() throws Exception
    {
        assertEquals(authenticationOptions.getClaimsLocales(), claimsLocales);
    }

    @Test
    public void testGetIdTokenHint() throws Exception
    {
        assertEquals(authenticationOptions.getIdTokenHint(), idTokenHint);
    }

    @Test
    public void testGetLoginHint() throws Exception
    {
        assertEquals(authenticationOptions.getLoginHint(), loginHint);
    }

    @Test
    public void testGetDbts() throws Exception
    {
        assertEquals(authenticationOptions.getDbts(), dbts);
    }

    @Test
    public void testGetClientName() throws Exception
    {
        assertEquals(authenticationOptions.getClientName(), clientName);
    }

    @Test
    public void testGetContext() throws Exception
    {
        assertEquals(authenticationOptions.getContext(), context);
    }

    @Test
    public void testGetBindingMessage() throws Exception
    {
        assertEquals(authenticationOptions.getBindingMessage(), bindingMessage);
    }

    @Test
    public void testGetClaimsJson() throws Exception
    {
        assertEquals(authenticationOptions.getClaimsJson(), claimsJson);
    }

    @Test
    public void testGetClaims() throws Exception
    {
        assertEquals(authenticationOptions.getClaims(), claims);
    }

}