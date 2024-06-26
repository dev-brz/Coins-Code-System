package com.cgzt.coinscode.users.adapters.outbound.batch;

import com.cgzt.coinscode.shared.domain.ports.outbound.services.ImageService;
import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccountEntity;
import com.cgzt.coinscode.users.domain.models.UserImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import static com.cgzt.coinscode.users.adapters.outbound.batch.UsersWriteListener.UPDATE_IMAGE_NAME_SQL;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersWriteListenerTest {
    @Mock
    JdbcTemplate template;
    @Mock
    ImageService imageService;
    @InjectMocks
    UsersWriteListener usersWriteListener;

    UserAccountEntity user;

    @BeforeEach
    void setUp() {
        user = new UserAccountEntity();
        user.setUsername("test");
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setPhoneNumber("1234567890");
        user.setImageName("profile.png");
    }

    @Test
    void testUploadImage() {
        var username = user.getUsername();
        var image = new UserImage("test.png");
        when(imageService.upload(username, new ClassPathResource(user.getImageName()), null)).thenReturn(image);
        when(template.update(UPDATE_IMAGE_NAME_SQL, image.name(), username)).thenReturn(1);

        usersWriteListener.uploadImage(user);

        verify(template).update(UPDATE_IMAGE_NAME_SQL, image.name(), username);
    }

    @Test
    void testUploadNullImage() {
        user.setImageName(null);
        usersWriteListener.uploadImage(user);

        verifyNoInteractions(imageService);
        verifyNoInteractions(template);
    }
}