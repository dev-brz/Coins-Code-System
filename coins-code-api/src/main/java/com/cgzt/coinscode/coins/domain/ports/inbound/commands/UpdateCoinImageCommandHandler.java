package com.cgzt.coinscode.coins.domain.ports.inbound.commands;

import com.cgzt.coinscode.coins.domain.ports.outbound.repositories.CoinsRepository;
import com.cgzt.coinscode.shared.domain.ports.outbound.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateCoinImageCommandHandler {
    private final ImageService imageService;
    private final CoinsRepository coinsRepository;

    @Value("${coin.image.dir}")
    private String imageDir;

    public void handle(Command command) {
        coinsRepository.validate(command.uid());
        var image = imageService.upload(command.uid(), command.image(), imageDir);
        coinsRepository.updateImageName(command.uid(), image.name());
    }

    public record Command(String uid, InputStreamSource image) {
    }
}
