package com.cgzt.coinscode.transactions.domain.ports.inbound.commands;

import com.cgzt.coinscode.coins.domain.ports.outbound.repository.CoinsRepository;
import com.cgzt.coinscode.transactions.domain.models.Transaction;
import com.cgzt.coinscode.transactions.domain.models.TransactionStatus;
import com.cgzt.coinscode.transactions.domain.models.TransactionTarget;
import com.cgzt.coinscode.transactions.domain.models.TransactionType;
import com.cgzt.coinscode.transactions.domain.ports.outbound.repository.TransactionsRepository;
import com.cgzt.coinscode.transactions.domain.ports.outbound.strategies.TransactionNumberGeneratorStrategy;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class TopUpCommandHandler {
    private final CoinsRepository coinsRepository;
    private final TransactionsRepository transactionsRepository;
    private final TransactionNumberGeneratorStrategy topUpTransactionNumberGeneratorStrategy;

    @Transactional
    public void handle(Command command) {
        coinsRepository.validate(command.coinUid(), command.username());


        var source = TransactionTarget.builder()
                .coinUid(command.coinUid())
                .username(command.username())
                .build();

        var transaction = Transaction.builder()
                .number(topUpTransactionNumberGeneratorStrategy.generate())
                .source(source)
                .dest(source)

                .description(command.description())
                .status(TransactionStatus.APPROVED)
                .type(TransactionType.TOP_UP)
                .amount(command.amount())
                .build();

        coinsRepository.add(command.coinUid(), command.amount());
        transactionsRepository.save(transaction);
    }

    public record Command(
            @NotBlank String username,
            @NotBlank String coinUid,
            @DecimalMin(value = "0") BigDecimal amount,
            @NotNull String description) {
    }
}
