package com.cgzt.coinscode.transactions.adapters.outbound.repositories;

import com.cgzt.coinscode.coins.adapters.outbound.entities.CoinEntity;
import com.cgzt.coinscode.coins.domain.ports.outbound.repositories.CoinsRepository;
import com.cgzt.coinscode.transactions.adapters.outbound.entities.TransactionCodeEntity;
import com.cgzt.coinscode.transactions.adapters.outbound.mappers.TransactionCodeMapper;
import com.cgzt.coinscode.transactions.domain.models.TransactionCode;
import com.cgzt.coinscode.transactions.domain.ports.outbound.repositories.TransactionCodeRepository;
import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccountEntity;
import com.cgzt.coinscode.users.domain.ports.outbound.repositories.CurrentUserRepository;
import com.cgzt.coinscode.users.domain.ports.outbound.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Repository
@RequiredArgsConstructor
class TransactionCodeRepositoryImpl implements TransactionCodeRepository {
    private final TransactionCodeJpaRepository transactionCodeJpaRepository;
    private final CoinsRepository coinsRepository;
    private final TransactionCodeMapper mapper;
    private final CurrentUserRepository currentUserRepository;
    private final UserRepository userRepository;

    @Value("${transaction.code.expiration-time}")
    private final int expirationTime = 30;

    @Override
    @Transactional
    public TransactionCode generateTransactionCode(String coinUid, BigDecimal amount, String description) {
        var coinEntity = new CoinEntity();
        var userAccountEntity = new UserAccountEntity();

        var transactionCodeEntity = transactionCodeJpaRepository.findOldestExpiredCode()
                .orElseGet(() -> {
                    var entity = new TransactionCodeEntity();
                    var code = transactionCodeJpaRepository.findLastCode() + 1;
                    entity.setCode(code);
                    return entity;
                });

        var coinId = coinsRepository.findIdByUid(coinUid);

        currentUserRepository.getId().ifPresent(userAccountEntity::setId);
        coinEntity.setId(coinId);

        transactionCodeEntity.setCoin(coinEntity);
        transactionCodeEntity.setOwner(userAccountEntity);
        transactionCodeEntity.setAmount(amount);
        transactionCodeEntity.setDescription(description);
        transactionCodeEntity.setExpiresAt(LocalDateTime.now().plusSeconds(30));

        var savedCode = transactionCodeJpaRepository.save(transactionCodeEntity);

        return mapper.map(savedCode);
    }

    @Override
    public void cleanExpiredCodes() {
        transactionCodeJpaRepository.deleteExpiredCodes();
    }

    @Override
    public TransactionCode findByCode(int code) {
        var codeEntity = transactionCodeJpaRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Code not found"));

        return mapper.map(codeEntity);
    }

    @Override
    public TransactionCode findValidByCode(int code) {
        var transactionCode = findByCode(code);

        validate(transactionCode);

        return transactionCode;
    }

    @Override
    public boolean validate(int code) {
        var codeEntity = transactionCodeJpaRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Code not found"));

        if (codeEntity.getExpiresAt() == null || codeEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "Transaction code is expired");
        }

        return true;
    }

    private boolean validate(TransactionCode code) {
        if (code.expiresAt() == null || code.expiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "Transaction code is expired");
        }

        return true;
    }
}
