package com.cgzt.coinscode.transactions.domain.ports.inbound.commands;

import com.cgzt.coinscode.coins.domain.models.Coin;
import com.cgzt.coinscode.coins.domain.ports.outbound.repositories.CoinsRepository;
import com.cgzt.coinscode.transactions.domain.models.Transaction;
import com.cgzt.coinscode.transactions.domain.models.TransactionCode;
import com.cgzt.coinscode.transactions.domain.ports.outbound.repositories.TransactionCodeRepository;
import com.cgzt.coinscode.transactions.domain.ports.outbound.repositories.TransactionsRepository;
import com.cgzt.coinscode.transactions.domain.ports.outbound.strategies.TransactionNumberGeneratorStrategy;
import com.cgzt.coinscode.users.domain.models.User;
import com.cgzt.coinscode.users.domain.ports.outbound.repositories.CurrentUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferCommandHandlerTest {
    @Mock
    TransactionsRepository transactionsRepository;
    @Mock
    CoinsRepository coinsRepository;
    @Mock
    TransactionCodeRepository transactionCodeRepository;
    @Mock
    TransactionNumberGeneratorStrategy topUpTransactionNumberGeneratorStrategy;
    @Mock
    CurrentUserRepository currentUserRepository;

    @InjectMocks
    TransferCommandHandler transferCommandHandler;

    @Test
    void handle_ShouldTransferCoins() {
        var code = 12;
        var coinFromUid = "coinFrom";
        var coinToUid = "coinTo";
        var command = new TransferCommandHandler.Command(code, coinFromUid);
        var transactionCode = mock(TransactionCode.class);
        var coinFrom = mock(Coin.class);
        var coinTo = mock(Coin.class);
        var userFrom = mock(User.class);
        var userTo = mock(User.class);

        when(transactionCodeRepository.findValidByCode(code)).thenReturn(transactionCode);
        when(coinsRepository.findByUid(coinFromUid)).thenReturn(coinFrom);
        when(currentUserRepository.get()).thenReturn(userFrom);
        when(userFrom.getUsername()).thenReturn("from");
        when(userTo.getUsername()).thenReturn("to");
        when(coinTo.getUid()).thenReturn(coinToUid);
        when(coinFrom.getUid()).thenReturn(coinFromUid);
        when(transactionCode.coin()).thenReturn(coinTo);
        when(transactionCode.owner()).thenReturn(userTo);
        when(transactionCode.amount()).thenReturn(BigDecimal.ONE);
        when(topUpTransactionNumberGeneratorStrategy.generate()).thenReturn("transactionNumber");

        transferCommandHandler.handle(command);

        verify(coinsRepository).remove(coinFromUid, BigDecimal.ONE);
        verify(coinsRepository).add(coinToUid, BigDecimal.ONE);
        verify(transactionsRepository).save(any(Transaction.class));
        verify(coinTo).getUid();
        verify(coinTo).getName();
        verify(coinFrom).getUid();
        verify(coinFrom).getName();
        verify(userFrom).getUsername();
        verify(userFrom).getFirstName();
        verify(userFrom).getLastName();
        verify(userTo).getUsername();
        verify(userTo).getFirstName();
        verify(userTo).getLastName();
        verify(transactionCode).description();
        verify(transactionCode, times(2)).amount();
        verify(transactionCode).coin();
        verify(transactionCode).owner();
    }
}