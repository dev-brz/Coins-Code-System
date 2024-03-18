package com.cgzt.coinscode.coins.domain.ports.inbound.commands;

import com.cgzt.coinscode.coins.domain.ports.outbound.repository.CoinsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DeleteCoinCommandHandler {
    private final CoinsRepository coinsRepository;

    public void handle(String uid) {
        coinsRepository.delete(uid);
    }
}
