package com.cgzt.coinscode.users.domain.ports.outbound.repositories;

import com.cgzt.coinscode.users.domain.models.User;

public interface CurrentUserRepository {
    User get();

    void changePassword(char[] oldPassword, char[] newPassword);
}
