package com.cgzt.coinscode.adapters.outbound.adapters;

import com.cgzt.coinscode.adapters.outbound.entities.UserDao;
import com.cgzt.coinscode.adapters.outbound.mappers.UserDtoMapper;
import com.cgzt.coinscode.adapters.outbound.repositories.UserRepository;
import com.cgzt.coinscode.domain.ports.data.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserRepositoryAdapterImplTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserDtoMapper mapper;

    @InjectMocks
    private UserRepositoryAdapterImpl adapter;

    private UserDto userDto;
    private UserDao userDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDto = new UserDto();
        userDto.setUsername("test");
        userDto.setEmail("test@test.com");
        userDto.setFirstName("Test");
        userDto.setLastName("Test");
        userDto.setPhoneNumber("1234567890");
        userDto.setProfileImage("localhost/image/n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg=.png");

        userDao = new UserDao();
        userDao.setUsername("test");
        userDao.setEmail("test@test.com");
        userDao.setFirstName("Test");
        userDao.setLastName("Test");
        userDao.setPhoneNumber("1234567890");
        userDao.setImageName("n4bQgYhMfWWaL+qgxVrQFaO/TxsrC4Is0V1sFbDwCgg=.png");

    }

    @Test
    void findByUsername() {
        when(repository.findByUsername("test")).thenReturn(Optional.of(userDao));
        when(mapper.daoToDto(userDao)).thenReturn(userDto);

        Optional<UserDto> result = adapter.findByUsername("test");

        assertTrue(result.isPresent());
        assertEquals(userDto, result.get());
        verify(repository).findByUsername("test");
        verify(mapper).daoToDto(userDao);
    }

    @Test
    void deleteByUsername() {
        when(repository.findByUsername("test")).thenReturn(Optional.of(userDao));
        doNothing().when(repository).delete(userDao);

        adapter.deleteByUsername("test");

        verify(repository).delete(userDao);
    }

    @Test
    void getAll() {
        List<UserDao> daoList = List.of(userDao);
        List<UserDto> dtoList = List.of(userDto);
        when(repository.findAll()).thenReturn(daoList);
        when(mapper.daoListToDtoList(daoList)).thenReturn(dtoList);

        List<UserDto> result = adapter.getAll();

        assertEquals(dtoList, result);
        verify(repository).findAll();
        verify(mapper).daoListToDtoList(daoList);
    }

    @Test
    void saveWhenUserExists() {
        when(repository.findByUsername("test")).thenReturn(Optional.of(userDao));
        when(repository.save(userDao)).thenReturn(userDao);

        adapter.save(userDto);

        verify(repository).findByUsername("test");
        verify(repository).save(userDao);
        verify(mapper, never()).dtoToDao(any());
    }

    @Test
    void saveWhenUserDoesNotExist() {
        when(repository.findByUsername("test")).thenReturn(Optional.empty());
        when(mapper.dtoToDao(userDto)).thenReturn(userDao);
        when(repository.save(userDao)).thenReturn(userDao);

        adapter.save(userDto);

        verify(repository).findByUsername("test");
        verify(repository).save(userDao);
        verify(mapper).dtoToDao(userDto);
    }

    @Test
    void updateUserImageUrl() {
        when(repository.findByUsername("test")).thenReturn(Optional.of(userDao));
        when(repository.save(userDao)).thenReturn(userDao);

        adapter.updateUserImageUrl("test", "new.jpg");

        assertEquals("new.jpg", userDao.getImageName());
        verify(repository).findByUsername("test");
        verify(repository).save(userDao);
    }

}