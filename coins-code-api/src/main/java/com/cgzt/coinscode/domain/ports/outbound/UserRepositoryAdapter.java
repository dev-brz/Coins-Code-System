package com.cgzt.coinscode.domain.ports.outbound;

import com.cgzt.coinscode.domain.ports.data.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryAdapter {
    Optional<UserDto> findByUsername(String username);

    void deleteByUsername(String username);

    List<UserDto> getAll();

    void save(UserDto user);

    void updateUserImageUrl(String username, String hash);


    boolean existsByUsername(String username);
}
