package com.cgzt.coinscode.domain.ports.inbound;

import com.cgzt.coinscode.domain.ports.inbound.UpdateUserCommandHandler.Command;
import com.cgzt.coinscode.domain.ports.mapper.UserCommandMapper;
import com.cgzt.coinscode.domain.ports.outbound.UserService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUserCommandHandler implements CommandHandler<Command, Boolean> {
    private final UserService userService;
    private final UserCommandMapper mapper;

    @Override
    public Boolean handle(Command command) {
        return userService.updateUser(mapper.toUserDto(command));
    }

    public record Command( @NotBlank(message = "username can not be blank") String username,
                           @NotBlank(message = "firstName can not be blank") String firstName,
                           @NotBlank(message = "lastName can not be blank") String lastName,
                           @NotBlank(message = "email can not be blank") String email,
                           @NotBlank(message = "phoneNumber can not be blank") String phoneNumber ) {
    }
}
