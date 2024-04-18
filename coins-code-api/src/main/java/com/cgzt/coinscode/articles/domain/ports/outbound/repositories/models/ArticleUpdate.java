package com.cgzt.coinscode.articles.domain.ports.outbound.repositories.models;

public record ArticleUpdate(String title,
                            String description,
                            String content,
                            int version) {
}
