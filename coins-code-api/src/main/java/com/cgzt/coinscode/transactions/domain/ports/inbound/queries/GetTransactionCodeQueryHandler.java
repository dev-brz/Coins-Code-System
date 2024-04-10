package com.cgzt.coinscode.transactions.domain.ports.inbound.queries;

import com.cgzt.coinscode.transactions.domain.ports.inbound.queries.mappers.GetTransactionCodeResultMapper;
import com.cgzt.coinscode.transactions.domain.ports.inbound.queries.models.GetTransactionCodeResult;
import com.cgzt.coinscode.transactions.domain.ports.outbound.repositories.TransactionCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetTransactionCodeQueryHandler {
    private final TransactionCodeRepository transactionCodeRepository;
    private final GetTransactionCodeResultMapper mapper;

    public GetTransactionCodeResult getTransactionCodeByCode(Integer code) {
        return mapper.map(transactionCodeRepository.findByCode(code));
    }
}
