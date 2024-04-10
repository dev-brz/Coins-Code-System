package com.cgzt.coinscode.users.domain.ports.outbound.repositories;

import com.cgzt.coinscode.users.domain.models.User;

import java.util.Optional;

public interface CurrentUserRepository {
    User get();

    Optional<Long> getId();

    void changePassword(char[] oldPassword, char[] newPassword);
}
