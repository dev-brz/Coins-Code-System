package com.cgzt.coinscode.core.repositories;

import com.cgzt.coinscode.articles.adapters.outbound.entities.ArticleImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestArticleImageRepository extends JpaRepository<ArticleImageEntity, Long> {
}

