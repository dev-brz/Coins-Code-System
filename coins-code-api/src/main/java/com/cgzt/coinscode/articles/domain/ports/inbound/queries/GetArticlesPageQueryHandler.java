package com.cgzt.coinscode.articles.domain.ports.inbound.queries;

import com.cgzt.coinscode.articles.domain.ports.inbound.queries.mappers.ArticlesQueriesMapper;
import com.cgzt.coinscode.articles.domain.ports.inbound.queries.models.ArticleOverviewResult;
import com.cgzt.coinscode.articles.domain.ports.outbound.repositories.ArticlesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetArticlesPageQueryHandler {
    private final ArticlesRepository articlesRepository;
    private final ArticlesQueriesMapper mapper;

    public Page<ArticleOverviewResult> handle(Query query) {
        return articlesRepository.findPage(query.pageable).map(mapper::toArticleOverviewResult);
    }

    public record Query(Pageable pageable) {
    }
}
