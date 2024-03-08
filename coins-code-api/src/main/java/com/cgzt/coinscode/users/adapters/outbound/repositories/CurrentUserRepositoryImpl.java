package com.cgzt.coinscode.users.adapters.outbound.repositories;

import com.cgzt.coinscode.users.domain.models.User;
import com.cgzt.coinscode.users.domain.ports.outbound.repository.CurrentUserRepository;
import com.cgzt.coinscode.users.domain.ports.outbound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class CurrentUserRepositoryImpl implements CurrentUserRepository {
    private final UserDetailsManager userDetailsManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        return userRepository.findByUsername(username).orElseThrow();
    }

    @Override
    public void changePassword(char[] oldPassword, char[] newPassword) {
        userDetailsManager.changePassword(
                passwordEncoder.encode(String.valueOf(oldPassword)),
                passwordEncoder.encode(String.valueOf(newPassword))
        );
    }
}
