package com.school.auth;

import com.school.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authMgr;
    private final JwtUtil jwt;

    @PostMapping("/login")
    public TokenDTO login(@RequestBody LoginDTO in) {
        Authentication auth = authMgr.authenticate(
                new UsernamePasswordAuthenticationToken(in.email(), in.password()));
        String role = auth.getAuthorities().iterator().next().getAuthority();
        return new TokenDTO(jwt.generate(in.email(), role));
    }

    /* ── tiny records for payload ───────── */
    public record LoginDTO(String email, String password) {}
    public record TokenDTO(String token) {}
}
