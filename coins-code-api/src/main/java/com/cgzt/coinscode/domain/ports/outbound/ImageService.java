package com.cgzt.coinscode.domain.ports.outbound;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

public interface ImageService {

    String store(String username, MultipartFile image);

    Resource load(String imageName) throws MalformedURLException;

    void delete(String name);

    void deleteAll();
}
