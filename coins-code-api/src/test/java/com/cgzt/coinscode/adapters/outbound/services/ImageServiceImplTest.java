package com.cgzt.coinscode.adapters.outbound.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        imageService.imageDir = imageDir;
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

        String actualName = imageService.store(name, mockImage);


        assertEquals(expectedName, actualName);
        verify(mockImage).transferTo(any(File.class));
    }

    @Test
    void testGetImage() throws IOException {
        String name = "myImage";

        ClassPathResource mockResource = mock(ClassPathResource.class);
        when(mockResource.getInputStream()).thenReturn(mock(InputStream.class));
        imageService.store(name, mockImage);

        var resource = imageService.load(name);

        assertNotNull(resource);
    }

    @Test
    void testImageExt() {
        String actual = imageService.getImageExt("hello.txt.pdf");
        assertEquals(".pdf", actual);
    }
}
