package com.gsma.mobileconnect.r2.authentication;

import com.gsma.mobileconnect.r2.claims.ClaimsParameter;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.testng.Assert.assertEquals;

/**
 * @since 2.0
 */
public class AuthenticationOptionsTest
{
    @Test
    public void builderObjectShouldBuildAuthenticationOptions() throws URISyntaxException
    {
        final String acrValues = "2";
        final String bindingMessage = "bindingMessage";
        final ClaimsParameter claimsParameter = new ClaimsParameter.Builder().build();
        final String claimsJson = "{\"k\":\"v\"}";
        final String claimsLocales = "claimsLocale";
        final String clientId = "clientId";
        final String clientName = "clientName";
        final String context = "context";
        final String dbts = "dbts";
        final String display = "display";
        final String idTokenHint = "idTokenHint";
        final String loginHint = "loginHint";
        final int maxAge = 0;
        final String nonce = "nonce";
        final String prompt = "prompt";
        final URI redirectUrl = new URI("uri");
        final String scope = "scope";
        final String state = "state";
        final String uiLocales = "uiLocales";

        final AuthenticationOptions authenticationOptions = new AuthenticationOptions.Builder()
            .withAcrValues(acrValues)
            .withBindingMessage(bindingMessage)
            .withClaims(claimsParameter)
            .withClaimsJson(claimsJson)
            .withClaimsLocales(claimsLocales)
            .withClientId(clientId)
            .withClientName(clientName)
            .withContext(context)
            .withDbts(dbts)
            .withDisplay(display)
            .withIdTokenHint(idTokenHint)
            .withLoginHint(loginHint)
            .withMaxAge(maxAge)
            .withNonce(nonce)
            .withPrompt(prompt)
            .withRedirectUrl(redirectUrl)
            .withScope(scope)
            .withState(state)
            .withUiLocales(uiLocales)
            .build();

        assertEquals(authenticationOptions.getAcrValues(), acrValues);
        assertEquals(authenticationOptions.getBindingMessage(), bindingMessage);
        assertEquals(authenticationOptions.getClaims(), claimsParameter);
        assertEquals(authenticationOptions.getClaimsJson(), claimsJson);
        assertEquals(authenticationOptions.getClaimsLocales(), claimsLocales);
        assertEquals(authenticationOptions.getClientId(), clientId);
        assertEquals(authenticationOptions.getClientName(), clientName);
        assertEquals(authenticationOptions.getContext(), context);
        assertEquals(authenticationOptions.getDbts(), dbts);
        assertEquals(authenticationOptions.getDisplay(), display);
        assertEquals(authenticationOptions.getIdTokenHint(), idTokenHint);
        assertEquals(authenticationOptions.getLoginHint(), loginHint);
        assertEquals(authenticationOptions.getMaxAge(), maxAge);
        assertEquals(authenticationOptions.getPrompt(), prompt);
        assertEquals(authenticationOptions.getRedirectUrl(), redirectUrl);
        assertEquals(authenticationOptions.getScope(), scope);
        assertEquals(authenticationOptions.getState(), state);
        assertEquals(authenticationOptions.getUiLocales(), uiLocales);
    }
}