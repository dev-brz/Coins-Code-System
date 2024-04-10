package com.cgzt.coinscode.transactions.adapters.inbound.jobs;

import com.cgzt.coinscode.transactions.domain.ports.outbound.repositories.TransactionCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
class CleanExpiredCodesJob {
    private final TransactionCodeRepository transactionCodeRepository;

    @Scheduled(cron = "${transaction.code.clean-cron}")
    void cleanExpiredCodes() {
        transactionCodeRepository.cleanExpiredCodes();
    }
}