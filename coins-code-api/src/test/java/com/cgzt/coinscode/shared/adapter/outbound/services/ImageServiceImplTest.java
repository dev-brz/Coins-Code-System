package com.cgzt.coinscode.shared.adapter.outbound.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ImageServiceImplTest {
    AutoCloseable openedMocks;
    ImageServiceImpl imageService;
    String imageDir = "%s/coin-code/test/user/profile-images/".formatted(System.getProperty("root"));
    @Mock
    private MultipartFile mockImage;

    @BeforeEach
    void setUp() {
        openedMocks = MockitoAnnotations.openMocks(this);
        imageService = new ImageServiceImpl();
    }

    @AfterEach
    void clear() throws Exception {
        openedMocks.close();
    }

    @Test
    void testSaveImage() throws IOException {
        when(mockImage.getOriginalFilename()).thenReturn("profile.png");
        String name = "myImage";

        String expectedName = imageService.hashOf(name) + ".png";

        String actualName = imageService.upload(name, mockImage, imageDir).name();


        assertEquals(expectedName, actualName);
        verify(mockImage).transferTo(any(File.class));
    }

    @Test
    void testGetImage() {
        when(mockImage.getOriginalFilename()).thenReturn("myImage.png");
        imageService.upload("myImage", mockImage, imageDir);
        String hashedName = imageService.hashOf("myImage") + ".png";

        Resource resource = imageService.load(hashedName, imageDir);

        assertNotNull(resource);
    }

    @Test
    void testImageExt() {
        String actual = imageService.getImageExt("hello.txt.pdf");
        assertEquals(".pdf", actual);
    }

    @Test
    void testRemoveImage() throws IOException {
        Path file = Files.createTempFile("hash", ".png");

        imageService.remove(file.getFileName().toString(), file.getParent().toString());

        assertFalse(Files.exists(file));
    }
}
