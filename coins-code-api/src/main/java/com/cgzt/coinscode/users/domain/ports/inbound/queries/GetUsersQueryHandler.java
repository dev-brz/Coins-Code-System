package com.cgzt.coinscode.users.domain.ports.inbound.queries;

import com.cgzt.coinscode.users.domain.ports.inbound.queries.mappers.UserQueryMapper;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.model.GetUsersResult;
import com.cgzt.coinscode.users.domain.ports.outbound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUsersQueryHandler {
    private final UserRepository userRepository;
    private final UserQueryMapper mapper;

    public GetUsersResult handle() {
        return new GetUsersResult(mapper.toUserResults(userRepository.findAll()));
    }
}
