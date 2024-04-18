package com.cgzt.coinscode.articles.adapters.inbound.models;

import com.cgzt.coinscode.articles.domain.ports.inbound.commands.UpdateArticleImageCommandHandler;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UpdateArticleImageForm(MultipartFile image, @NotNull Integer version) {
    public UpdateArticleImageCommandHandler.Command toCommand(String uid) {
        return new UpdateArticleImageCommandHandler.Command(uid, image, version);
    }
}
