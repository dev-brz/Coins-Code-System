package com.cgzt.coinscode.users.domain.ports.outbound.repository;

import com.cgzt.coinscode.users.domain.models.User;

public interface CurrentUserRepository {
    User get();

    void changePassword(char[] oldPassword, char[] newPassword);
}
