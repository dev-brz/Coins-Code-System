package com.cgzt.coinscode.articles.adapters.inbound;

import com.cgzt.coinscode.articles.adapters.inbound.models.UpdateArticleImageForm;
import com.cgzt.coinscode.articles.adapters.inbound.models.UpdateArticleRequestBody;
import com.cgzt.coinscode.articles.domain.ports.inbound.commands.DeleteArticleCommandHandler;
import com.cgzt.coinscode.articles.domain.ports.inbound.commands.SaveArticleCommandHandler;
import com.cgzt.coinscode.articles.domain.ports.inbound.commands.UpdateArticleCommandHandler;
import com.cgzt.coinscode.articles.domain.ports.inbound.commands.UpdateArticleImageCommandHandler;
import com.cgzt.coinscode.articles.domain.ports.inbound.queries.GetArticleQueryHandler;
import com.cgzt.coinscode.articles.domain.ports.inbound.queries.GetArticlesPageQueryHandler;
import com.cgzt.coinscode.articles.domain.ports.inbound.queries.models.ArticleOverviewResult;
import com.cgzt.coinscode.articles.domain.ports.inbound.queries.models.ArticleResult;
import com.cgzt.coinscode.core.utils.ResponseEntityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ArticlesController.ARTICLES)
@RequiredArgsConstructor
@Tag(name = "Articles Controller", description = "Articles API")
class ArticlesController {
    static final String ARTICLES = "/articles";
    static final String UID = "/{uid}";
    static final String IMAGE = "/image";

    private final GetArticlesPageQueryHandler getArticlesPageQueryHandler;
    private final GetArticleQueryHandler getArticleQueryHandler;
    private final SaveArticleCommandHandler saveArticleCommandHandler;
    private final UpdateArticleCommandHandler updateArticleCommandHandler;
    private final UpdateArticleImageCommandHandler updateArticleImageCommandHandler;
    private final DeleteArticleCommandHandler deleteArticleCommandHandler;

    @GetMapping
    @Operation(summary = "Get articles page")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    Page<ArticleOverviewResult> getArticlesPage(
            @ParameterObject @PageableDefault Pageable pageable) {
        return getArticlesPageQueryHandler.handle(new GetArticlesPageQueryHandler.Query(pageable));
    }

    @GetMapping(UID)
    @Operation(summary = "Get article")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    ArticleResult getArticle(@PathVariable String uid) {
        return getArticleQueryHandler.handle(new GetArticleQueryHandler.Query(uid));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @Operation(summary = "Save article")
    @ApiResponse(responseCode = "201", description = "Successfully created")
    @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    ResponseEntity<Void> saveArticle(@Valid SaveArticleCommandHandler.Command command) {
        return ResponseEntityUtils.created(saveArticleCommandHandler.handle(command).uid());
    }

    @PatchMapping(UID)
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update article")
    @ApiResponse(responseCode = "204", description = "Successfully updated")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    void updateArticle(@PathVariable String uid, @Valid @RequestBody UpdateArticleRequestBody body) {
        updateArticleCommandHandler.handle(body.toCommand(uid));
    }

    @PatchMapping(value = UID + IMAGE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @Operation(summary = "Update article image")
    @ApiResponse(responseCode = "204", description = "Successfully updated image")
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    void updateArticleImage(@PathVariable String uid, @Valid UpdateArticleImageForm form) {
        updateArticleImageCommandHandler.handle(form.toCommand(uid));
    }

    @DeleteMapping(UID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @Operation(summary = "Delete article")
    @ApiResponse(responseCode = "204", description = "Successfully deleted")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    void deleteArticle(@PathVariable String uid) {
        deleteArticleCommandHandler.handle(new DeleteArticleCommandHandler.Command(uid));
    }
}
