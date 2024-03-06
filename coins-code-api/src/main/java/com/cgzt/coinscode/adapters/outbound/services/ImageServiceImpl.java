package com.cgzt.coinscode.adapters.outbound.services;

import com.cgzt.coinscode.domain.ports.exceptions.ProfileImageException;
import com.cgzt.coinscode.domain.ports.outbound.ImageService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Log4j2
@Service
public class ImageServiceImpl implements ImageService {

    @Value("${user.profile.dir}")
    protected String imageDir;

    @Override
    public String store(String username, MultipartFile image) {
        assert image != null && ! image.isEmpty();
        assert StringUtils.isNotBlank(username);

        try {
            var hash = hashOf(username);
            var imageExt = getImageExt(image.getOriginalFilename());
            var imagePath = getImagePath(hash, imageExt);
            var dest = new File(imagePath);

            dest.createNewFile();

            image.transferTo(dest);

            return hash + imageExt;
        } catch ( IOException e ) {
            log.error("Could not save profile image", e);
            throw new ProfileImageException("Could not save profile image", e);
        }
    }

    @Override
    public Resource load(String imageName) throws MalformedURLException {
        var image = new File(getImagePath(imageName));
        return new UrlResource(image.toURI());
    }

    @Override
    public void delete(String name) {
        var imagePath = Path.of(getImagePath(name));
        FileSystemUtils.deleteRecursively(imagePath.toFile());
    }

    @Override
    public void deleteAll() {
        var rootLocation = Path.of(imageDir);
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    protected String getImagePath(String hash, String ext) {
        var folder = new File(imageDir);
        folder.mkdirs();
        return "%s/%s%s".formatted(imageDir, hash, ext);
    }

    protected String getImagePath(String name) {
        return "%s/%s".formatted(imageDir, name);
    }

    protected String hashOf(String value) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var hashBytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(hashBytes);
        } catch ( NoSuchAlgorithmException e ) {
            throw new RuntimeException(e);
        }
    }

    protected String getImageExt(String name) {
        var start = StringUtils.lastIndexOf(name, ".");
        return StringUtils.substring(name, start);
    }
}
