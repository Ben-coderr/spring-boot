package com.school.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain chain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeHttpRequests().anyRequest().authenticated()
            .and().httpBasic();
        return http.build();
    }
    @Bean
    public UserDetailsService uds() {
        UserDetails u = User.withUsername("admin")
                            .password("{noop}password")
                            .roles("ADMIN")
                            .build();
        return new InMemoryUserDetailsManager(u);
    }
}
