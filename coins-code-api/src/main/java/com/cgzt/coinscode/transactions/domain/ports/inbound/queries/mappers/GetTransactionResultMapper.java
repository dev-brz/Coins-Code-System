package com.cgzt.coinscode.transactions.domain.ports.inbound.queries.mappers;

import com.cgzt.coinscode.transactions.domain.models.Transaction;
import com.cgzt.coinscode.transactions.domain.ports.inbound.queries.models.GetTransactionResult;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GetTransactionResultMapper {
    GetTransactionResult map(Transaction transaction);

    List<GetTransactionResult> map(List<Transaction> transactions);
}
