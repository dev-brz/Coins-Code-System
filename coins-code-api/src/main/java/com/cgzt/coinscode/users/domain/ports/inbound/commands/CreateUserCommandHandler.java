package com.cgzt.coinscode.users.domain.ports.inbound.commands;

import com.cgzt.coinscode.users.domain.models.User;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.mappers.UserCommandMapper;
import com.cgzt.coinscode.users.domain.ports.outbound.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CreateUserCommandHandler {
    private final UserRepository userRepository;
    private final UserCommandMapper userCommandMapper;

    public void handle(final Command command) {
        User user = userCommandMapper.toUser(command);
        userRepository.save(user, command.password);
    }

    public record Command(@NotBlank String username,
                          @NotBlank String firstName,
                          @NotBlank String lastName,
                          @NotBlank String email,
                          @NotBlank String phoneNumber,
                          @NotEmpty char[] password) {
    }

}