package com.encora.taskmanager.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("""
            Put your secret key here, this is just a placeholder.
            """.getBytes());

    private static final long JWT_EXPIRATION_TIME = 3_600_000;

    public String generateToken(String username) {
        Date issueDate = new Date();
        Date expiration = new Date(issueDate.getTime() + JWT_EXPIRATION_TIME);

        return Jwts.builder()
                .subject(username)
                .issuedAt(issueDate)
                .expiration(expiration)
                .signWith(SECRET_KEY)
                .compact();

    }

    public String extractUsernameFromToken(String token) {
        var payload = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return payload.getSubject();
    }
}
