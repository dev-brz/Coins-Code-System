package com.cgzt.coinscode.transactions.domain.ports.inbound.queries;

import com.cgzt.coinscode.transactions.domain.ports.inbound.queries.mappers.GetTransactionResultMapper;
import com.cgzt.coinscode.transactions.domain.ports.inbound.queries.models.GetTransactionsResult;
import com.cgzt.coinscode.transactions.domain.ports.outbound.repositories.TransactionsRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetTransactionsQueryHandler {
    private final TransactionsRepository transactionsRepository;
    private final GetTransactionResultMapper mapper;

    public GetTransactionsResult handle(Query query) {
        if (StringUtils.isNotBlank(query.coinUid)) {
            return new GetTransactionsResult(mapper.map(transactionsRepository.findAllByCoinUid(query.coinUid)));
        }

        if (StringUtils.isNotBlank(query.username)) {
            return new GetTransactionsResult(mapper.map(transactionsRepository.findAllByUsername(query.username)));
        }

        return new GetTransactionsResult(mapper.map(transactionsRepository.findAllByUsername(query.username)));
    }

    public record Query(
            String username,
            String coinUid) {
    }
}
