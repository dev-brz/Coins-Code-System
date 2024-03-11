package com.cgzt.coinscode.users.domain.ports.outbound.service;

import com.cgzt.coinscode.users.domain.models.UserImage;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;

public interface ImageService{
    UserImage upload(String username, InputStreamSource image);

    Resource load(String imageName);
}
