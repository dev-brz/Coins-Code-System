package com.cgzt.coinscode.articles.domain.ports.inbound.commands;

import com.cgzt.coinscode.articles.domain.ports.inbound.commands.mappers.ArticlesCommandsMapper;
import com.cgzt.coinscode.articles.domain.ports.outbound.repositories.ArticlesRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class SaveArticleCommandHandler {
    private final ArticlesRepository articlesRepository;
    private final ArticlesCommandsMapper mapper;

    public Result handle(Command command) {
        String articleUid = articlesRepository.save(mapper.toArticle(command));

        return new Result(articleUid);
    }

    public record Command(@NotBlank @Size(max = 128) String title,
                          @Size(max = 255) String description,
                          @NotBlank @Size(max = 4096) String content,
                          MultipartFile image
    ) {
    }

    public record Result(String uid) {
    }
}
