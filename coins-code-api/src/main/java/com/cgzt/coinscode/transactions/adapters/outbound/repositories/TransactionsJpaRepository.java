package com.cgzt.coinscode.transactions.adapters.outbound.repositories;

import com.cgzt.coinscode.transactions.adapters.outbound.entities.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

interface TransactionsJpaRepository extends JpaRepository<TransactionEntity, Long> {
    Optional<TransactionEntity> findByNumber(String number);

    @Query("SELECT t FROM TransactionEntity t WHERE t.source.username = :username OR t.dest.username = :username")
    List<TransactionEntity> findAllByUsername(String username);

    @Query("SELECT t FROM TransactionEntity t WHERE t.sourceCoin.uid = :coinUid OR t.destCoin.uid = :coinUid")
    List<TransactionEntity> findAllByCoinUid(String coinUid);
}
