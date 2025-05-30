package com.school.security;

import com.school.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil        jwt;   // helper for token
    private final UserRepository users; // read users

    public JwtAuthFilter(JwtUtil jwt, UserRepository users) {
        this.jwt   = jwt;
        this.users = users;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest  req,
                                    HttpServletResponse res,
                                    FilterChain         chain)
            throws ServletException, IOException { // check token on each call

        String hdr = req.getHeader(HttpHeaders.AUTHORIZATION); // get header
        if (hdr != null && hdr.startsWith("Bearer ")) {
            String token = hdr.substring(7);
            try {
                var claims = jwt.parse(token); // decode token
                String username = claims.getSubject();

                var principal = users.findByUsername(username).orElse(null);
                if (principal != null) {
                    var auth = new UsernamePasswordAuthenticationToken(
                                    principal, null, principal.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception ignored) { // invalid token means anonymous droppout
            }
        }
        chain.doFilter(req, res); // continue filter chain
    }
}
