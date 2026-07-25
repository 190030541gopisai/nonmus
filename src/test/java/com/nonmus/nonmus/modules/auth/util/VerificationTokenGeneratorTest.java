package com.nonmus.nonmus.modules.auth.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VerificationTokenGeneratorTest {

    private final VerificationTokenGenerator generator = new VerificationTokenGenerator();

    @Test
    void generate_shouldReturnUrlSafeBase64String() {
        String token = generator.generate();

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertFalse(token.contains("+"));
        assertFalse(token.contains("/"));
        assertFalse(token.contains("="));
    }

    @Test
    void generate_shouldReturnDifferentTokensOnEachCall() {
        String token1 = generator.generate();
        String token2 = generator.generate();

        assertNotEquals(token1, token2);
    }
}