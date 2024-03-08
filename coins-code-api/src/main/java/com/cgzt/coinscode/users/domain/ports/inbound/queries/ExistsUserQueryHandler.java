package com.cgzt.coinscode.users.domain.ports.inbound.queries;

import com.cgzt.coinscode.users.domain.ports.outbound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExistsUserQueryHandler {
    private final UserRepository userRepository;

    public Result handle(Query query) {
        boolean userExists = userRepository.existsByUsername(query.username);
        return new Result(userExists);
    }

    public record Query(String username) {
    }

    public record Result(boolean userExists) {
    }
}
