package com.gsma.mobileconnect.r2.authentication;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @since 2.0
 */
public class TokenValidationOptionsTest
{
    @Test
    public void testGetAcceptedValidationResults() throws Exception
    {
        TokenValidationOptions tokenValidationOptions = new TokenValidationOptions.Builder()
            .withAcceptedValidationResults(TokenValidationResult.Valid)
            .build();

        assertEquals(tokenValidationOptions.getAcceptedValidationResults(),
            TokenValidationResult.Valid);
    }

}