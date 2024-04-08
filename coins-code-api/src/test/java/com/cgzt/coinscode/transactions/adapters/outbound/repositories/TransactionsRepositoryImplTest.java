package com.cgzt.coinscode.transactions.adapters.outbound.repositories;

import com.cgzt.coinscode.coins.adapters.outbound.entities.CoinEntity;
import com.cgzt.coinscode.coins.domain.ports.outbound.repositories.CoinsRepository;
import com.cgzt.coinscode.transactions.adapters.outbound.entities.TransactionEntity;
import com.cgzt.coinscode.transactions.adapters.outbound.mappers.TransactionsMapper;
import com.cgzt.coinscode.transactions.domain.models.Transaction;
import com.cgzt.coinscode.transactions.domain.models.TransactionStatus;
import com.cgzt.coinscode.transactions.domain.models.TransactionTarget;
import com.cgzt.coinscode.transactions.domain.models.TransactionType;
import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccountEntity;
import com.cgzt.coinscode.users.domain.ports.outbound.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionsRepositoryImplTest {
    Transaction transaction;
    @Mock
    TransactionsJpaRepository transactionsJpaRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CoinsRepository coinsRepository;
    @Mock
    TransactionsMapper mapper;
    @InjectMocks
    TransactionsRepositoryImpl transactionsRepository;

    @BeforeEach
    void setUp() {
        var source = TransactionTarget.builder().coinUid("sourceCoinUid").coinName("sourceCoinName").username("sourceUsername").firstName("sourceFirstName").lastName("sourceLastName").build();

        transaction = Transaction.builder().number("123").source(source).dest(source).description("Test Transaction").status(TransactionStatus.PENDING).type(TransactionType.TOP_UP).createdAt("2022-01-01T00:00:00").amount(new BigDecimal("1000")).build();
    }

    @Test
    void save_Success() {
        var transactionEntity = new TransactionEntity();
        var target = new UserAccountEntity();
        var targetCoin = new CoinEntity();

        targetCoin.setUid("sourceCoinUid");

        target.setUsername("sourceUsername");

        transactionEntity.setSource(target);
        transactionEntity.setDest(target);

        transactionEntity.setSourceCoin(targetCoin);
        transactionEntity.setDestCoin(targetCoin);

        when(mapper.map(transaction)).thenReturn(transactionEntity);
        when(coinsRepository.findIdByUid(transaction.getSource().getCoinUid())).thenReturn(1L);
        when(userRepository.findIdByUsername(transaction.getSource().getUsername())).thenReturn(Optional.of(1L));

        transactionsRepository.save(transaction);

        verify(transactionsJpaRepository).save(transactionEntity);
        assertNotNull(transactionEntity.getSource().getId());
        assertNotNull(transactionEntity.getDest().getId());
        assertNotNull(transactionEntity.getSourceCoin().getId());
        assertNotNull(transactionEntity.getDestCoin().getId());
    }

    @Test
    void findByNumber_Success() {
        String number = "123";
        TransactionEntity transactionEntity = new TransactionEntity();
        when(transactionsJpaRepository.findByNumber(number)).thenReturn(Optional.of(transactionEntity));
        when(mapper.map(transactionEntity)).thenReturn(transaction);

        Transaction result = transactionsRepository.findByNumber(number);

        assertEquals(transaction, result);
    }

    @Test
    void findByNumber_NotFound() {
        String number = "123";
        when(transactionsJpaRepository.findByNumber(number)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> transactionsRepository.findByNumber(number));
    }
}