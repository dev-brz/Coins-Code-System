package com.cgzt.coinscode.users.domain.ports.inbound.commands;

import com.cgzt.coinscode.shared.domain.ports.outbound.services.ImageService;
import com.cgzt.coinscode.users.domain.models.User;
import com.cgzt.coinscode.users.domain.models.UserImage;
import com.cgzt.coinscode.users.domain.ports.outbound.repositories.CurrentUserRepository;
import com.cgzt.coinscode.users.domain.ports.outbound.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UpdateUserImageCommandHandler {
    private final CurrentUserRepository currentUserRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    @Value("${user.profile.dir}")
    String imageDir;

    public void handle(final Command command) {
        User currentUser = currentUserRepository.get();
        UserImage image = imageService.upload(currentUser.getUsername(), command.image, imageDir);
        userRepository.updateImageName(currentUser.getUsername(), image.name());
    }

    public record Command(MultipartFile image) {
    }
}
