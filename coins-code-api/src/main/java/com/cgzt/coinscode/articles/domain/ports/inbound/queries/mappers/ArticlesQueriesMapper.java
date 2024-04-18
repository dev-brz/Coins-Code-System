package com.cgzt.coinscode.articles.domain.ports.inbound.queries.mappers;

import com.cgzt.coinscode.articles.domain.models.Article;
import com.cgzt.coinscode.articles.domain.ports.inbound.queries.models.ArticleOverviewResult;
import com.cgzt.coinscode.articles.domain.ports.inbound.queries.models.ArticleResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ArticlesQueriesMapper {
    ArticleResult toArticleResult(Article article);

    ArticleOverviewResult toArticleOverviewResult(Article article);
}
