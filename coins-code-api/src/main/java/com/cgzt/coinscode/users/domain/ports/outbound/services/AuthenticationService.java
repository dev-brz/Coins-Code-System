package com.cgzt.coinscode.users.domain.ports.outbound.services;

public interface AuthenticationService {
    void validateCredentials(String username, char[] password);
}
