package com.cgzt.coinscode.coins.adapters.inbound;

import com.cgzt.coinscode.coins.domain.ports.inbound.commands.*;
import com.cgzt.coinscode.coins.domain.ports.inbound.queries.GetCoinQueryHandler;
import com.cgzt.coinscode.coins.domain.ports.inbound.queries.GetCoinsQueryHandler;
import com.cgzt.coinscode.coins.domain.ports.inbound.queries.models.GetCoinResult;
import com.cgzt.coinscode.coins.domain.ports.inbound.queries.models.GetCoinsResult;
import com.cgzt.coinscode.shared.domain.ports.outbound.services.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(CoinsController.COINS)
@RequiredArgsConstructor
@Tag(name = "Coins Controller", description = "Coins API")
class CoinsController {
    public static final String COINS = "/coins";
    public static final String UID = "/{uid}";
    public static final String UID_IMAGE = "/{uid}/image";
    public static final String NAME_IMAGE = "/{name}/image";

    private final GetCoinsQueryHandler getCoinsQueryHandler;
    private final GetCoinQueryHandler getCoinQueryHandler;
    private final CreateCoinCommandHandler createCoinCommandHandler;
    private final UpdateCoinCommandHandler updateCoinCommandHandler;
    private final UpdateCoinImageCommandHandler updateCoinImageCommandHandler;
    private final DeleteCoinCommandHandler deleteCoinCommandHandler;
    private final ExistsCoinQueryHandler existsCoinQueryHandler;
    private final ImageService imageService;

    @Value("${coin.image.dir}")
    private String imageDir;

    @GetMapping
    @PreAuthorize(/*@formatter:off*/"""                                                                                  
        hasAnyRole('ADMIN','EMPLOYEE')
        or #username == authentication.name
    """/*@formatter:on*/)
    @Operation(summary = "Get all coins", description = "Get all coins for a specific user or all users")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    GetCoinsResult getCoins(@RequestParam(required = false) final String username,
                            @RequestParam(required = false) final String uid,
                            @RequestParam(required = false) final String name) {
        return getCoinsQueryHandler.handle(new GetCoinsQueryHandler.Query(uid, username, name));
    }

    @GetMapping(UID)
    @PostAuthorize(/*@formatter:off*/"""
        hasAnyRole('ADMIN', 'EMPLOYEE')
        or returnObject.username == authentication.name
    """/*@formatter:on*/)
    @Operation(summary = "Get a coin", description = "Get a specific coin by its uid")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "404", description = "Coin not found", content = @Content)
    GetCoinResult getCoin(@PathVariable final String uid) {
        return getCoinQueryHandler.handle(uid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(/*@formatter:off*/"""
        hasAnyRole('ADMIN','EMPLOYEE')
        or #command.username == authentication.name
    """/*@formatter:on*/)
    @Operation(summary = "Create a coin", description = "Create a new coin")
    @ApiResponse(responseCode = "201", description = "Coin created")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    void createCoin(@Valid @RequestBody final CreateCoinCommandHandler.Command command) {
        createCoinCommandHandler.handle(command);
    }

    @PatchMapping
    @PreAuthorize(/*@formatter:off*/"""
        hasAnyRole('ADMIN','EMPLOYEE')
        or #command.username == authentication.name
    """/*@formatter:on*/)
    @Operation(summary = "Update a coin", description = "Update an existing coin")
    @ApiResponse(responseCode = "200", description = "Coin updated")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    void updateCoin(@Valid @RequestBody final UpdateCoinCommandHandler.Command command) {
        updateCoinCommandHandler.handle(command);
    }

    @DeleteMapping(UID)
    @PreAuthorize(/*@formatter:off*/"""
        hasAnyRole('ADMIN', 'EMPLOYEE')
        or @getCoinQueryHandler.handle(#uid).username == authentication.name
    """/*@formatter:on*/)
    @Operation(summary = "Delete a coin", description = "Delete a coin by its uid")
    @ApiResponse(responseCode = "200", description = "Coin deleted")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Coin not found")
    void deleteCoin(@PathVariable final String uid) {
        deleteCoinCommandHandler.handle(uid);
    }

    @RequestMapping(method = RequestMethod.HEAD)
    @Operation(summary = "Check if a coin exists", description = "Check if a coin exists by its name and username")
    @ApiResponse(responseCode = "200", description = "Coin exists")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Coin not found")
    void existsCoin(@RequestParam final String name, @RequestParam final String username) {
        boolean exists = existsCoinQueryHandler.handle(new ExistsCoinQueryHandler.Query(name, username));

        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coin not found");
        }
    }

    @PostMapping(value = UID_IMAGE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update a coin's image", description = "Update the image of a coin by its uid")
    @ApiResponse(responseCode = "200", description = "Image updated")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    void updateCoinImage(@PathVariable final String uid, @RequestPart final MultipartFile image) {
        updateCoinImageCommandHandler.handle(new UpdateCoinImageCommandHandler.Command(uid, image));
    }

    @GetMapping(NAME_IMAGE)
    @Operation(summary = "Get a coin's image", description = "Get the image of a coin by its name")
    @ApiResponse(responseCode = "200", description = "Image fetched")
    @ApiResponse(responseCode = "400", description = "Coin not found by uid testUid", content = @Content)
    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    Resource getCoinImage(@PathVariable final String name) {
        return imageService.load(name, imageDir);
    }
}
