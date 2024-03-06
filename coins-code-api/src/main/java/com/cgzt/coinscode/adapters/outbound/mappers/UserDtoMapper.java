package com.cgzt.coinscode.adapters.outbound.mappers;

import com.cgzt.coinscode.adapters.outbound.entities.UserDao;
import com.cgzt.coinscode.domain.ports.data.UserDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    UserDto daoToDto(UserDao userDao);

    List<UserDto> daoListToDtoList(List<UserDao> users);

    UserDao dtoToDao(UserDto user);
}
