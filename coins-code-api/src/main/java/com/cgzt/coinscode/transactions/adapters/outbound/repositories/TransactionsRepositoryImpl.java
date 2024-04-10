package com.cgzt.coinscode.transactions.adapters.outbound.repositories;

import com.cgzt.coinscode.coins.adapters.outbound.entities.CoinEntity;
import com.cgzt.coinscode.coins.domain.ports.outbound.repositories.CoinsRepository;
import com.cgzt.coinscode.transactions.adapters.outbound.entities.TransactionEntity;
import com.cgzt.coinscode.transactions.adapters.outbound.mappers.TransactionsMapper;
import com.cgzt.coinscode.transactions.domain.models.Transaction;
import com.cgzt.coinscode.transactions.domain.models.TransactionType;
import com.cgzt.coinscode.transactions.domain.ports.outbound.repositories.TransactionsRepository;
import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccountEntity;
import com.cgzt.coinscode.users.domain.ports.outbound.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Repository
@RequiredArgsConstructor
class TransactionsRepositoryImpl implements TransactionsRepository {
    private final TransactionsJpaRepository transactionsJpaRepository;
    private final UserRepository userRepository;
    private final CoinsRepository coinsRepository;
    private final TransactionsMapper mapper;

    @Override
    public void save(Transaction transaction) {
        var transactionEntity = mapper.map(transaction);

        populate(transactionEntity);

        if (transaction.getType() == TransactionType.TRANSFER) {
            userRepository.incrementNumberOfSends(transaction.getSource().getUsername());
            userRepository.incrementNumberOfReceives(transaction.getDest().getUsername());
        }
        
        transactionsJpaRepository.save(transactionEntity);
    }

    @Override
    public Transaction findByNumber(String number) {
        return transactionsJpaRepository.findByNumber(number)
                .map(mapper::map)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
    }

    @Override
    public List<Transaction> findAllByUsername(String username) {
        return mapper.map(transactionsJpaRepository.findAllByUsername(username));
    }

    @Override
    public List<Transaction> findAllByCoinUid(String coinUid) {
        return mapper.map(transactionsJpaRepository.findAllByCoinUid(coinUid));
    }

    private void populate(TransactionEntity target) {
        populateUserId(target.getSource());
        populateUserId(target.getDest());

        populateCoinId(target.getSourceCoin());
        populateCoinId(target.getDestCoin());
    }

    private void populateCoinId(CoinEntity coin) {
        coin.setId(coinsRepository.findIdByUid(coin.getUid()));
    }

    private void populateUserId(UserAccountEntity user) {
        userRepository.findIdByUsername(user.getUsername())
                .ifPresent(user::setId);
    }
}
