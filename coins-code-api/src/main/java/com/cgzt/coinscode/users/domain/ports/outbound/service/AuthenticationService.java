package com.cgzt.coinscode.users.domain.ports.outbound.service;

public interface AuthenticationService {
    void validateCredentials(String username, char[] password);
}
