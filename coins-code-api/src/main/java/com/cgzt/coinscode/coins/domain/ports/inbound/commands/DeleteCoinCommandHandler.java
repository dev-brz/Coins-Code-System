package com.cgzt.coinscode.coins.domain.ports.inbound.commands;

import com.cgzt.coinscode.coins.domain.ports.outbound.repositories.CoinsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteCoinCommandHandler {
    private final CoinsRepository coinsRepository;

    public void handle(String uid) {
        coinsRepository.delete(uid);
    }
}
