package com.cgzt.coinscode.coins.adapters.outbound.mappers;

import com.cgzt.coinscode.coins.adapters.outbound.entities.CoinEntity;
import com.cgzt.coinscode.coins.domain.models.Coin;
import com.cgzt.coinscode.transactions.adapters.outbound.entities.TransactionCodeEntity;
import com.cgzt.coinscode.transactions.domain.models.TransactionCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CoinsMapper {
    @Mapping(target = "username", source = "coin.userAccount.username")
    Coin map(CoinEntity coin);

    CoinEntity map(Coin coin);

    @Mapping(target = "coin", ignore = true)
    @Mapping(target = "owner", ignore = true)
    TransactionCode map(TransactionCodeEntity source);

    List<Coin> map(List<CoinEntity> coins);
}
