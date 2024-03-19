package com.cgzt.coinscode.coins.domain.ports.outbound.repository;

import com.cgzt.coinscode.coins.domain.models.Coin;

import java.math.BigDecimal;
import java.util.List;

public interface CoinsRepository {
    List<Coin> findAll();

    List<Coin> findAllByUsername(String username);

    Coin findByUid(String uid);

    void save(Coin coin);

    void update(Coin coin);

    void delete(String uid);

    boolean exists(String name, String username);

    Coin findByUsernameAndName(String name, String username);

    void updateImageName(String uid, String imageName);

    boolean exists(String uid);

    void validate(String uid);

    void validate(String uid, String username);

    void add(String uid, BigDecimal amount);

    Long findIdByUid(String uid);
}
