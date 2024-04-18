package com.cgzt.coinscode.articles.domain.ports.inbound.queries;

import com.cgzt.coinscode.articles.domain.ports.inbound.queries.mappers.ArticlesQueriesMapper;
import com.cgzt.coinscode.articles.domain.ports.inbound.queries.models.ArticleResult;
import com.cgzt.coinscode.articles.domain.ports.outbound.repositories.ArticlesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetArticleQueryHandler {
    private final ArticlesRepository articlesRepository;
    private final ArticlesQueriesMapper mapper;

    public ArticleResult handle(Query query) {
        return mapper.toArticleResult(articlesRepository.findByUid(query.uid));
    }

    public record Query(String uid) {
    }
}
