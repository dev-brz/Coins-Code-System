package com.cgzt.coinscode.articles.adapters.outbound.repositories;

import com.cgzt.coinscode.articles.adapters.outbound.entities.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface JpaArticlesRepository extends JpaRepository<ArticleEntity, Long> {
    Optional<ArticleEntity> findByUid(String uid);

    @Transactional
    @Modifying
    void deleteByUid(String uid);
}
