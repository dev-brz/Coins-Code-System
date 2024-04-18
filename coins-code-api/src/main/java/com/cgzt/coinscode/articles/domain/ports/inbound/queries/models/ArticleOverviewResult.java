package com.cgzt.coinscode.articles.domain.ports.inbound.queries.models;

import java.time.LocalDateTime;

public record ArticleOverviewResult(String uid,
                                    String title,
                                    String description,
                                    LocalDateTime createdAt,
                                    byte[] image) {
}
