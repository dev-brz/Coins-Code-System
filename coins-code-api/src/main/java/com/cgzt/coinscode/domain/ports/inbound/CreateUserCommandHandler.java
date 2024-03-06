package com.cgzt.coinscode.domain.ports.inbound;

import com.cgzt.coinscode.domain.ports.mapper.UserCommandMapper;
import com.cgzt.coinscode.domain.ports.outbound.UserService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CreateUserCommandHandler implements CommandHandler<CreateUserCommandHandler.Command, Boolean> {
    private final UserService userService;
    private final UserCommandMapper mapper;
    private final PasswordEncoder encoder;


    public Boolean handle(final Command command) {
        var user = mapper.toUserDto(command);
        var password = encoder.encode(command.password());

        userService.createUserAccount(user, password);
        return true;
    }

    public record Command( @NotBlank(message = "username should not be null") String username,
                           @NotBlank(message = "firstName should not be null") String firstName,
                           @NotBlank(message = "lastName should not be null") String lastName,
                           @NotBlank(message = "email should not be null") String email,
                           @NotBlank(message = "phoneNumber should not be null") String phoneNumber,
                           @NotBlank(message = "password should not be null") String password ) {
    }

}