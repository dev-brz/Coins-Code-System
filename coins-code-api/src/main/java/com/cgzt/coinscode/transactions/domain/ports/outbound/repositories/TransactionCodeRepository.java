package com.cgzt.coinscode.transactions.domain.ports.outbound.repositories;

import com.cgzt.coinscode.transactions.domain.models.TransactionCode;

import java.math.BigDecimal;

public interface TransactionCodeRepository {
    TransactionCode generateTransactionCode(String coinUid, BigDecimal amount, String description);

    void cleanExpiredCodes();

    TransactionCode findByCode(int code);

    TransactionCode findValidByCode(int code);

    boolean validate(int code);
}
