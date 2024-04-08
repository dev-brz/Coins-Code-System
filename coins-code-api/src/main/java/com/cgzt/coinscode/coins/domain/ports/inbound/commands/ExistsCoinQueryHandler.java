package com.cgzt.coinscode.coins.domain.ports.inbound.commands;

import com.cgzt.coinscode.coins.domain.ports.outbound.repositories.CoinsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExistsCoinQueryHandler {
    private final CoinsRepository coinsRepository;

    public boolean handle(Query query) {
        return coinsRepository.exists(query.name(), query.username());
    }

    public record Query(String name, String username) {
    }
}
