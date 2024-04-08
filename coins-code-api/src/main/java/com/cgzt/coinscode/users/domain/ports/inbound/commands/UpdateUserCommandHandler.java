package com.cgzt.coinscode.users.domain.ports.inbound.commands;

import com.cgzt.coinscode.users.domain.ports.inbound.commands.mappers.UserCommandMapper;
import com.cgzt.coinscode.users.domain.ports.outbound.repositories.UserRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUserCommandHandler {
    private final UserRepository userRepository;
    private final UserCommandMapper mapper;

    public void handle(final Command command) {
        userRepository.update(mapper.toUserUpdate(command));
    }

    public record Command(@NotBlank String username,
                          String firstName,
                          String lastName,
                          String email,
                          String phoneNumber) {
    }
}
