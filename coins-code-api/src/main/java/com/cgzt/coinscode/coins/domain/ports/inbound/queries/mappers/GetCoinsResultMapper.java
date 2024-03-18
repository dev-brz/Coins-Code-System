package com.cgzt.coinscode.coins.domain.ports.inbound.queries.mappers;

import com.cgzt.coinscode.coins.domain.models.Coin;
import com.cgzt.coinscode.coins.domain.ports.inbound.queries.model.GetCoinResult;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GetCoinsResultMapper {
    GetCoinResult map(Coin coin);

    List<GetCoinResult> map(List<Coin> coins);
}
