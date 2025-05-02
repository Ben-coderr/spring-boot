package com.school.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {
    private static final byte[] KEY =       
    "hfKpyyT6u83e1zwx9s3UXN5UWKfT1n40nRBxD8itzL8UOEjQdwbZKWqGgN6RE8cc".getBytes();
    private final JwtParser parser = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(KEY)).build();

    public String generate(String email, String role) {
        return Jwts.builder()
                   .subject(email)
                   .claim("role", role)
                   .issuedAt(new Date())
                   .expiration(Date.from(Instant.now().plusSeconds(8*3600)))
                   .signWith(Keys.hmacShaKeyFor(KEY))
                   .compact();
    }

    public UsernamePasswordAuthenticationToken parse(String token) {
        Claims c = parser.parseSignedClaims(token).getPayload();
        String email = c.getSubject();
        String role  = c.get("role", String.class);
        return new UsernamePasswordAuthenticationToken(
                email, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
    }
}
