package com.cgzt.coinscode.transactions.domain.ports.inbound.commands;

import com.cgzt.coinscode.coins.domain.ports.outbound.repositories.CoinsRepository;
import com.cgzt.coinscode.transactions.domain.models.Transaction;
import com.cgzt.coinscode.transactions.domain.models.TransactionStatus;
import com.cgzt.coinscode.transactions.domain.models.TransactionTarget;
import com.cgzt.coinscode.transactions.domain.models.TransactionType;
import com.cgzt.coinscode.transactions.domain.ports.outbound.repositories.TransactionsRepository;
import com.cgzt.coinscode.transactions.domain.ports.outbound.strategies.TransactionNumberGeneratorStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TopUpCommandHandlerTest {
    @Mock
    CoinsRepository coinsRepository;
    @Mock
    TransactionsRepository transactionsRepository;
    @Mock
    TransactionNumberGeneratorStrategy topUpTransactionNumberGeneratorStrategy;
    @InjectMocks
    TopUpCommandHandler topUpCommandHandler;

    @Test
    void testHandleSuccess() {
        var transactionNumber = "123456";
        var command = new TopUpCommandHandler.Command(
                "username",
                "coinUid",
                BigDecimal.TEN,
                "description");

        var target = TransactionTarget.builder()
                .coinUid(command.coinUid())
                .username(command.username())
                .build();

        var transaction = Transaction.builder()
                .number(transactionNumber)
                .dest(target)
                .source(target)
                .description(command.description())
                .status(TransactionStatus.APPROVED)
                .type(TransactionType.TOP_UP)
                .amount(BigDecimal.TEN)
                .build();

        when(topUpTransactionNumberGeneratorStrategy.generate()).thenReturn(transactionNumber);

        topUpCommandHandler.handle(command);

        verify(transactionsRepository, times(1)).save(transaction);
        verify(coinsRepository, times(1)).add(command.coinUid(), command.amount());
    }
}