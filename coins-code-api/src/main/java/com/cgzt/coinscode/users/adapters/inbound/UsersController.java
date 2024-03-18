package com.cgzt.coinscode.users.adapters.inbound;

import com.cgzt.coinscode.shared.domain.ports.outbound.services.ImageService;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.CreateUserCommandHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.DeleteUserCommandHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.UpdateUserCommandHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.UpdateUserImageCommandHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.UpdateUserPasswordCommandHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.UserLoginCommandHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.ExistsUserQueryHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.GetUserQueryHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.GetUsersQueryHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.model.GetUserResult;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.model.GetUsersResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static com.cgzt.coinscode.users.adapters.inbound.UsersController.USERS;

@Tag(name = "Users", description = "The Users API")
@RestController
@RequiredArgsConstructor
@RequestMapping(USERS)
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
    private final UpdateUserPasswordCommandHandler updateUserPasswordCommandHandler;
    private final DeleteUserCommandHandler deleteUserCommandHandler;
    private final GetUserQueryHandler getUserQueryHandler;
    private final GetUsersQueryHandler getUsersQueryHandler;
    private final ExistsUserQueryHandler existsUserQueryHandler;
    private final ImageService imageService;
    @Value("${user.profile.dir}")
    protected String imageDir;

    @Operation(summary = "Login a user", description = "Login a user by username and password",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @PostMapping(LOGIN)
    void login(@RequestBody UserLoginCommandHandler.Command command) {
        userLoginCommandHandler.handle(command);
    }

    @Operation(summary = "Register a new user", description = "Create a new user with the provided information",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created successfully"),
                    @ApiResponse(responseCode = "400", description = "Username, email or phone number is already taken")
            })
    @PostMapping
    void register(@Valid @RequestBody CreateUserCommandHandler.Command command) {
        createUserCommandHandler.handle(command);
    }

    @Operation(summary = "Update user password", description = "Update the password of a user",
            responses = {@ApiResponse(responseCode = "200", description = "Password updated successfully")})
    @PatchMapping(PASSWORD)
    void updatePassword(@Valid @RequestBody UpdateUserPasswordCommandHandler.Command command) {
        updateUserPasswordCommandHandler.handle(command);
    }

    @Operation(summary = "Update user information", description = "Update the information of a user",
            responses = {@ApiResponse(responseCode = "200", description = "User updated successfully")})
    @PatchMapping
    void update(@Valid @RequestBody UpdateUserCommandHandler.Command command) {
        updateUserCommandHandler.handle(command);
    }

    @Operation(summary = "Delete a user", description = "Delete a user by username",
            responses = {@ApiResponse(responseCode = "200", description = "User deleted successfully")})
    @DeleteMapping(USERNAME)
    void delete(@PathVariable String username) {
        deleteUserCommandHandler.handle(new DeleteUserCommandHandler.Command(username));
    }


    @GetMapping
    @Operation(summary = "Get all users", description = "Get a list of all users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(not = @Schema())),
            })
    GetUsersResult getUsers() {
        return getUsersQueryHandler.handle();
    }

    @Operation(summary = "Get a user", description = "Get a user by username",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(not = @Schema()))})
    @GetMapping(USERNAME)
    GetUserResult getUser(@PathVariable String username) {
        return getUserQueryHandler.handle(new GetUserQueryHandler.Query(username))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Check if a user exists", description = "Check if a user exists by username",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User exists"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    @RequestMapping(value = USERNAME, method = RequestMethod.HEAD)
    void isUserExisting(@PathVariable String username) {
        ExistsUserQueryHandler.Result result = existsUserQueryHandler
                .handle(new ExistsUserQueryHandler.Query(username));

        if (!result.userExists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(IMAGE)
    @Operation(summary = "Upload user image", description = "Upload an image for a user",
            responses = {@ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    void uploadUserImage(@RequestParam MultipartFile image) {
        updateUserImageCommandHandler.handle(new UpdateUserImageCommandHandler.Command(image));
    }

    @GetMapping(IMAGE)
    @ResponseBody
    @Operation(summary = "Get user image", description = "Get the image of a user by image name",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Image does not exist", content = @Content(not = @Schema()))
            })
    Resource getUserImage(@RequestParam String imageName) {
        return imageService.load(imageName, imageDir);
    }

}
