package com.cgzt.coinscode.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
class SecurityConfig {
    private final CorsConfigurationSource corsConfigurationSource;
    private final UserDetailsManager userDetailsManager;

    @Bean
    SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        return http
                .httpBasic(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .userDetailsService(userDetailsManager)
                .headers(c -> c.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(c -> c.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(it -> it
                        .requestMatchers("/h2-console/**", "/login").permitAll()
                        .anyRequest().authenticated())
                .build();
    }
}
