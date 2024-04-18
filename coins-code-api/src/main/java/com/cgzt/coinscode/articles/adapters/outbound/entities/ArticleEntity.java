package com.cgzt.coinscode.articles.adapters.outbound.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
@Getter
@Setter
// TODO - add support for article i18n as a part of [#89]
public class ArticleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @UuidGenerator
    @Column(nullable = false, unique = true)
    private String uid;
    @Version
    private int version;
    @Column(nullable = false)
    private String title;
    private String description;
    @Column(nullable = false)
    private String content;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private ArticleImageEntity image;
}
