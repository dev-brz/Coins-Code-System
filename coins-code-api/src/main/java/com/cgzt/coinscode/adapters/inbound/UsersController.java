package com.cgzt.coinscode.adapters.inbound;

import com.cgzt.coinscode.domain.ports.inbound.UserLoginCommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class UsersController {
    public static final String LOGIN_URL = "/login";
    private final UserLoginCommandHandler userLoginCommandHandler;

    @PostMapping(LOGIN_URL)
    void login(@RequestBody UserLoginCommandHandler.Command command) {
        userLoginCommandHandler.handle(command);
    }
}
