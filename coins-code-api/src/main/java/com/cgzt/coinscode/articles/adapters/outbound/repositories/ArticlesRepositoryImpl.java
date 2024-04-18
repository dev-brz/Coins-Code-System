package com.cgzt.coinscode.articles.adapters.outbound.repositories;

import com.cgzt.coinscode.articles.adapters.outbound.entities.ArticleEntity;
import com.cgzt.coinscode.articles.adapters.outbound.mappers.ArticlesEntityMapper;
import com.cgzt.coinscode.articles.domain.models.Article;
import com.cgzt.coinscode.articles.domain.ports.outbound.repositories.ArticlesRepository;
import com.cgzt.coinscode.articles.domain.ports.outbound.repositories.models.ArticleImageUpdate;
import com.cgzt.coinscode.articles.domain.ports.outbound.repositories.models.ArticleUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Repository
@RequiredArgsConstructor
class ArticlesRepositoryImpl implements ArticlesRepository {
    private final JpaArticlesRepository jpaArticlesRepository;
    private final ArticlesEntityMapper mapper;

    @Override
    public Page<Article> findPage(Pageable pageable) {
        return jpaArticlesRepository.findAll(pageable).map(mapper::toArticle);
    }

    @Override
    public Article findByUid(String articleUid) {
        return jpaArticlesRepository.findByUid(articleUid).map(mapper::toArticle)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public String save(Article article) {
        ArticleEntity entity = mapper.toEntity(article);
        return jpaArticlesRepository.save(entity).getUid();
    }

    @Override
    @Transactional
    public void update(String articleUid, ArticleUpdate articleUpdate) {
        ArticleEntity targetArticle = jpaArticlesRepository.findByUid(articleUid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ArticleEntity entity = mapper.toEntity(articleUpdate, targetArticle);
        jpaArticlesRepository.save(entity);
    }

    @Override
    @Transactional
    public void updateImage(String articleUid, ArticleImageUpdate articleImageUpdate) {
        ArticleEntity targetArticle = jpaArticlesRepository.findByUid(articleUid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ArticleEntity entity = mapper.toEntity(articleImageUpdate, targetArticle);
        jpaArticlesRepository.save(entity);
    }

    @Override
    public void delete(String articleUid) {
        jpaArticlesRepository.deleteByUid(articleUid);
    }
}
