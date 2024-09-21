package com.encora.taskmanager.util;

import com.encora.taskmanager.model.Token;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.Date;

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
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MILLISECOND, 0);

        Date issuedAt = calendar.getTime();
        Date expiration = new Date(issuedAt.getTime() + JwtUtil.JWT_EXPIRATION_TIME);

        Token tokenData = new Token("user@example.com", issuedAt, expiration);

        String generatedToken = jwtUtil.generateToken(tokenData);
        Token parsedToken = jwtUtil.extractClaimsFromToken(generatedToken);

        assertEquals(tokenData.username(), parsedToken.username());
        assertEquals(tokenData.issuedAt().getTime(), parsedToken.issuedAt().getTime());
        assertEquals(tokenData.expiration().getTime(), parsedToken.expiration().getTime());
    }

    @Test
    void testParseTokenWhenHasExpired() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.MILLISECOND, 0);

        Date issuedAt = new Date(calendar.getTimeInMillis() - 2 * JwtUtil.JWT_EXPIRATION_TIME);
        Date expired = new Date(issuedAt.getTime() + JwtUtil.JWT_EXPIRATION_TIME);

        Token tokenData = new Token("user@example.com", issuedAt, expired);

        String generatedToken = jwtUtil.generateToken(tokenData);
        Exception exception = assertThrows(ExpiredJwtException.class,
                () -> jwtUtil.extractClaimsFromToken(generatedToken)
        );
        assertTrue(exception.getMessage().contains("expired"));
    }

    private Date generateDate(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
