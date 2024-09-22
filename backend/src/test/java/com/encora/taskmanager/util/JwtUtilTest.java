package com.encora.taskmanager.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void testGenerateTokenAndExtractClaims() {
        String username = "user@example.com";

        String generatedToken = jwtUtil.generateToken(username);
        String extractedUsername = jwtUtil.extractUsernameFromToken(generatedToken);

        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateToken() {
        String username = "user@example.com";

        String generatedToken = jwtUtil.generateToken(username);
        boolean isValid = jwtUtil.validateToken(username, generatedToken);

        assertTrue(isValid);
    }

    @Test
    void testValidateTokenWhenUsernameDoesNotMatch() {
        String username = "user@example.com";

        String generatedToken = jwtUtil.generateToken(username);
        boolean isValid = jwtUtil.validateToken("wrongUsername", generatedToken);

        assertFalse(isValid);
    }
}
