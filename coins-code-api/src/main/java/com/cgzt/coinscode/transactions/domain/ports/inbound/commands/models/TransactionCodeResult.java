package com.cgzt.coinscode.transactions.domain.ports.inbound.commands.models;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionCodeResult(
        String code,
        CoinResult coin,
        BigDecimal amount,
        LocalDateTime expiresAt,
        String description) {
    public record CoinResult(
            String uid,
            String name,
            String imageName,
            String description,
            BigDecimal amount
    ) {
    }
}




