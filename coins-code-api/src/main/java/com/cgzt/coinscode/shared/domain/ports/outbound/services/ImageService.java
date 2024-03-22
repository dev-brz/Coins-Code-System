package com.cgzt.coinscode.shared.domain.ports.outbound.services;

import com.cgzt.coinscode.users.domain.models.UserImage;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;

public interface ImageService {
    UserImage upload(String username, InputStreamSource image, String imageDir);

    Resource load(String imageName, String imageDir);

    void remove(String imageName, String imageDir);
}
