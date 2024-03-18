package com.cgzt.coinscode.coins.adapters.outbound.mappers;

import com.cgzt.coinscode.coins.adapters.outbound.entities.CoinEntity;
import com.cgzt.coinscode.coins.domain.models.Coin;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CoinsMapper {
    Coin map(CoinEntity coin);

    CoinEntity map(Coin coin);

    List<Coin> map(List<CoinEntity> coins);
}
