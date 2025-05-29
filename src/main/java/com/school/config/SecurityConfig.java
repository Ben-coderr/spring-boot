package com.school.config;

import com.school.repository.UserRepository;
import com.school.security.JwtAuthFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class SecurityConfig { // config for security

    @Bean
    public PasswordEncoder encoder() { // plain text for demo
        return NoOpPasswordEncoder.getInstance();
    }
    @Bean
    public UserDetailsService uds(UserRepository repo) { // fetch user
        return username -> repo.findByUsername(username)
                               .orElseThrow(() -> new RuntimeException("user?"));
    }

    @Bean
    public DaoAuthenticationProvider authProvider(UserDetailsService uds,
                                                  PasswordEncoder enc) { // provider with our user
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(enc);
        return provider;
    }
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration cfg) throws Exception { // expose manager
        return cfg.getAuthenticationManager();
    }
    @Bean
public SecurityFilterChain filter(HttpSecurity http,
                                  DaoAuthenticationProvider auth,
                                  JwtAuthFilter             jwt) throws Exception { // build chain

    http.csrf().disable()

        // allow swagger and login without token
        .authorizeHttpRequests()
            .requestMatchers("/swagger-ui/**","/v3/api-docs/**","/auth/login", "/auth/signup/**").permitAll()
            .anyRequest().authenticated()

        .and()
        .authenticationProvider(auth)

        // we no longer need HTTP Basic for the API itself
        .sessionManagement().disable()
        .httpBasic().disable();

    // our filter runs before Spring’s own auth filters
    http.addFilterBefore(jwt, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
}
