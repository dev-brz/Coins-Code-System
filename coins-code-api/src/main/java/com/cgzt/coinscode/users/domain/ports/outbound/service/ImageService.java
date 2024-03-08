package com.cgzt.coinscode.users.domain.ports.outbound.service;

import com.cgzt.coinscode.users.domain.models.UserImage;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    UserImage upload(String username, MultipartFile image);

    Resource load(String imageName);
}
