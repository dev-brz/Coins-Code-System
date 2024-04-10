package com.cgzt.coinscode.transactions.domain.ports.inbound.commands;

import com.cgzt.coinscode.coins.domain.models.Coin;
import com.cgzt.coinscode.coins.domain.ports.outbound.repositories.CoinsRepository;
import com.cgzt.coinscode.transactions.domain.models.*;
import com.cgzt.coinscode.transactions.domain.ports.outbound.repositories.TransactionCodeRepository;
import com.cgzt.coinscode.transactions.domain.ports.outbound.repositories.TransactionsRepository;
import com.cgzt.coinscode.transactions.domain.ports.outbound.strategies.TransactionNumberGeneratorStrategy;
import com.cgzt.coinscode.users.domain.models.User;
import com.cgzt.coinscode.users.domain.ports.outbound.repositories.CurrentUserRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferCommandHandler {
    private final TransactionsRepository transactionsRepository;
    private final CoinsRepository coinsRepository;
    private final TransactionCodeRepository transactionCodeRepository;
    private final TransactionNumberGeneratorStrategy topUpTransactionNumberGeneratorStrategy;
    private final CurrentUserRepository currentUserRepository;

    @Transactional
    public void handle(TransferCommandHandler.Command command) {
        var transactionCode = transactionCodeRepository.findValidByCode(command.code());
        var fromCoin = coinsRepository.findByUid(command.coinUid());
        var source = createTarget(fromCoin, currentUserRepository.get());
        var dest = createTarget(transactionCode.coin(), transactionCode.owner());
        var amount = transactionCode.amount();

        coinsRepository.remove(source.getCoinUid(), amount);
        coinsRepository.add(dest.getCoinUid(), amount);
        createTransaction(transactionCode, source, dest);
    }


    private TransactionTarget createTarget(Coin coin, User user) {
        return TransactionTarget.builder()
                .coinUid(coin.getUid())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .coinName(coin.getName())
                .build();
    }

    private void createTransaction(TransactionCode transactionCode, TransactionTarget source, TransactionTarget dest) {
        var transaction = Transaction.builder()
                .number(topUpTransactionNumberGeneratorStrategy.generate())
                .source(source)
                .dest(dest)
                .description(transactionCode.description())
                .status(TransactionStatus.COMPLETED)
                .type(TransactionType.TRANSFER)
                .amount(transactionCode.amount())
                .build();

        transactionsRepository.save(transaction);
    }

    public record Command(
            @NotNull Integer code,
            @NotBlank String coinUid
    ) {
    }

}
