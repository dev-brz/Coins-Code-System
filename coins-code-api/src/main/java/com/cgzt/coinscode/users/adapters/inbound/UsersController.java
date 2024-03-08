package com.cgzt.coinscode.users.adapters.inbound;

import com.cgzt.coinscode.users.domain.ports.inbound.commands.*;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.ExistsUserQueryHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.GetUserQueryHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.GetUsersQueryHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.model.GetUserResult;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.model.GetUsersResult;
import com.cgzt.coinscode.users.domain.ports.outbound.service.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static com.cgzt.coinscode.users.adapters.inbound.UsersController.USERS;

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

    @PostMapping(LOGIN)
    void login(@RequestBody UserLoginCommandHandler.Command command) {
        userLoginCommandHandler.handle(command);
    }

    @PostMapping
    void register(@Valid @RequestBody CreateUserCommandHandler.Command command) {
        createUserCommandHandler.handle(command);
    }

    @PatchMapping(PASSWORD)
    void updatePassword(@Valid @RequestBody UpdateUserPasswordCommandHandler.Command command) {
        updateUserPasswordCommandHandler.handle(command);
    }

    @PatchMapping
    void update(@Valid @RequestBody UpdateUserCommandHandler.Command command) {
        updateUserCommandHandler.handle(command);
    }

    @DeleteMapping(USERNAME)
    void delete(@PathVariable String username) {
        deleteUserCommandHandler.handle(new DeleteUserCommandHandler.Command(username));
    }

    @GetMapping
    GetUsersResult getUsers() {
        return getUsersQueryHandler.handle();
    }

    @GetMapping(USERNAME)
    GetUserResult getUser(@PathVariable String username) {
        return getUserQueryHandler.handle(new GetUserQueryHandler.Query(username))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @RequestMapping(value = USERNAME, method = RequestMethod.HEAD)
    void isUserExisting(@PathVariable String username) {
        ExistsUserQueryHandler.Result result = existsUserQueryHandler
                .handle(new ExistsUserQueryHandler.Query(username));

        if (!result.userExists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(IMAGE)
    void uploadUserImage(@RequestParam MultipartFile image) {
        updateUserImageCommandHandler.handle(new UpdateUserImageCommandHandler.Command(image));
    }

    @GetMapping(IMAGE)
    @ResponseBody
    Resource getUserImage(@RequestParam String imageName) {
        return imageService.load(imageName);
    }
}
