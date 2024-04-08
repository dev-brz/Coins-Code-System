package com.cgzt.coinscode.transactions.adapters.outbound.mappers;

import com.cgzt.coinscode.coins.adapters.outbound.entities.CoinEntity;
import com.cgzt.coinscode.transactions.adapters.outbound.entities.TransactionEntity;
import com.cgzt.coinscode.transactions.domain.models.Transaction;
import com.cgzt.coinscode.transactions.domain.models.TransactionTarget;
import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionsMapper {

    @Mappings({
            @Mapping(target = "destCoin", expression = "java(mapCoin(transaction.getDest()))"),
            @Mapping(target = "sourceCoin", expression = "java(mapCoin(transaction.getSource()))"),
    })
    TransactionEntity map(Transaction transaction);

    @Mappings({
            @Mapping(target = "dest", expression = "java(mapTarget(transaction.getDest(), transaction.getDestCoin()))"),
            @Mapping(target = "source", expression = "java(mapTarget(transaction.getSource(), transaction.getSourceCoin()))"),
    })
    Transaction map(TransactionEntity transaction);

    List<Transaction> map(List<TransactionEntity> transactions);

    UserAccountEntity mapAccount(TransactionTarget transactionTarget);

    default CoinEntity mapCoin(TransactionTarget target) {
        if (target == null) {
            return null;
        }

        var coinEntity = new CoinEntity();

        coinEntity.setUid(target.getCoinUid());
        coinEntity.setName(target.getCoinName());

        return coinEntity;
    }

    default TransactionTarget mapTarget(UserAccountEntity userAccount, CoinEntity coinEntity) {
        if (userAccount != null && coinEntity != null) {
            return TransactionTarget.builder()
                    .username(userAccount.getUsername())
                    .firstName(userAccount.getFirstName())
                    .lastName(userAccount.getLastName())
                    .coinUid(coinEntity.getName())
                    .coinName(coinEntity.getImageName())
                    .build();
        }
        return null;
    }
}
