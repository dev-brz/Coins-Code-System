package com.cgzt.coinscode.coins.domain.ports.inbound.queries.mappers;

import com.cgzt.coinscode.coins.domain.models.Coin;
import com.cgzt.coinscode.coins.domain.ports.inbound.queries.models.GetCoinResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GetCoinsResultMapper {
    @Mapping(target = "transactionCodes.owner", ignore = true)
    GetCoinResult map(Coin coin);

    List<GetCoinResult> map(List<Coin> coins);
}
