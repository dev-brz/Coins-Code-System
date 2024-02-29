package com.cgzt.coinscode.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import javax.sql.DataSource;

@Configuration
class UserDetailsManagerConfig {
    @Bean
    UserDetailsManager userDetailsManager(final PasswordEncoder encoder, final DataSource dataSource) {
        // TODO - Remove users initialization after implementing registration [#12]
        UserDetails user = User.builder()
                .username("user")
                .password(encoder.encode("resu"))
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("nimda"))
                .roles("USER", "ADMIN")
                .build();

        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        users.createUser(user);
        users.createUser(admin);

        return users;
//        return new JdbcUserDetailsManager(dataSource);
    }
}
