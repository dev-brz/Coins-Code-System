package com.cgzt.coinscode.users.domain.ports.inbound.commands.mappers;

import com.cgzt.coinscode.users.domain.models.User;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.CreateUserCommandHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.UpdateUserCommandHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.models.UserUpdate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserCommandMapper {
    User toUser(CreateUserCommandHandler.Command command);

    UserUpdate toUserUpdate(UpdateUserCommandHandler.Command command);
}
