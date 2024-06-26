package com.cgzt.coinscode.coins.adapters.outbound.repositories;

import com.cgzt.coinscode.coins.adapters.outbound.entities.CoinEntity;
import com.cgzt.coinscode.coins.adapters.outbound.mappers.CoinsMapper;
import com.cgzt.coinscode.coins.domain.models.Coin;
import com.cgzt.coinscode.users.domain.models.User;
import com.cgzt.coinscode.users.domain.ports.outbound.repositories.CurrentUserRepository;
import com.cgzt.coinscode.users.domain.ports.outbound.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CoinsRepositoryImplTest {
    Coin coin;
    @Spy
    CoinEntity coinEntity;
    @Mock
    CoinsJpaRepository coinsJpaRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    CoinsMapper mapper;
    @Mock
    CurrentUserRepository currentUserRepository;
    @InjectMocks
    CoinsRepositoryImpl coinsRepositoryImpl;

    @BeforeEach
    void setUp() {
        coin = Coin.builder()
                .uid("testUid")
                .username("testUser")
                .name("testName")
                .imageName("testImageName")
                .description("testDescription")
                .amount(new BigDecimal("0.0"))
                .build();
    }

    @Test
    void testFindAllSuccess() {
        List<CoinEntity> coinEntities = new ArrayList<>();
        coinEntities.add(coinEntity);
        coinEntities.add(new CoinEntity());

        List<Coin> expected = List.of(coin, Coin.builder().build());

        when(mapper.map(coinEntities)).thenReturn(expected);
        when(coinsJpaRepository.findAll()).thenReturn(coinEntities);


        List<Coin> coins = coinsRepositoryImpl.findAll();

        assertNotNull(coins);
        assertEquals(coinEntities.size(), coins.size());
        verify(coinsJpaRepository).findAll();
    }


    @Test
    void testFindByUsernameSuccess() {
        var username = "testUser";
        List<CoinEntity> coinEntities = new ArrayList<>();
        coinEntities.add(coinEntity);
        coinEntities.add(new CoinEntity());
        when(coinsJpaRepository.findAllByUserAccountUsername(username)).thenReturn(coinEntities);

        assertEquals(new ArrayList<>(), coinsRepositoryImpl.findAllByUsername(username));
        verify(coinsJpaRepository).findAllByUserAccountUsername(username);
    }

    @Test
    void testFindByUsernameAndNameSuccess() {
        var username = "testUser";
        var name = "testName";
        var expected = Optional.of(coinEntity);
        when(coinsJpaRepository.findByNameAndUserAccountUsername(name, username)).thenReturn(expected);
        when(mapper.map(coinEntity)).thenReturn(coin);

        assertEquals(coin, coinsRepositoryImpl.findByUsernameAndName(name, username));

        verify(coinsJpaRepository).findByNameAndUserAccountUsername(name, username);
    }

    @Test
    void testFindByUidSuccess() {
        var uid = "testUid";
        var expected = Optional.of(coinEntity);

        when(coinsJpaRepository.findByUid(uid)).thenReturn(expected);
        when(mapper.map(coinEntity)).thenReturn(coin);
        assertEquals(coin, coinsRepositoryImpl.findByUid(uid));
        verify(coinsJpaRepository).findByUid(uid);
    }

    @Test
    void testSaveSuccess() {
        when(userRepository.findIdByUsername(coin.getUsername())).thenReturn(Optional.of(1L));
        when(coinsJpaRepository.save(coinEntity)).thenReturn(new CoinEntity());
        when(mapper.map(coin)).thenReturn(coinEntity);

        coinsRepositoryImpl.save(coin);

        assertEquals(1L, coinEntity.getUserAccount().getId());
        verify(coinEntity).setAmount(BigDecimal.ZERO);
        verify(coinsJpaRepository).save(coinEntity);
    }

    @Test
    void testUpdateSuccess() {
        var coinUpdate = Coin.builder()
                .uid("testUid")
                .name("testNameUpdate")
                .username("testUser")
                .description("testDescriptionUpdate")
                .build();

        when(coinsJpaRepository.findByNameAndUserAccountUsername(coinUpdate.getName(), coinUpdate.getUsername())).thenReturn(Optional.of(coinEntity));
        when(coinsJpaRepository.findByUid(coinUpdate.getUid())).thenReturn(Optional.of(coinEntity));
        when(coinsJpaRepository.save(any())).thenReturn(new CoinEntity());

        coinsRepositoryImpl.update(coin);

        coinEntity.setName(coinUpdate.getName());
        coinEntity.setDescription(coinUpdate.getDescription());

        verify(coinsJpaRepository).save(coinEntity);
    }


    @Test
    void testDeleteSuccess() {
        String uid = "testUid";

        doNothing().when(coinsJpaRepository).deleteByUid(uid);
        coinsRepositoryImpl.delete(uid);
        verify(coinsJpaRepository).deleteByUid(uid);
    }

    @Test
    void testUpdateImageNameSuccess() {
        String uid = "testUid";
        String imageName = "testImageName";

        when(coinsJpaRepository.findByUid(uid)).thenReturn(Optional.of(coinEntity));
        when(coinsJpaRepository.save(coinEntity)).thenReturn(new CoinEntity());

        coinsRepositoryImpl.updateImageName(uid, imageName);

        verify(coinEntity).setImageName(imageName);
        verify(coinsJpaRepository).save(coinEntity);
    }


    @Test
    void testUpdateImageNameFailure() {
        String uid = "testUid";
        String imageName = "testImageName";

        when(coinsJpaRepository.findByUid(uid)).thenReturn(Optional.empty());

        try {
            coinsRepositoryImpl.updateImageName(uid, imageName);
        } catch (Exception e) {
            assertEquals("404 NOT_FOUND \"Coin not found by uid testUid\"", e.getMessage());
        }
    }

    @Test
    void remove_withinLimits() {
        var uid = "testUid";
        var amount = new BigDecimal(1);
        var currentUser = mock(User.class);

        when(currentUser.getSendLimits()).thenReturn(new BigDecimal(100));
        when(currentUser.getUsername()).thenReturn("mock");
        when(coinEntity.getAmount()).thenReturn(BigDecimal.valueOf(200));
        when(coinsJpaRepository.findByUid(uid)).thenReturn(Optional.of(coinEntity));
        when(coinsJpaRepository.save(coinEntity)).thenReturn(new CoinEntity());
        when(currentUserRepository.get()).thenReturn(currentUser);
        when(coinsJpaRepository.findByUid(uid)).thenReturn(Optional.of(coinEntity));
        when(coinsJpaRepository.save(coinEntity)).thenReturn(new CoinEntity());
        doNothing().when(userRepository).removeSendLimits("mock", amount);
        coinsRepositoryImpl.remove(uid, amount);

        verify(coinEntity).setAmount(new BigDecimal(199));
        verify(coinsJpaRepository).save(coinEntity);
    }

    @Test
    void remove_withLessCoins() {
        var uid = "testUid";
        var amount = new BigDecimal(1);
        var currentUser = mock(User.class);

        when(currentUser.getSendLimits()).thenReturn(new BigDecimal(100));
        when(currentUser.getUsername()).thenReturn("mock");
        when(coinEntity.getAmount()).thenReturn(BigDecimal.valueOf(200));
        when(coinsJpaRepository.findByUid(uid)).thenReturn(Optional.of(coinEntity));
        when(coinsJpaRepository.save(coinEntity)).thenReturn(new CoinEntity());
        when(currentUserRepository.get()).thenReturn(currentUser);
        when(coinsJpaRepository.findByUid(uid)).thenReturn(Optional.of(coinEntity));
        when(coinsJpaRepository.save(coinEntity)).thenReturn(new CoinEntity());
        doNothing().when(userRepository).removeSendLimits("mock", amount);
        coinsRepositoryImpl.remove(uid, amount);

        assertThrows(RuntimeException.class, () -> coinsRepositoryImpl.remove(uid, new BigDecimal(201)));
    }
}