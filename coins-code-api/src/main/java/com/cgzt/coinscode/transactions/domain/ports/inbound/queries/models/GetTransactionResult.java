package com.cgzt.coinscode.transactions.domain.ports.inbound.queries.models;

import com.cgzt.coinscode.transactions.domain.models.TransactionStatus;
import com.cgzt.coinscode.transactions.domain.models.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GetTransactionResult(
        String code,
        GetTransactionTargetResult source,
        GetTransactionTargetResult target,
        BigDecimal amount,
        TransactionStatus status,
        TransactionType type,
        LocalDateTime createdAt,
        String description) {
}
