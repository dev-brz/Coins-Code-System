package com.cgzt.coinscode.transactions.adapters.outbound.repositories;

import com.cgzt.coinscode.coins.adapters.outbound.entities.CoinEntity;
import com.cgzt.coinscode.coins.domain.ports.outbound.repositories.CoinsRepository;
import com.cgzt.coinscode.transactions.adapters.outbound.entities.TransactionCodeEntity;
import com.cgzt.coinscode.transactions.adapters.outbound.mappers.TransactionCodeMapper;
import com.cgzt.coinscode.transactions.domain.models.TransactionCode;
import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccountEntity;
import com.cgzt.coinscode.users.domain.ports.outbound.repositories.CurrentUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionCodeRepositoryImplTest {
    @Spy
    TransactionCodeEntity entity;

    @Mock
    TransactionCodeJpaRepository transactionsCodeJpaRepository;
    @Mock
    TransactionCodeMapper transactionCodeMapper;
    @InjectMocks
    TransactionCodeRepositoryImpl transactionCodeRepository;
    @Mock
    CoinsRepository coinRepository;
    @Mock
    CurrentUserRepository currentUserRepository;


    @Test
    void should_generateCode_when_noExpiredCodeExist() {
        var transactionCode = mock(TransactionCode.class);
        var newCodeEntity = new TransactionCodeEntity();
        var coinUid = "coinUid";
        var amount = BigDecimal.valueOf(1000);
        var description = "Test Transaction";
        var coin = new CoinEntity();

        coin.setId(1L);

        newCodeEntity.setId(1L);
        newCodeEntity.setCoin(coin);
        newCodeEntity.setAmount(amount);
        newCodeEntity.setDescription(description);
        newCodeEntity.setExpiresAt(LocalDateTime.now().plusSeconds(30));

        when(transactionsCodeJpaRepository.findOldestExpiredCode()).thenReturn(Optional.empty());
        when(coinRepository.findIdByUid(coinUid)).thenReturn(1L);
        when(transactionsCodeJpaRepository.save(any())).thenReturn(newCodeEntity);
        when(transactionsCodeJpaRepository.findLastCode()).thenReturn(0);
        when(currentUserRepository.getId()).thenReturn(Optional.of(1L));
        when(transactionCodeMapper.map(newCodeEntity)).thenReturn(transactionCode);


        transactionCodeRepository.generateTransactionCode(coinUid, amount, description);
    }

    @Test
    void should_generateCode_when_expiredCodeExist() {
        var transactionCode = mock(TransactionCode.class);
        var coinUid = "coinUid";
        var amount = BigDecimal.valueOf(1000);
        var description = "Test Transaction";
        var user = new UserAccountEntity();
        var coin = new CoinEntity();

        user.setId(100L);
        coin.setId(100L);

        when(transactionsCodeJpaRepository.findOldestExpiredCode()).thenReturn(Optional.of(entity));
        when(currentUserRepository.getId()).thenReturn(Optional.of(100L));
        when(coinRepository.findIdByUid(coinUid)).thenReturn(100L);
        when(transactionsCodeJpaRepository.save(entity)).thenReturn(entity);
        when(transactionCodeMapper.map(entity)).thenReturn(transactionCode);


        transactionCodeRepository.generateTransactionCode(coinUid, amount, description);

        verify(entity).setCoin(refEq(coin));
        verify(entity).setOwner(refEq(user));
        verify(entity).setAmount(amount);
        verify(entity).setDescription(description);
        verify(entity).setExpiresAt(any());
    }

    @Test
    void shouldThrow_gone_when_expiredCode_isFound() {
        var transactionCode = mock(TransactionCode.class);
        var codeEntity = new TransactionCodeEntity();

        when(transactionsCodeJpaRepository.findByCode(1)).thenReturn(Optional.of(codeEntity));
        when(transactionCodeMapper.map(codeEntity)).thenReturn(transactionCode);
        when(transactionCode.expiresAt()).thenReturn(LocalDateTime.now().minusMinutes(10));

        assertThrows(ResponseStatusException.class, () -> transactionCodeRepository.findValidByCode(1));
    }

    @Test
    void shouldReturn_transactionCode_when_codeIsValid() {
        var transactionCode = mock(TransactionCode.class);
        var codeEntity = new TransactionCodeEntity();

        when(transactionsCodeJpaRepository.findByCode(1)).thenReturn(Optional.of(codeEntity));
        when(transactionCodeMapper.map(codeEntity)).thenReturn(transactionCode);
        when(transactionCode.expiresAt()).thenReturn(LocalDateTime.now().plusMinutes(10));

        var validByCode = transactionCodeRepository.findValidByCode(1);

        assertEquals(transactionCode, validByCode);
        verify(transactionCode, times(2)).expiresAt();
    }
}