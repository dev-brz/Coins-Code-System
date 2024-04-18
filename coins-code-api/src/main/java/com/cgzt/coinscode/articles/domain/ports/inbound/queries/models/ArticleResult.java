package com.cgzt.coinscode.articles.domain.ports.inbound.queries.models;

import java.time.LocalDateTime;

public record ArticleResult(String uid,
                            String title,
                            String description,
                            String content,
                            LocalDateTime createdAt,
                            byte[] image) {
}
