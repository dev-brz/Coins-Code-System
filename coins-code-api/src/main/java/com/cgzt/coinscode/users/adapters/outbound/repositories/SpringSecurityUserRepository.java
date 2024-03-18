package com.cgzt.coinscode.users.adapters.outbound.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SpringSecurityUserRepository {
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    @Value("${user.client.roles}")
    private String[] roles;

    public void createUserAccount(String username, char[] password) {
        createUserAccount(username, password, roles);
    }

    public void createUserAccount(String username, char[] password, String[] roles) {
        var userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password(passwordEncoder.encode(String.valueOf(password)))
                .roles(roles)
                .build();

        userDetailsManager.createUser(userDetails);
    }

    public void deleteUserAccount(String username) {
        userDetailsManager.deleteUser(username);
    }
}
