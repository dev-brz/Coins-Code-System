package com.cgzt.coinscode.domain.ports.inbound;

import com.cgzt.coinscode.domain.ports.outbound.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginCommandHandler {
    private final AuthenticationService authenticationService;

    public void handle(Command command) {
        authenticationService.validateCredentials(command.username, command.password);
    }

    public record Command(String username, char[] password) {
    }
}
