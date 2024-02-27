package com.cgzt.coinscode.domain.ports.outbound;

public interface AuthenticationService {
    void validateCredentials(String username, char[] password);
}
