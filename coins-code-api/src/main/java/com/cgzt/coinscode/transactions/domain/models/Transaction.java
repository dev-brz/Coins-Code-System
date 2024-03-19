package com.cgzt.coinscode.transactions.domain.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Transaction {
    private String number;
    private TransactionTarget source;
    private TransactionTarget dest;
    private String description;
    private TransactionStatus status;
    private TransactionType type;
    private String createdAt;
    private BigDecimal amount;
}