package com.cgzt.coinscode.articles.domain.ports.inbound.commands;

import com.cgzt.coinscode.articles.domain.ports.inbound.commands.mappers.ArticlesCommandsMapper;
import com.cgzt.coinscode.articles.domain.ports.outbound.repositories.ArticlesRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateArticleCommandHandler {
    private final ArticlesRepository articlesRepository;
    private final ArticlesCommandsMapper mapper;

    public void handle(Command command) {
        articlesRepository.update(command.uid, mapper.toArticleUpdate(command));
    }

    public record Command(@NotNull String uid,
                          @NotNull Integer version,
                          @Size(max = 128) String title,
                          @Size(max = 255) String description,
                          @Size(max = 4096) String content) {
    }
}
