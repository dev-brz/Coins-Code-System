package com.cgzt.coinscode.transactions.domain.ports.inbound.commands.mappers;

import com.cgzt.coinscode.transactions.domain.models.TransactionCode;
import com.cgzt.coinscode.transactions.domain.ports.inbound.commands.models.TransactionCodeResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionResultCodeMapper {
    TransactionCodeResult map(TransactionCode source);
}
