package com.cgzt.coinscode.articles.domain.ports.outbound.repositories;

import com.cgzt.coinscode.articles.domain.models.Article;
import com.cgzt.coinscode.articles.domain.ports.outbound.repositories.models.ArticleImageUpdate;
import com.cgzt.coinscode.articles.domain.ports.outbound.repositories.models.ArticleUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticlesRepository {
    Page<Article> findPage(Pageable pageable);

    Article findByUid(String articleUid);

    String save(Article article);

    void update(String articleUid, ArticleUpdate articleUpdate);

    void updateImage(String articleUid, ArticleImageUpdate articleImageUpdate);

    void delete(String articleUid);
}
