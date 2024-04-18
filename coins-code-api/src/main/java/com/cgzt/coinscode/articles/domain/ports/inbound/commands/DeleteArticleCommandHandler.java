package com.cgzt.coinscode.articles.domain.ports.inbound.commands;

import com.cgzt.coinscode.articles.domain.ports.outbound.repositories.ArticlesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteArticleCommandHandler {
    private final ArticlesRepository articlesRepository;

    public void handle(Command command) {
        articlesRepository.delete(command.uid);
    }

    public record Command(String uid) {
    }
}
