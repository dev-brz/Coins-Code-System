package com.cgzt.coinscode.adapters.inbound;

import com.cgzt.coinscode.domain.ports.inbound.UserLoginCommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
class UsersController {
    public static final String LOGIN_URL = "/login";
    public static final String LOGOUT_URL = "/logout";
    private final UserLoginCommandHandler userLoginCommandHandler;

    @PostMapping(LOGIN_URL)
    void login(@RequestBody UserLoginCommandHandler.Command command) {
        userLoginCommandHandler.handle(command);
    }

    @PostMapping(LOGOUT_URL)
    void logout(){

    }
}
