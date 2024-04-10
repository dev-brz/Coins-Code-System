package com.cgzt.coinscode.users.adapters.outbound.repositories;

import com.cgzt.coinscode.users.domain.models.User;
import com.cgzt.coinscode.users.domain.ports.outbound.repositories.CurrentUserRepository;
import com.cgzt.coinscode.users.domain.ports.outbound.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
class CurrentUserRepositoryImpl implements CurrentUserRepository {
    private final UserDetailsManager userDetailsManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User get() {
        return userRepository.findByUsername(getUsername()).orElseThrow();
    }

    @Override
    public Optional<Long> getId() {
        return userRepository.findIdByUsername(getUsername());
    }

    @Override
    public void changePassword(char[] oldPassword, char[] newPassword) {
        userDetailsManager.changePassword(
                passwordEncoder.encode(String.valueOf(oldPassword)),
                passwordEncoder.encode(String.valueOf(newPassword))
        );
    }

    private String getUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return ((UserDetails) auth.getPrincipal()).getUsername();
    }
}
