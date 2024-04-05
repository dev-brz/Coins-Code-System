package com.cgzt.coinscode.coins.adapters.outbound.mappers;

import com.cgzt.coinscode.coins.adapters.outbound.entities.CoinEntity;
import com.cgzt.coinscode.coins.domain.models.Coin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CoinsMapper {
    @Mapping(target = "username", source = "coin.userAccount.username")
    Coin map(CoinEntity coin);

    CoinEntity map(Coin coin);

    List<Coin> map(List<CoinEntity> coins);
}
