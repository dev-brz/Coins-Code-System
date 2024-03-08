package com.cgzt.coinscode.users.domain.ports.inbound.commands;

import com.cgzt.coinscode.users.domain.models.User;
import com.cgzt.coinscode.users.domain.models.UserImage;
import com.cgzt.coinscode.users.domain.ports.outbound.repository.CurrentUserRepository;
import com.cgzt.coinscode.users.domain.ports.outbound.service.ImageService;
import com.cgzt.coinscode.users.domain.ports.outbound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UpdateUserImageCommandHandler {
    private final CurrentUserRepository currentUserRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;

    public void handle(final Command command) {
        User currentUser = currentUserRepository.get();
        UserImage image = imageService.upload(currentUser.getUsername(), command.image);
        userRepository.updateImageName(currentUser.getUsername(), image.name());
    }

    public record Command(MultipartFile image) {
    }
}
