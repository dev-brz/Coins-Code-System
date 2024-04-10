package com.cgzt.coinscode.transactions.domain.models;

import com.cgzt.coinscode.coins.domain.models.Coin;
import com.cgzt.coinscode.users.domain.models.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionCode(
        String code,
        User owner,
        Coin coin,
        BigDecimal amount,
        LocalDateTime expiresAt,
        String description) {
}
