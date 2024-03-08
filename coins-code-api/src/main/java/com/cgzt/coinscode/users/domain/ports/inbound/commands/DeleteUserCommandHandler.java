package com.cgzt.coinscode.users.domain.ports.inbound.commands;

import com.cgzt.coinscode.users.domain.ports.outbound.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteUserCommandHandler {
    private final UserRepository userRepository;

    public void handle(Command command) {
        userRepository.deleteByUsername(command.username);
    }

    public record Command(@NotBlank String username) {
    }
}
