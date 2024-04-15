package com.cgzt.coinscode.coins.adapters.outbound.batch;

import com.cgzt.coinscode.coins.adapters.outbound.entities.CoinEntity;
import com.cgzt.coinscode.shared.domain.ports.outbound.services.ImageService;
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
class CoinsWriteListener implements ItemWriteListener<CoinEntity> {
    public static final String UPDATE_IMAGE_NAME_SQL = "UPDATE coins SET image_name = ? WHERE uid = ?";

    private final ImageService imageService;
    private final JdbcTemplate jdbcTemplate;

    @Value("${coin.image.dir}")
    private String imageDir;

    @Override
    public void afterWrite(Chunk<? extends CoinEntity> items) {
        items.forEach(this::uploadImage);
    }

    protected void uploadImage(CoinEntity coin) {
        if (StringUtils.isNotBlank(coin.getImageName())) {
            var image = imageService.upload(coin.getUid(), new ClassPathResource(coin.getImageName()), imageDir);
            jdbcTemplate.update(UPDATE_IMAGE_NAME_SQL, image.name(), coin.getUid());
        }
    }
}
