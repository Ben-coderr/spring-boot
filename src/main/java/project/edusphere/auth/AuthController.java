package project.edusphere.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import project.edusphere.security.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API for authentication and token management")
public class AuthController {

    private final AuthenticationManager authMgr;
    private final JwtUtil jwt;

    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
                content = @Content(schema = @Schema(implementation = TokenDTO.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public TokenDTO login(
            @Parameter(description = "Login credentials", required = true,
                     schema = @Schema(implementation = LoginDTO.class))
            @RequestBody LoginDTO in) {
        Authentication auth = authMgr.authenticate(
                new UsernamePasswordAuthenticationToken(in.email(), in.password()));
        String role = auth.getAuthorities().iterator().next().getAuthority();
        return new TokenDTO(jwt.generate(in.email(), role.replace("ROLE_", "")));
    }

    /* ── tiny records for payload ───────── */
    @Schema(description = "Login request with email and password")
    public record LoginDTO(
            @Schema(description = "User email", example = "admin@school.com") 
            String email, 
            
            @Schema(description = "User password", example = "password123") 
            String password) {}
    
    @Schema(description = "JWT token response")
    public record TokenDTO(
            @Schema(description = "JWT authentication token", 
                   example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") 
            String token) {}
}