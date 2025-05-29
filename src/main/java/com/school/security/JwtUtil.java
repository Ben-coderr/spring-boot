package com.school.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import com.school.model.User;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {


 private final SecretKey key = Keys.hmacShaKeyFor( 
             "ksaujdh87dh3287hcp23h237pdh387dh".getBytes());

    public String generate(String username, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                   .subject(username)
                   .claim("role", role)
                   .issuedAt(Date.from(now))
                   .expiration(Date.from(now.plusSeconds(86_400)))   // 1 day
                   .signWith(key)
                   .compact();
    }
    public String generateToken(User user) {
        return generateToken(
                user.getUsername(),
                Map.of("role", user.getRole().name(),
                       "uid",  user.getId())
        );
    }
    public io.jsonwebtoken.Claims parse(String jwt) {
        return Jwts.parser()                 // same API, type now matches
                   .verifyWith(key)
                   .build()
                   .parseSignedClaims(jwt)
                   .getPayload();
    }
}
