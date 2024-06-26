package com.cgzt.coinscode.users.adapters.outbound.batch;

import com.cgzt.coinscode.shared.domain.ports.outbound.services.ImageService;
import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccountEntity;
import com.cgzt.coinscode.users.adapters.outbound.repositories.SpringSecurityUserRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UsersWriteListener implements ItemWriteListener<UserAccountEntity> {
    public static final String UPDATE_IMAGE_NAME_SQL = "UPDATE user_account SET image_name = ? WHERE username = ?";
    private final ImageService imageService;
    private final SpringSecurityUserRepository springSecurityUserRepository;
    private final JdbcTemplate jdbcTemplate;

    @Value("${user.profile.dir}")
    private String imageDir;
    @Value("${user.default.password}")
    private char[] defaultPassword;

    @Override
    public void beforeWrite(Chunk<? extends UserAccountEntity> items) {
        items.forEach(user -> springSecurityUserRepository.createUserAccount(user.getUsername(), defaultPassword));
    }

    @Override
    public void afterWrite(Chunk<? extends UserAccountEntity> items) {
        items.forEach(this::uploadImage);
    }

    protected void uploadImage(UserAccountEntity user) {
        var imageName = user.getImageName();

        if (StringUtils.isNotBlank(imageName)) {
            var resource = new ClassPathResource(imageName);
            var username = user.getUsername();

            var image = imageService.upload(username, resource, imageDir);
            user.setImageName(image.name());

            jdbcTemplate.update(UPDATE_IMAGE_NAME_SQL, image.name(), username);
        }

    }
}
