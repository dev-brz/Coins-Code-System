package com.cgzt.coinscode.users.domain.ports.inbound.commands;

import com.cgzt.coinscode.users.domain.models.User;
import com.cgzt.coinscode.users.domain.ports.outbound.repository.CurrentUserRepository;
import com.cgzt.coinscode.users.domain.ports.outbound.service.AuthenticationService;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUserPasswordCommandHandler {
    private final AuthenticationService authenticationService;
    private final CurrentUserRepository currentUserRepository;

    public void handle(final Command command) {
        User user = currentUserRepository.get();

        authenticationService.validateCredentials(user.getUsername(), command.oldPassword);

        currentUserRepository.changePassword(command.oldPassword, command.newPassword);
    }

    public record Command(@NotEmpty char[] oldPassword,
                          @NotEmpty char[] newPassword) {
    }
}
