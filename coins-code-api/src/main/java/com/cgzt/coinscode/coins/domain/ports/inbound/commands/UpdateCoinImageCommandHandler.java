package com.cgzt.coinscode.coins.domain.ports.inbound.commands;

import com.cgzt.coinscode.coins.domain.ports.outbound.repository.CoinsRepository;
import com.cgzt.coinscode.shared.domain.ports.outbound.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
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

    public record Command(String uid, MultipartFile image) {
    }
}
