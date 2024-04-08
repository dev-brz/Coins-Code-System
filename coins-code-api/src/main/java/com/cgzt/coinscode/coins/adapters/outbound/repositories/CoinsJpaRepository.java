package com.cgzt.coinscode.coins.adapters.outbound.repositories;

import com.cgzt.coinscode.coins.adapters.outbound.entities.CoinEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

interface CoinsJpaRepository extends JpaRepository<CoinEntity, Long> {
    Optional<CoinEntity> findByUid(String uid);

    @Transactional
    void deleteByUid(String uid);

    boolean existsByUid(String uid);

    @Query("SELECT c.id FROM CoinEntity c WHERE c.uid = ?1")
    Optional<Long> findIdByUid(String uid);

    @Query("SELECT c FROM CoinEntity c WHERE c.userAccount.username = ?1")
    List<CoinEntity> findAllByUserAccountUsername(String username);

    @Query("SELECT c FROM CoinEntity c WHERE c.name = ?1 AND c.userAccount.username = ?2")
    Optional<CoinEntity> findByNameAndUserAccountUsername(String name, String username);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CoinEntity c WHERE c.name = ?1 AND c.userAccount.username = ?2")
    boolean existsByNameAndUserAccountUsername(String name, String username);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CoinEntity c WHERE c.uid = ?1 AND c.userAccount.username = ?2")
    boolean existsByUidAndUserAccountUsername(String uid, String username);
}
