package com.cgzt.coinscode.transactions.adapters.inbound.jobs;

import com.cgzt.coinscode.users.domain.ports.outbound.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
class ResetLimitsJob {
    private final UserRepository userRepository;

    @Scheduled(cron = "${user.limits.reset-cron}")
    void cleanExpiredCodes() {
        userRepository.resetLimits();
    }
}