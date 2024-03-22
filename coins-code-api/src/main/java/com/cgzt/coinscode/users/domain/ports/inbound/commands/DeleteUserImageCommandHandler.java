package com.cgzt.coinscode.users.domain.ports.inbound.commands;

import com.cgzt.coinscode.shared.domain.ports.outbound.services.ImageService;
import com.cgzt.coinscode.users.domain.models.User;
import com.cgzt.coinscode.users.domain.ports.outbound.repository.CurrentUserRepository;
import com.cgzt.coinscode.users.domain.ports.outbound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteUserImageCommandHandler {
    private final CurrentUserRepository currentUserRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;
    @Value("${user.profile.dir}")
    private String imageDir;

    @Transactional
    public void handle() {
        User currentUser = currentUserRepository.get();

        if (StringUtils.isNotBlank(currentUser.getImageName())) {
            imageService.remove(currentUser.getImageName(), imageDir);
            userRepository.updateImageName(currentUser.getUsername(), null);
        }
    }
}
