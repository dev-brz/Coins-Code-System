package com.cgzt.coinscode.users.domain.ports.inbound.commands;

import com.cgzt.coinscode.users.domain.ports.outbound.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginCommandHandler {
    private final AuthenticationService authenticationService;

    public void handle(final Command command) {
        authenticationService.validateCredentials(command.username, command.password);
    }

    public record Command(String username, char[] password) {
    }
}
