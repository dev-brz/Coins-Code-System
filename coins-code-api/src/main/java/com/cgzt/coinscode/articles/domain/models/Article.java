package com.cgzt.coinscode.articles.domain.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Article {
    private String uid;
    private String title;
    private String description;
    private String content;
    private LocalDateTime createdAt;
    private byte[] image;
}
