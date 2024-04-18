package com.cgzt.coinscode.articles.domain.ports.inbound.commands;

import com.cgzt.coinscode.articles.domain.ports.inbound.commands.mappers.ArticlesCommandsMapper;
import com.cgzt.coinscode.articles.domain.ports.outbound.repositories.ArticlesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UpdateArticleImageCommandHandler {
    private final ArticlesRepository articlesRepository;
    private final ArticlesCommandsMapper mapper;

    public void handle(Command command) {
        articlesRepository.updateImage(command.uid, mapper.toArticleImageUpdate(command));
    }

    public record Command(String uid, MultipartFile image, int version) {
    }
}
