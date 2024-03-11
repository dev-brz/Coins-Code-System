package com.cgzt.coinscode.users.adapters.outbound.repositories;

import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccount;
import com.cgzt.coinscode.users.adapters.outbound.mappers.UserMapper;
import com.cgzt.coinscode.users.domain.models.User;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.model.UserUpdate;
import com.cgzt.coinscode.users.domain.ports.outbound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Repository
@RequiredArgsConstructor
class UserRepositoryImpl implements UserRepository{
    private final SpringJpaUserAccountRepository jpaUserRepository;
    private final SpringSecurityUserRepository securityUserRepository;
    private final UserMapper userMapper;

    @Override
    public List<User> findAll(){
        return userMapper.toUsers(jpaUserRepository.findAll());
    }

    @Override
    public Optional<User> findByUsername(String username){
        return jpaUserRepository.findByUsername(username).map(userMapper::toUser);
    }

    @Override
    public boolean existsByUsername(String username){
        return jpaUserRepository.existsByUsername(username);
    }

    @Override
    @Transactional
    public void save(User user, char[] password) {
        if (jpaUserRepository.existsByUsernameOrEmailOrPhoneNumber(user.getUsername(), user.getEmail(), user.getPhoneNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username, email or phone number is already taken");
        }
        UserAccount userAccount = userMapper.toUserAccount(user);

        securityUserRepository.createUserAccount(user.getUsername(), password);
        jpaUserRepository.save(userAccount);
    }

    @Override
    public void update(UserUpdate userUpdate){
        UserAccount currentUser = jpaUserRepository.findByUsername(userUpdate.username())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        validateBeforeUpdate(userUpdate);
        userMapper.merge(userUpdate, currentUser);

        jpaUserRepository.save(currentUser);
    }

    @Override
    public void updateImageName(String username, String imageName) {
        if (!jpaUserRepository.existsByUsername(username)) {
            throw new ResponseStatusException(NOT_FOUND, "User does not exist");
        }
        jpaUserRepository.updateImageNameWhereUsername(imageName, username);
    }

    @Override
    @Transactional
    public void deleteByUsername(String username){
        jpaUserRepository.deleteByUsername(username);
        securityUserRepository.deleteUserAccount(username);
    }


    private void validateBeforeUpdate(UserUpdate user) {
        if (StringUtils.isNotBlank(user.email()) && jpaUserRepository.existsByEmail(user.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided email is already taken");
        }
        if (StringUtils.isNotBlank(user.phoneNumber()) && jpaUserRepository.existsByPhoneNumber(user.phoneNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided phone number is already taken");
        }
    }
}
