package com.cgzt.coinscode.transactions.domain.ports.outbound.repositories;

import com.cgzt.coinscode.transactions.domain.models.Transaction;

import java.util.List;

public interface TransactionsRepository {
    void save(Transaction transaction);

    Transaction findByNumber(String number);

    List<Transaction> findAllByUsername(String username);

    List<Transaction> findAllByCoinUid(String coinUid);
}
