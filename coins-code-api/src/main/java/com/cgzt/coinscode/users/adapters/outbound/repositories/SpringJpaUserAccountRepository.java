package com.cgzt.coinscode.users.adapters.outbound.repositories;

import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
interface SpringJpaUserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);

    boolean existsByUsernameOrEmailOrPhoneNumber(String username, String email, String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumberAndUsernameNot(String phoneNumber, String username);

    boolean existsByEmail(String email);

    boolean existsByEmailAndUsernameNot(String email, String username);

    boolean existsByUsername(String username);

    @Modifying
    @Transactional
    @Query("update UserAccount u set u.imageName = :imageName where u.username = :username")
    void updateImageNameWhereUsername(String imageName, String username);

    @Modifying
    @Transactional
    void deleteByUsername(String username);

    @Query("select u.id from UserAccount u where u.username = :username")
    Optional<Long> findIdByUsername(String username);
}
