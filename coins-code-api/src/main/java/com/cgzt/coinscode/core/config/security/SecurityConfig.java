package com.cgzt.coinscode.core.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig {
    private final CorsConfigurationSource corsConfigurationSource;
    private final UserDetailsManager userDetailsManager;

    @Bean
    SecurityFilterChain filterChain(@Value("${security.permittedMatchers}") final String[] permittedMatchers,
                                    @Value("${swagger.permittedMatchers}") final String[] swaggerEndpoints,
                                    final AuthenticationEntryPoint entryPoint,
                                    final HttpSecurity http) throws Exception {
        return http
                .httpBasic(it -> it.authenticationEntryPoint(entryPoint))
                .userDetailsService(userDetailsManager)
                .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(c -> c.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(it -> it
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.HEAD, "/users/**").permitAll()
                        .requestMatchers(permittedMatchers).permitAll()
                        .requestMatchers(swaggerEndpoints).permitAll()
                        .anyRequest().authenticated())
                .build();
    }
}
