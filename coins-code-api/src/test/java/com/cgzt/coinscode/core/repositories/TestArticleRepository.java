package com.cgzt.coinscode.core.repositories;

import com.cgzt.coinscode.articles.adapters.outbound.entities.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestArticleRepository extends JpaRepository<ArticleEntity, Long> {
}
