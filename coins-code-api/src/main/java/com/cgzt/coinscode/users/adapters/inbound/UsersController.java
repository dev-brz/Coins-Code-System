package com.cgzt.coinscode.users.adapters.inbound;

import com.cgzt.coinscode.shared.domain.ports.outbound.services.ImageService;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.*;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.ExistsUserQueryHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.GetUserQueryHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.GetUsersQueryHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.models.GetUserResult;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.models.GetUsersResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static com.cgzt.coinscode.users.adapters.inbound.UsersController.USERS;

@RestController
@RequestMapping(USERS)
@RequiredArgsConstructor
@Tag(name = "Users Controller", description = "Users API")
class UsersController {
    public static final String USERS = "/users";
    public static final String LOGIN = "/login";
    public static final String USERNAME = "/{username}";
    public static final String PASSWORD = "/password";
    public static final String IMAGE = "/image";

    private final UserLoginCommandHandler userLoginCommandHandler;
    private final CreateUserCommandHandler createUserCommandHandler;
    private final UpdateUserCommandHandler updateUserCommandHandler;
    private final UpdateUserImageCommandHandler updateUserImageCommandHandler;
    private final DeleteUserImageCommandHandler deleteUserImageCommandHandler;
    private final UpdateUserPasswordCommandHandler updateUserPasswordCommandHandler;
    private final DeleteUserCommandHandler deleteUserCommandHandler;
    private final GetUserQueryHandler getUserQueryHandler;
    private final GetUsersQueryHandler getUsersQueryHandler;
    private final ExistsUserQueryHandler existsUserQueryHandler;
    private final ImageService imageService;

    @Value("${user.profile.dir}")
    private String imageDir;

    @PostMapping(LOGIN)
    @SecurityRequirements
    @Operation(summary = "Login a user", description = "Login a user by username and password")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    void login(@RequestBody final UserLoginCommandHandler.Command command) {
        userLoginCommandHandler.handle(command);
    }

    @PostMapping
    @SecurityRequirements
    @Operation(summary = "Register a new user", description = "Create a new user with the provided information")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Username, email or phone number is already taken")
    void register(@Valid @RequestBody final CreateUserCommandHandler.Command command) {
        createUserCommandHandler.handle(command);
    }

    @PatchMapping(PASSWORD)
    @Operation(summary = "Update user password", description = "Update the password of a user")
    @ApiResponse(responseCode = "200", description = "Password updated successfully")
    void updatePassword(@Valid @RequestBody final UpdateUserPasswordCommandHandler.Command command) {
        updateUserPasswordCommandHandler.handle(command);
    }

    @PatchMapping
    @PreAuthorize(/*@formatter:off*/"""
        hasAnyRole('ADMIN','EMPLOYEE')
        or #command.username == authentication.name
    """/*@formatter:on*/)
    @Operation(summary = "Update user information", description = "Update the information of a user")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    void update(@Valid @RequestBody final UpdateUserCommandHandler.Command command) {
        updateUserCommandHandler.handle(command);
    }

    @DeleteMapping(USERNAME)
    @PreAuthorize(/*@formatter:off*/"""
        hasAnyRole('ADMIN','EMPLOYEE')
        or #username == authentication.name
    """/*@formatter:on*/)
    @Operation(summary = "Delete a user", description = "Delete a user by username")
    @ApiResponse(responseCode = "200", description = "User deleted successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    void delete(@PathVariable final String username) {
        deleteUserCommandHandler.handle(new DeleteUserCommandHandler.Command(username));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    @Operation(summary = "Get all users", description = "Get a list of all users")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    GetUsersResult getUsers() {
        return getUsersQueryHandler.handle();
    }

    @GetMapping(USERNAME)
    @PreAuthorize(/*@formatter:off*/"""
        hasAnyRole('ADMIN','EMPLOYEE')
        or #username == authentication.name
    """/*@formatter:on*/)
    @Operation(summary = "Get a user", description = "Get a user by username")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    GetUserResult getUser(@PathVariable final String username) {
        return getUserQueryHandler.handle(new GetUserQueryHandler.Query(username))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(method = RequestMethod.HEAD)
    @SecurityRequirements
    @Operation(summary = "Check if a user exists", description = "Check if a user exists by either username, email or telephone number")
    @ApiResponse(responseCode = "200", description = "User exists")
    @ApiResponse(responseCode = "404", description = "User not found")
    void isUserExisting(@RequestParam(required = false) final String username,
                        @RequestParam(required = false) final String email,
                        @RequestParam(required = false) final String phoneNumber) {
        boolean userExists = existsUserQueryHandler
                .handle(new ExistsUserQueryHandler.Query(username, email, phoneNumber));

        if (!userExists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = IMAGE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload user image", description = "Upload an image for a user")
    @ApiResponse(responseCode = "200", description = "Image uploaded successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    void uploadUserImage(@RequestParam final MultipartFile image) {
        updateUserImageCommandHandler.handle(new UpdateUserImageCommandHandler.Command(image));
    }

    @GetMapping(IMAGE)
    @ResponseBody
    @Operation(summary = "Get user image", description = "Get the image of a user by image name")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @ApiResponse(responseCode = "400", description = "Image does not exist", content = @Content)
    Resource getUserImage(@RequestParam final String imageName) {
        return imageService.load(imageName, imageDir);
    }


    @DeleteMapping(IMAGE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete current user image")
    @ApiResponse(responseCode = "204", description = "Image deleted successfully")
    void deleteUserImage() {
        deleteUserImageCommandHandler.handle();
    }
}
