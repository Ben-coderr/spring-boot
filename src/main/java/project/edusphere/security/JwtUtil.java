package project.edusphere.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

 
    private final byte[] key;
   private final JwtParser parser;

    public JwtUtil(@Value("${security.jwt.secret}") String secret) {
        this.key    = secret.getBytes();
        this.parser = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(key)).build();
   }

    public String generate(String email, String role) {
         return Jwts.builder()
                   .subject(email)
                   .claim("role", role)
                   .issuedAt(new Date())
                   .expiration(Date.from(Instant.now().plusSeconds(8*3600)))
                   .signWith(Keys.hmacShaKeyFor(key))
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

