package com.cgzt.coinscode.users.adapters.outbound.repositories;

import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

interface SpringJpaUserAccountRepository extends JpaRepository<UserAccountEntity, Long> {
    Optional<UserAccountEntity> findByUsername(String username);

    boolean existsByUsernameOrEmailOrPhoneNumber(String username, String email, String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumberAndUsernameNot(String phoneNumber, String username);

    boolean existsByEmail(String email);

    boolean existsByEmailAndUsernameNot(String email, String username);

    boolean existsByUsername(String username);

    @Modifying
    @Transactional
    @Query("update UserAccountEntity u set u.imageName = :imageName where u.username = :username")
    void updateImageNameWhereUsername(String imageName, String username);

    @Modifying
    @Transactional
    void deleteByUsername(String username);

    @Query("select u.id from UserAccountEntity u where u.username = :username")
    Optional<Long> findIdByUsername(String username);
}
