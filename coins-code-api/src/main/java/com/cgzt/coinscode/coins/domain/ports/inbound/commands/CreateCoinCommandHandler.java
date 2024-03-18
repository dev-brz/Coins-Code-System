package com.cgzt.coinscode.coins.domain.ports.inbound.commands;

import com.cgzt.coinscode.coins.domain.models.Coin;
import com.cgzt.coinscode.coins.domain.ports.outbound.repository.CoinsRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCoinCommandHandler {
    private final CoinsRepository coinsRepository;

    public void handle(Command command) {
        var coin = Coin.builder()
                .name(command.name())
                .username(command.username())
                .description(command.description())
                .build();

        coinsRepository.save(coin);
    }

    public record Command(
            @NotBlank String name,
            @NotBlank String username,
            @NotBlank String description
    ) {
    }
}
