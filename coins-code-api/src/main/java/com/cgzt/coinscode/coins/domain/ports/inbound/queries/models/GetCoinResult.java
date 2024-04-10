package com.cgzt.coinscode.coins.domain.ports.inbound.queries.models;

import com.cgzt.coinscode.transactions.domain.ports.inbound.queries.models.GetTransactionCodeResult;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.List;

public record GetCoinResult(
        String uid,
        @JsonIgnore String username,
        String name,
        String imageName,
        String description,
        BigDecimal amount,
        List<GetTransactionCodeResult> transactionCodes
) {
}
