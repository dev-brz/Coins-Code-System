package com.cgzt.coinscode.users.domain.ports.inbound.queries;

import com.cgzt.coinscode.users.domain.ports.inbound.queries.mappers.UserQueryMapper;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.models.GetUserResult;
import com.cgzt.coinscode.users.domain.ports.outbound.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetUserQueryHandler {
    private final UserRepository userRepository;
    private final UserQueryMapper mapper;

    public Optional<GetUserResult> handle(Query query) {
        return userRepository.findByUsername(query.username).map(mapper::toUserResult);
    }

    public record Query(String username) {
    }
}
