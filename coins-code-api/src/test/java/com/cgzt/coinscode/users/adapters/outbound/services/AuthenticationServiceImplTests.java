package com.cgzt.coinscode.users.adapters.outbound.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationServiceImplTests {
    UserDetailsService userDetailsService = Mockito.mock(UserDetailsService.class);
    PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    AuthenticationServiceImpl authenticationService;

    @BeforeAll
    void setUp() {
        UserDetails anyUserDetails = User.withUsername("any").password("any").build();
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(anyUserDetails);
        authenticationService = new AuthenticationServiceImpl(userDetailsService, passwordEncoder);
    }

    @Test
    void validateCredentials_shouldThrowException_whenPasswordIsInvalid() {
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        Executable validateCredentialsExecutable =
                () -> authenticationService.validateCredentials("any", new char[]{});

        assertThrows(ResponseStatusException.class, validateCredentialsExecutable);
    }

    @Test
    void validateCredentials_shouldNotThrowException_whenPasswordIsValid() {
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        Executable validateCredentialsExecutable =
                () -> authenticationService.validateCredentials("any", new char[]{});

        assertDoesNotThrow(validateCredentialsExecutable);
    }
}
