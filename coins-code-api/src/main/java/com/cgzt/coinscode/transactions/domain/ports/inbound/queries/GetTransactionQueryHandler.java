package com.cgzt.coinscode.transactions.domain.ports.inbound.queries;

import com.cgzt.coinscode.transactions.domain.ports.inbound.commands.mappers.GetTransactionResultMapper;
import com.cgzt.coinscode.transactions.domain.ports.inbound.queries.model.GetTransactionResult;
import com.cgzt.coinscode.transactions.domain.ports.outbound.repository.TransactionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GetTransactionQueryHandler {
    private final TransactionsRepository transactionsRepository;
    private final GetTransactionResultMapper mapper;

    public GetTransactionResult getTransactionByCode(String number) {
        return mapper.map(transactionsRepository.findByNumber(number));
    }
}
