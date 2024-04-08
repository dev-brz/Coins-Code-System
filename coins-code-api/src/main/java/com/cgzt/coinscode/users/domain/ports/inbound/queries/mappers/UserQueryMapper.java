package com.cgzt.coinscode.users.domain.ports.inbound.queries.mappers;

import com.cgzt.coinscode.users.domain.models.User;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.models.GetUserResult;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserQueryMapper {
    GetUserResult toUserResult(User user);

    List<GetUserResult> toUserResults(List<User> users);
}
