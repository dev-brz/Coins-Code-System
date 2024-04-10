package com.cgzt.coinscode.transactions.domain.ports.inbound.queries.models;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GetTransactionCodeResult(
        String code,
        GetUserResult owner,
        BigDecimal amount,
        LocalDateTime expiresAt,
        String description) {

    public record GetUserResult(
            String username,
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            boolean active,
            String imageName
    ) {
    }
}
