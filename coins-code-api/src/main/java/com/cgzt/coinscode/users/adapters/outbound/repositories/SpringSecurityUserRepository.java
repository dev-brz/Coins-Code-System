package com.cgzt.coinscode.users.adapters.outbound.repositories;

import com.cgzt.coinscode.users.domain.models.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class SpringSecurityUserRepository {
    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public void createUserAccount(String username, char[] password) {
        createUserAccount(username, password, UserRole.CLIENT_ROLES);
    }

    public void createUserAccount(String username, char[] password, Set<UserRole> roles) {
        var userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password(passwordEncoder.encode(String.valueOf(password)))
                .roles(roles.stream().map(Enum::name).toArray(String[]::new))
                .build();

        userDetailsManager.createUser(userDetails);
    }

    public void deleteUserAccount(String username) {
        userDetailsManager.deleteUser(username);
    }
}
