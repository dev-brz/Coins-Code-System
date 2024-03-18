package com.cgzt.coinscode.coins.domain.ports.inbound.commands;

import com.cgzt.coinscode.coins.domain.models.Coin;
import com.cgzt.coinscode.coins.domain.ports.outbound.repository.CoinsRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UpdateCoinCommandHandler {
    private final CoinsRepository coinsRepository;

    public void handle(Command command) {
        var coin = Coin.builder()
                .uid(command.uid())
                .name(command.name())
                .username(command.username())
                .description(command.description())
                .build();

        coinsRepository.update(coin);
    }

    public record Command(
            @NotBlank String uid,
            @NotBlank String name,
            @NotBlank String username,
            @NotNull String description
    ) {
    }
}
