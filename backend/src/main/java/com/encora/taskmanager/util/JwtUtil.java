package com.encora.taskmanager.util;


import com.encora.taskmanager.model.Token;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("PUT_A_SECRET_KEY_HERE".getBytes());

    static final long JWT_EXPIRATION_TIME = 3_600_000;

    public String generateToken(Token tokenData) {
        return Jwts.builder()
                .subject(tokenData.username())
                .issuedAt(tokenData.issuedAt())
                .expiration(tokenData.expiration())
                .signWith(SECRET_KEY)
                .compact();

    }

    public Token extractClaimsFromToken(String token) {
        var payload = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new Token(payload.getSubject(), payload.getIssuedAt(), payload.getExpiration());
    }
}
