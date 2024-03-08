package com.cgzt.coinscode.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.sql.DataSource;

@Configuration
class UserDetailsManagerConfig {
    @Bean
    UserDetailsManager userDetailsManager(final DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }
}
