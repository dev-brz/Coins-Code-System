package com.cgzt.coinscode.articles.domain.ports.inbound.commands.mappers;

import com.cgzt.coinscode.articles.domain.models.Article;
import com.cgzt.coinscode.articles.domain.ports.inbound.commands.SaveArticleCommandHandler;
import com.cgzt.coinscode.articles.domain.ports.inbound.commands.UpdateArticleCommandHandler;
import com.cgzt.coinscode.articles.domain.ports.inbound.commands.UpdateArticleImageCommandHandler;
import com.cgzt.coinscode.articles.domain.ports.outbound.repositories.models.ArticleImageUpdate;
import com.cgzt.coinscode.articles.domain.ports.outbound.repositories.models.ArticleUpdate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Mapper(componentModel = "spring")
public interface ArticlesCommandsMapper {
    @Mapping(target = "image", expression = "java(toBytes(command.image()))")
    Article toArticle(SaveArticleCommandHandler.Command command);

    ArticleUpdate toArticleUpdate(UpdateArticleCommandHandler.Command command);

    @Mapping(target = "image", expression = "java(toBytes(command.image()))")
    ArticleImageUpdate toArticleImageUpdate(UpdateArticleImageCommandHandler.Command command);

    default byte[] toBytes(MultipartFile multipartFile) {
        if (multipartFile != null && !multipartFile.isEmpty()) {
            try {
                return multipartFile.getBytes();
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }
}
