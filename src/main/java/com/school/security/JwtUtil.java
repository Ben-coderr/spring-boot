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
 public class JwtUtil { // helper for JWT


 private final SecretKey key = Keys.hmacShaKeyFor(
             "ksaujdh87dh3287hcp23h237pdh387dh".getBytes()); // secret key

    public String generate(String username, String role) { // make token
        Instant now = Instant.now();
        return Jwts.builder()
                   .subject(username)
                   .claim("role", role)
                   .issuedAt(Date.from(now))
                   .expiration(Date.from(now.plusSeconds(86_400)))   // 1 day
                   .signWith(key)
                   .compact();
    }
    public String generateToken(User user) { // from user object
        return generate(user.getUsername(), user.getRole().name());
    }
    public io.jsonwebtoken.Claims parse(String jwt) {
        return Jwts.parser()                 // check token
                   .verifyWith(key)
                   .build()
                   .parseSignedClaims(jwt)
                   .getPayload();
    }
}
