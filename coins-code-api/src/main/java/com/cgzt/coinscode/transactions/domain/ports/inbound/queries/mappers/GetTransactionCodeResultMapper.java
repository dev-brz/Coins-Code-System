package com.cgzt.coinscode.transactions.domain.ports.inbound.queries.mappers;

import com.cgzt.coinscode.transactions.domain.models.TransactionCode;
import com.cgzt.coinscode.transactions.domain.ports.inbound.queries.models.GetTransactionCodeResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GetTransactionCodeResultMapper {
    GetTransactionCodeResult map(TransactionCode source);
}
