package com.cgzt.coinscode.users.adapters.outbound.services;

import com.cgzt.coinscode.core.exceptions.ProfileImageException;
import com.cgzt.coinscode.users.domain.models.UserImage;
import com.cgzt.coinscode.users.domain.ports.outbound.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
@Service
class ImageServiceImpl implements ImageService {
    @Value("${user.profile.dir}")
    String imageDir;

    @Override
    public UserImage upload(String username, MultipartFile image) {
        assert image != null && !image.isEmpty();
        assert StringUtils.isNotBlank(username);

        try {
            var hash = hashOf(username);
            var imageExt = getImageExt(image.getOriginalFilename());
            var imagePath = getImagePath(hash, imageExt);
            var dest = new File(imagePath);

            dest.createNewFile();

            image.transferTo(dest);

            return new UserImage(hash + imageExt);
        } catch (IOException e) {
            log.error("Could not save profile image", e);
            throw new ProfileImageException("Could not save profile image", e);
        }
    }

    @Override
    public Resource load(String imageName) {
        var image = new File(getImagePath(imageName));
        try {
            return new UrlResource(image.toURI());
        } catch (MalformedURLException e) {
            throw new ProfileImageException("Could not load profile image", e);
        }
    }

    String hashOf(String value) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var hashBytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    String getImageExt(String name) {
        var start = StringUtils.lastIndexOf(name, ".");
        return StringUtils.substring(name, start);
    }

    private String getImagePath(String hash, String ext) {
        var folder = new File(imageDir);
        folder.mkdirs();
        return "%s/%s%s".formatted(imageDir, hash, ext);
    }

    private String getImagePath(String name) {
        return "%s/%s".formatted(imageDir, name);
    }
}