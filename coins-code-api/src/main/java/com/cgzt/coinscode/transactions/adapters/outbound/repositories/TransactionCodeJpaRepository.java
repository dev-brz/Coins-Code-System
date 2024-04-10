package com.cgzt.coinscode.transactions.adapters.outbound.repositories;

import com.cgzt.coinscode.transactions.adapters.outbound.entities.TransactionCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

interface TransactionCodeJpaRepository extends JpaRepository<TransactionCodeEntity, Long> {
    @Query("SELECT tc FROM TransactionCodeEntity tc WHERE tc.expiresAt < CURRENT_TIMESTAMP ORDER BY tc.expiresAt ASC LIMIT 1")
    Optional<TransactionCodeEntity> findOldestExpiredCode();

    @Query("SELECT COALESCE(MAX(tc.code), 0) FROM TransactionCodeEntity tc")
    int findLastCode();

    @Query("DELETE FROM TransactionCodeEntity tc WHERE tc.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredCodes();

    Optional<TransactionCodeEntity> findByCode(int code);
}
