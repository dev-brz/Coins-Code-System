package com.cgzt.coinscode.domain.ports.inbound;

import com.cgzt.coinscode.domain.ports.inbound.UpdateUserPasswordCommandHandler.Command;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUserPasswordCommandHandler implements CommandHandler<Command, Boolean> {
    private final JdbcUserDetailsManager userService;

    @Override
    public Boolean handle(Command command) {
        userService.changePassword(command.oldPassword(), command.newPassword());
        return true;
    }

    public record Command( @NotBlank(message = "old password can not be blank") String oldPassword,
                           @NotBlank(message = "new password can not be blank") String newPassword ) {
    }
}
