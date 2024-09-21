package com.encora.taskmanager.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {
    @InjectMocks
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateTokenAndExtractClaims() {
        String username = "user@example.com";

        String generatedToken = jwtUtil.generateToken(username);
        String extractedUsername = jwtUtil.extractUsernameFromToken(generatedToken);

        assertEquals(username, extractedUsername);
    }
}
