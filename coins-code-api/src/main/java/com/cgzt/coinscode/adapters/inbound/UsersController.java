package com.cgzt.coinscode.adapters.inbound;

import com.cgzt.coinscode.domain.ports.data.UserDto;
import com.cgzt.coinscode.domain.ports.inbound.*;
import com.cgzt.coinscode.domain.ports.outbound.ImageService;
import com.cgzt.coinscode.domain.ports.outbound.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
class UsersController {
    public static final String LOGIN_URL = "/login";
    public static final String LOGOUT_URL = "/logout";
    public static final String USERNAME = "/{username}";
    public static final String PASSWORD = "/password";
    public static final String IMAGE = "image";

    private final UserLoginCommandHandler userLoginCommandHandler;
    private final CreateUserCommandHandler createUserCommandHandler;
    private final UpdateUserCommandHandler updateUserCommandHandler;
    private final UpdateUserImageCommandHandler updateUserImageCommandHandler;
    private final UpdateUserPasswordCommandHandler updateUserPasswordCommandHandler;
    private final UserService userService;
    private final ImageService imageService;

    @PostMapping(LOGIN_URL)
    void login(@RequestBody UserLoginCommandHandler.Command command) {
        userLoginCommandHandler.handle(command);
    }

    @PostMapping(LOGOUT_URL)
    void logout() {

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
        userService.deleteUser(username);
    }

    @GetMapping
    List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping(USERNAME)
    UserDto getUser(@PathVariable String username) {
        return userService.getUser(username);
    }

    @RequestMapping(value = USERNAME, method = RequestMethod.HEAD)
    void isUserExisting(@PathVariable String username) {
        if ( ! userService.isUserExisting(username) ) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(IMAGE)
    void uploadUserImage(@RequestParam MultipartFile image) {
        updateUserImageCommandHandler.handle(image);
    }

    @GetMapping(IMAGE)
    @ResponseBody
    Resource getUserImage(@RequestParam String name) throws MalformedURLException {
        return imageService.load(name);
    }

}
