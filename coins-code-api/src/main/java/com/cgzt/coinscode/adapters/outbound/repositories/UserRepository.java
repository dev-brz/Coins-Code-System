package com.cgzt.coinscode.adapters.outbound.repositories;

import com.cgzt.coinscode.adapters.outbound.entities.UserDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserDao, Long> {
    Optional<UserDao> findByUsername(String username);

    boolean existsByUsername(String username);
}
