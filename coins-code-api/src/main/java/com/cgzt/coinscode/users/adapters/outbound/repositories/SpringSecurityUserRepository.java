package com.cgzt.coinscode.users.adapters.outbound.repositories;

import com.cgzt.coinscode.users.domain.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class SpringSecurityUserRepository {
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    @Value("${user.client.roles}")
    private String[] roles;

    public void createUserAccount(User user, char[] password) {
        var userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(passwordEncoder.encode(String.valueOf(password)))
                .roles(roles)
                .build();

        userDetailsManager.createUser(userDetails);
    }

    public void deleteUserAccount(String username) {
        userDetailsManager.deleteUser(username);
    }
}
