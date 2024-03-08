package com.cgzt.coinscode.users.adapters.outbound.repositories;

import com.cgzt.coinscode.users.adapters.outbound.entities.UserAccount;
import com.cgzt.coinscode.users.adapters.outbound.mappers.UserMapper;
import com.cgzt.coinscode.users.domain.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRepositoryImplTest {
    @Mock
    SpringJpaUserAccountRepository jpaUserAccountRepository;

    @Mock
    SpringSecurityUserRepository securityUserRepository;

    @Mock
    UserMapper mapper;

    @InjectMocks
    UserRepositoryImpl repository;

    User user;
    UserAccount userAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUsername("test");
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setPhoneNumber("1234567890");
        user.setImageName("n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg=.png");

        userAccount = new UserAccount();
        userAccount.setUsername("test");
        userAccount.setEmail("test@test.com");
        userAccount.setFirstName("Test");
        userAccount.setLastName("Test");
        userAccount.setPhoneNumber("1234567890");
        userAccount.setImageName("n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg=.png");

    }

    @Test
    void findByUsername() {
        when(jpaUserAccountRepository.findByUsername("test")).thenReturn(Optional.of(userAccount));
        when(mapper.toUser(userAccount)).thenReturn(user);

        Optional<User> result = repository.findByUsername("test");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(jpaUserAccountRepository).findByUsername("test");
        verify(mapper).toUser(userAccount);
    }

    @Test
    void deleteByUsername() {
        doNothing().when(jpaUserAccountRepository).deleteByUsername("test");
        doNothing().when(securityUserRepository).deleteUserAccount("test");

        repository.deleteByUsername("test");

        verify(jpaUserAccountRepository).deleteByUsername("test");
        verify(securityUserRepository).deleteUserAccount("test");
    }

    @Test
    void findAll() {
        List<UserAccount> daoList = List.of(userAccount);
        List<User> dtoList = List.of(user);
        when(jpaUserAccountRepository.findAll()).thenReturn(daoList);
        when(mapper.toUsers(daoList)).thenReturn(dtoList);

        List<User> result = repository.findAll();

        assertEquals(dtoList, result);
        verify(jpaUserAccountRepository).findAll();
        verify(mapper).toUsers(daoList);
    }

    @Test
    void saveWhenUserExists() {
        when(jpaUserAccountRepository.existsByUsernameOrEmailOrPhoneNumber(any(), any(), any())).thenReturn(true);

        Executable executable = () -> repository.save(user, new char[]{});

        assertThrows(RuntimeException.class, executable);
    }

    @Test
    void saveWhenUserDoesNotExist() {
        when(jpaUserAccountRepository.existsByUsernameOrEmailOrPhoneNumber(any(), any(), any())).thenReturn(false);
        when(mapper.toUserAccount(user)).thenReturn(userAccount);
        doNothing().when(securityUserRepository).createUserAccount(any(), any());
        when(jpaUserAccountRepository.save(any())).thenReturn(userAccount);

        repository.save(user, new char[]{});

        verify(securityUserRepository).createUserAccount(any(), any());
        verify(jpaUserAccountRepository).save(any());
        verify(mapper).toUserAccount(user);
    }

    @Test
    void updateUserImageName() {
        when(jpaUserAccountRepository.existsByUsername("test")).thenReturn(true);
        doNothing().when(jpaUserAccountRepository).updateImageNameWhereUsername("new.jpg", "test");

        repository.updateImageName("test", "new.jpg");

        verify(jpaUserAccountRepository).existsByUsername("test");
        verify(jpaUserAccountRepository).updateImageNameWhereUsername("new.jpg", "test");
    }
}
