package com.cgzt.coinscode.users.domain.ports.inbound.queries;

import com.cgzt.coinscode.users.domain.ports.outbound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExistsUserQueryHandler {
    private final UserRepository userRepository;

    public boolean handle(Query query) {
        if (StringUtils.isNotBlank(query.username)) {
            return userRepository.existsByUsername(query.username);
        } else if (StringUtils.isNotBlank(query.email)) {
            return userRepository.existsByEmail(query.email);
        } else if (StringUtils.isNotBlank(query.phoneNumber)) {
            return userRepository.existsByPhoneNumber(query.phoneNumber);
        } else {
            return false;
        }
    }

    public record Query(@Nullable String username, @Nullable String email, @Nullable String phoneNumber) {
    }
}
