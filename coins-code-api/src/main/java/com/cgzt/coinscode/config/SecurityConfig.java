package com.cgzt.coinscode.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
class SecurityConfig {
    private final CorsConfigurationSource corsConfigurationSource;
    private final UserDetailsManager userDetailsManager;

    @Bean
    SecurityFilterChain filterChain(@Value("${security.permittedMatchers}") final String[] permittedMatchers,
                                    final AuthenticationEntryPoint entryPoint,
                                    final HttpSecurity http) throws Exception {
        return http
                .httpBasic(it -> it.authenticationEntryPoint(entryPoint))
                .userDetailsService(userDetailsManager)
                .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(c -> c.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(it -> it
                        .requestMatchers(permittedMatchers).permitAll()
                        .anyRequest().authenticated())
                .build();
    }
}
