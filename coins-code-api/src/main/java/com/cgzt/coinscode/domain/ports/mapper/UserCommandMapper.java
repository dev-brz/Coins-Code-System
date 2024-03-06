package com.cgzt.coinscode.domain.ports.mapper;

import com.cgzt.coinscode.domain.ports.data.UserDto;
import com.cgzt.coinscode.domain.ports.inbound.CreateUserCommandHandler;
import com.cgzt.coinscode.domain.ports.inbound.UpdateUserCommandHandler;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserCommandMapper {
    UserDto toUserDto(CreateUserCommandHandler.Command command);

    UserDto toUserDto(UpdateUserCommandHandler.Command command);
}
