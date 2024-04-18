package com.cgzt.coinscode.core.config.batch;

import com.cgzt.coinscode.articles.adapters.outbound.batch.ArticleFromSource;
import com.cgzt.coinscode.articles.adapters.outbound.entities.ArticleEntity;
import com.cgzt.coinscode.articles.adapters.outbound.entities.ArticleImageEntity;
import com.cgzt.coinscode.articles.adapters.outbound.repositories.JpaArticlesRepository;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.web.client.RestClient;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

@Configuration(proxyBeanMethods = false)
class ArticlesBatchConfig {
    private final static String SAMPLE_ARTICLES_PATH = "data/sample-articles.json";
    private final static String SAMPLE_ARTICLE_IMAGES_PATH = "data/images/articles/%s";
    private final static String PICSUM_URL = "https://picsum.photos/400/250";
    private final static String RESOURCES_PATH = "src/main/resources";

    @Bean
    ItemReader<ArticleFromSource> articlesJsonItemReader() {
        return new JsonItemReaderBuilder<ArticleFromSource>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(ArticleFromSource.class))
                .resource(new ClassPathResource(SAMPLE_ARTICLES_PATH))
                .name("articlesJsonItemReader")
                .build();
    }

    @Bean
    ItemProcessor<ArticleFromSource, ArticleEntity> articlesItemProcessor(final RestClient restClient) {
        LocalDateTime now = LocalDateTime.now();

        return (item) -> {
            ArticleEntity entity = new ArticleEntity();
            entity.setTitle(item.title());
            entity.setDescription(item.description());
            entity.setContent(item.content());
            entity.setVersion(0);
            entity.setCreatedAt(now);

            prepareImage(restClient, SAMPLE_ARTICLE_IMAGES_PATH.formatted(item.imageName())).ifPresent(imageContent -> {
                ArticleImageEntity image = new ArticleImageEntity();
                image.setContent(imageContent);

                entity.setImage(image);
            });

            return entity;
        };
    }

    @Bean
    ItemWriter<ArticleEntity> articlesRepositoryItemWriter(final DataSource dataSource, final JpaArticlesRepository articlesRepository) {
        return new RepositoryItemWriterBuilder<ArticleEntity>()
                .repository(articlesRepository)
                .methodName("save")
                .build();
    }

    @Bean
    Step addSampleArticlesStep(final JobRepository jobRepository,
                               final JpaTransactionManager jpaTransactionManager,
                               final ItemReader<ArticleFromSource> articlesJsonItemReader,
                               final ItemProcessor<ArticleFromSource, ArticleEntity> articlesItemProcessor,
                               final ItemWriter<ArticleEntity> articlesRepositoryItemWriter) {
        return new StepBuilder("Create Article Step", jobRepository)
                .<ArticleFromSource, ArticleEntity>chunk(3, jpaTransactionManager)
                .reader(articlesJsonItemReader)
                .processor(articlesItemProcessor)
                .writer(articlesRepositoryItemWriter)
                .build();
    }

    private Optional<byte[]> prepareImage(RestClient restClient, String imagePath) {
        Resource resource = new ClassPathResource(imagePath);

        try {
            return resource.exists() ?
                    Optional.of(resource.getContentAsByteArray()) :
                    Optional.ofNullable(fetchRandomImage(restClient, imagePath));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private byte[] fetchRandomImage(RestClient restClient, String imagePath) throws IOException {
        URI location = restClient
                .get()
                .uri(PICSUM_URL)
                .retrieve()
                .toBodilessEntity()
                .getHeaders()
                .getLocation();

        if (location != null) {
            byte[] imageBytes = restClient
                    .get()
                    .uri(location)
                    .accept(MediaType.IMAGE_JPEG)
                    .retrieve()
                    .body(byte[].class);

            if (imageBytes != null) {
                Path path = Path.of(RESOURCES_PATH).resolve(imagePath);
                Path directory = path.getParent();

                if (!Files.exists(directory)) {
                    Files.createDirectories(directory);
                }

                Files.write(path, imageBytes);
                return imageBytes;
            }
        }
        return null;
    }
}
