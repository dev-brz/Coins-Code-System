package com.cgzt.coinscode.articles.adapters.inbound.models;

import com.cgzt.coinscode.articles.domain.ports.inbound.commands.UpdateArticleCommandHandler;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateArticleRequestBody(
        @NotNull Integer version,
        @Size(max = 128) String title,
        @Size(max = 255) String description,
        @Size(max = 4096) String content) {
    public UpdateArticleCommandHandler.Command toCommand(String uid) {
        return new UpdateArticleCommandHandler.Command(uid, version, title, description, content);
    }
}
