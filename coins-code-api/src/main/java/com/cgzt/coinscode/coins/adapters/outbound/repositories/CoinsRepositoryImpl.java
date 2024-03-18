package com.cgzt.coinscode.coins.adapters.outbound.repositories;

import com.cgzt.coinscode.coins.adapters.outbound.entities.CoinEntity;
import com.cgzt.coinscode.coins.adapters.outbound.mappers.CoinsMapper;
import com.cgzt.coinscode.coins.domain.models.Coin;
import com.cgzt.coinscode.coins.domain.ports.outbound.repository.CoinsRepository;
import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccount;
import com.cgzt.coinscode.users.domain.ports.outbound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
class CoinsRepositoryImpl implements CoinsRepository {
    private final CoinsJpaRepository coinsJpaRepository;
    private final UserRepository userRepository;
    private final CoinsMapper mapper;

    @Override
    public List<Coin> findAll() {
        return mapper.map(coinsJpaRepository.findAll());
    }

    @Override
    public List<Coin> findAllByUsername(String username) {
        return mapper.map(coinsJpaRepository.findAllByUserAccountUsername(username));
    }

    @Override
    public Coin findByUsernameAndName(String name, String username) {
        return coinsJpaRepository.findByNameAndUserAccountUsername(name, username)
                .map(mapper::map)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coin not found"));
    }

    @Override
    public Coin findByUid(String uid) {
        return coinsJpaRepository.findByUid(uid)
                .map(mapper::map)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coin not found"));
    }

    @Override
    public void updateImageName(String uid, String imageName) {
        coinsJpaRepository.findByUid(uid)
                .map(coinEntity -> {
                    coinEntity.setImageName(imageName);
                    return coinEntity;
                })
                .map(coinsJpaRepository::save)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coin not found by uid %s".formatted(uid)));
    }

    @Override
    public void save(Coin coin) {
        validateNameForUserCoins(coin);

        userRepository.findIdByUsername(coin.getUsername())
                .map(id -> populateCoinEntity(coin, id))
                .map(coinsJpaRepository::save)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found by username %s".formatted(coin.getUsername())));
    }

    @Override
    public void update(Coin coin) {
        validateNameForUserCoins(coin);

        coinsJpaRepository.findByUid(coin.getUid())
                .map(coinEntity -> {
                    coinEntity.setName(coin.getName());
                    coinEntity.setDescription(coin.getDescription());
                    return coinEntity;
                })
                .map(coinsJpaRepository::save)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coin not found by uid %s".formatted(coin.getUid())));
    }

    @Override
    public void delete(String uid) {
        coinsJpaRepository.deleteByUid(uid);
    }

    @Override
    public boolean exists(String name, String username) {
        return coinsJpaRepository.existsByNameAndUserAccountUsername(name, username);
    }

    @Override
    public boolean exists(String uid) {
        return coinsJpaRepository.existsByUid(uid);
    }

    @Override
    public void validateUid(String uid) {
        if (!exists(uid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coin not found by uid %s".formatted(uid));
        }
    }

    private void validateNameForUserCoins(Coin coin) {
        coinsJpaRepository.findByNameAndUserAccountUsername(coin.getName(), coin.getUsername())
                .ifPresent(coinEntity -> {
                    if (!StringUtils.equals(coin.getUid(), coinEntity.getUid())) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Coin name already exists for this user. Please choose another name.");
                    }
                });
    }

    private CoinEntity populateCoinEntity(Coin coin, Long id) {
        var coinEntity = mapper.map(coin);
        var userAccount = new UserAccount();

        userAccount.setId(id);
        coinEntity.setUserAccount(userAccount);
        coinEntity.setAmount(BigDecimal.ZERO);

        return coinEntity;
    }
}