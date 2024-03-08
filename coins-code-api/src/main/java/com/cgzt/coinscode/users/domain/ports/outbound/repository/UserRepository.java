package com.cgzt.coinscode.users.domain.ports.outbound.repository;

import com.cgzt.coinscode.users.domain.models.User;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.model.UserUpdate;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    void save(User user, char[] password);

    void update(UserUpdate user);

    void updateImageName(String username, String imageName);

    void deleteByUsername(String username);
}
