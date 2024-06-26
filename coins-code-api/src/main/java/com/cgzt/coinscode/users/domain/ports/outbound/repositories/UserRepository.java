package com.cgzt.coinscode.users.domain.ports.outbound.repositories;

import com.cgzt.coinscode.users.domain.models.User;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.models.UserUpdate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    void save(User user, char[] password);

    void update(UserUpdate user);

    void updateImageName(String username, String imageName);

    void deleteByUsername(String username);

    Optional<Long> findIdByUsername(String username);

    void resetLimits();

    void removeSendLimits(String username, BigDecimal amount);

    void incrementNumberOfSends(String username);

    void incrementNumberOfReceives(String username);

}
