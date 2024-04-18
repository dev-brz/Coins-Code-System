package com.cgzt.coinscode.articles.adapters.outbound.mappers;

import com.cgzt.coinscode.articles.adapters.outbound.entities.ArticleEntity;
import com.cgzt.coinscode.articles.adapters.outbound.entities.ArticleImageEntity;
import com.cgzt.coinscode.articles.domain.models.Article;
import com.cgzt.coinscode.articles.domain.ports.outbound.repositories.models.ArticleImageUpdate;
import com.cgzt.coinscode.articles.domain.ports.outbound.repositories.models.ArticleUpdate;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArticlesEntityMapper {
    @Mapping(target = "image", source = "image.content")
    Article toArticle(ArticleEntity entity);

    @Mapping(target = "image", expression = "java(toImage(article.getImage()))")
    ArticleEntity toEntity(Article article);

    default ArticleEntity toEntity(ArticleUpdate update, ArticleEntity target) {
        ArticleEntity article = new ArticleEntity();
        article.setId(target.getId());
        article.setUid(target.getUid());
        article.setVersion(update.version());
        article.setTitle(StringUtils.firstNonBlank(update.title(), target.getTitle()));
        article.setDescription(StringUtils.firstNonBlank(update.description(), target.getDescription()));
        article.setContent(StringUtils.firstNonBlank(update.content(), target.getContent()));
        article.setCreatedAt(target.getCreatedAt());
        article.setImage(target.getImage());
        return article;
    }

    @Mapping(target = "image", expression = "java(toImage(update.image()))")
    @Mapping(target = "version", source = "update.version")
    ArticleEntity toEntity(ArticleImageUpdate update, ArticleEntity target);

    default ArticleImageEntity toImage(byte[] content) {
        if (content != null) {
            var entity = new ArticleImageEntity();
            entity.setContent(content);
            return entity;
        }
        return null;
    }
}
