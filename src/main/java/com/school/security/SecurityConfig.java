package com.school.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtFilter jwtFilter;
    private final UserDetailsService uds;
    private final PasswordEncoder encoder;
    
    // All paths that should be publicly accessible
    private static final String[] WHITE_LIST_URLS = {
        // Swagger UI v3 (OpenAPI)
        "/v3/api-docs",
        "/v3/api-docs/**",
        "/v3/api-docs.yaml",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/swagger-resources/**",
        "/webjars/**",
        
        // Auth endpoints
        "/api/auth/**",
        
        // Actuator endpoints if needed
        "/actuator/**"
    };

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(uds)
                .passwordEncoder(encoder)
                .and().build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Restrict origins in production
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",  // Next.js dev server
            "https://your-production-domain.com"  // Production frontend
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
            "authorization", 
            "content-type", 
            "x-requested-with",
            "cache-control"
        ));
        configuration.setExposedHeaders(Arrays.asList(
            "authorization", 
            "content-type",
            "x-auth-token"
        ));
        configuration.setAllowCredentials(true);  // Allow cookies/credentials
        configuration.setMaxAge(3600L);  // 1 hour cache

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .headers(headers -> headers
                .frameOptions(frameOpt -> frameOpt.disable())
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'")
                )
            )
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers(WHITE_LIST_URLS).permitAll();
                
                // Example role-based access (adjust to your needs)
                auth.requestMatchers("/api/admin/**").hasRole("ADMIN");
                auth.requestMatchers("/api/teacher/**").hasRole("TEACHER");
                auth.requestMatchers("/api/parent/**").hasRole("PARENT");
                auth.requestMatchers("/api/student/**").hasRole("STUDENT");
                auth.anyRequest().authenticated();
            })
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}