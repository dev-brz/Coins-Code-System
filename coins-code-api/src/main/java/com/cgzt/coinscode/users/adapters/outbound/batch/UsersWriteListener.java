package com.cgzt.coinscode.users.adapters.outbound.batch;

import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccount;
import com.cgzt.coinscode.users.adapters.outbound.repositories.SpringSecurityUserRepository;
import com.cgzt.coinscode.users.domain.ports.outbound.service.ImageService;
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
public class UsersWriteListener implements ItemWriteListener<UserAccount>{
    public static final String UPDATE_IMAGE_NAME_SQL = "UPDATE user_account SET image_name = ? WHERE username = ?";
    private final ImageService imageService;
    private final SpringSecurityUserRepository springSecurityUserRepository;
    private final JdbcTemplate jdbcTemplate;

    @Value("${user.default.password}")
    protected char[] defaultPassword;

    @Override
    public void beforeWrite(Chunk<? extends UserAccount> items){
        items.forEach(user -> springSecurityUserRepository.createUserAccount(user.getUsername(), defaultPassword));
    }

    @Override
    public void afterWrite(Chunk<? extends UserAccount> items){
        items.forEach(this::uploadImage);
    }

    protected void uploadImage(UserAccount user){
        var imageName = user.getImageName();

        if(StringUtils.isNotBlank(imageName)){
            var resource = new ClassPathResource(imageName);
            var username = user.getUsername();

            var image = imageService.upload(username, resource);
            user.setImageName(image.name());

            jdbcTemplate.update(UPDATE_IMAGE_NAME_SQL, image.name(), username);
        }

    }
}
