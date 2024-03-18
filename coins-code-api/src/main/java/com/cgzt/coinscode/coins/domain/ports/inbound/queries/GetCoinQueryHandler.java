package com.cgzt.coinscode.coins.domain.ports.inbound.queries;

import com.cgzt.coinscode.coins.domain.ports.inbound.queries.mappers.GetCoinsResultMapper;
import com.cgzt.coinscode.coins.domain.ports.inbound.queries.model.GetCoinResult;
import com.cgzt.coinscode.coins.domain.ports.outbound.repository.CoinsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetCoinQueryHandler {
    private final CoinsRepository coinsRepository;
    private final GetCoinsResultMapper mapper;

    public GetCoinResult handle(String uid) {
        var coin = coinsRepository.findByUid(uid);
        return mapper.map(coin);

    }
}
