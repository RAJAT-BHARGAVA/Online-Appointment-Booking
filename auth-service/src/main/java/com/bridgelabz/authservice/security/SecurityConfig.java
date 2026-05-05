package com.bridgelabz.authservice.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/register",
                                "/auth/login",
                                "/auth/validate",
                                "/auth/refresh",
                                "/auth/user/email/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Admin only
                        .requestMatchers(
                                "/auth/admin/**",
                                "/auth/users",
                                "/auth/users/**",
                                "/auth/activate/**"
                        ).hasRole("ADMIN")

                        // Provider only
                        .requestMatchers("/auth/provider/**").hasRole("PROVIDER")

                        // Patient only
                        .requestMatchers("/auth/patient/**").hasRole("PATIENT")

                        // Any logged-in user
                        .requestMatchers(
                                "/auth/profile/**",
                                "/auth/password/**",
                                "/auth/deactivate/**",
                                "/auth/user/**"
                        ).authenticated()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}