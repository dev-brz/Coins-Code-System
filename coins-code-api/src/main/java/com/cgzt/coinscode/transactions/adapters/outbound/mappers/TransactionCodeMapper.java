package com.cgzt.coinscode.transactions.adapters.outbound.mappers;

import com.cgzt.coinscode.transactions.adapters.outbound.entities.TransactionCodeEntity;
import com.cgzt.coinscode.transactions.domain.models.TransactionCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionCodeMapper {

    @Mapping(target = "coin.transactionCodes", ignore = true)
    TransactionCode map(TransactionCodeEntity savedCode);

}
