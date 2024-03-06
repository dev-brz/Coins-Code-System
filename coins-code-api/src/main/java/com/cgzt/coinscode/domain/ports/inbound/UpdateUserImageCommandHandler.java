package com.cgzt.coinscode.domain.ports.inbound;

import com.cgzt.coinscode.domain.ports.outbound.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UpdateUserImageCommandHandler implements CommandHandler<MultipartFile, Boolean> {
    private final UserService userService;

    @Override
    public Boolean handle(MultipartFile image) {
        return userService.updateUserImage(image);
    }
}
