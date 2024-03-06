package com.cgzt.coinscode.adapters.outbound.adapters;

import com.cgzt.coinscode.adapters.outbound.entities.UserDao;
import com.cgzt.coinscode.adapters.outbound.mappers.UserDtoMapper;
import com.cgzt.coinscode.adapters.outbound.repositories.UserRepository;
import com.cgzt.coinscode.domain.ports.data.UserDto;
import com.cgzt.coinscode.domain.ports.outbound.UserRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRepositoryAdapterImpl implements UserRepositoryAdapter {
    private final UserRepository repository;
    private final UserDtoMapper mapper;
    protected @Value("${user.profile.image-url}") String imageUrl;


    @Override
    public Optional<UserDto> findByUsername(String username) {
        return repository.findByUsername(username)
                .map(this::mapUserDto);
    }

    @Override
    public void deleteByUsername(String username) {
        repository.findByUsername(username)
                .ifPresent(repository::delete);
    }

    @Override
    public List<UserDto> getAll() {
        return mapper.daoListToDtoList(repository.findAll());
    }

    @Override
    public void save(UserDto user) {
        repository.findByUsername(user.getUsername())
                .map(it -> {
                    populate(user, it);
                    return it;
                })
                .ifPresentOrElse(repository::save, createUser(user));
    }

    @Override
    public void updateUserImageUrl(String username, String name) {
        repository.findByUsername(username)
                .ifPresent(it -> {
                    it.setImageName(name);
                    repository.save(it);
                });
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    private Runnable createUser(UserDto user) {
        return () -> {
            var userDao = mapper.dtoToDao(user);
            repository.save(userDao);
        };
    }


    protected void populate(UserDto source, UserDao target) {
        copyDetails(source, target);

        if ( StringUtils.isEmpty(target.getUsername()) ) {
            target.setUsername(source.getUsername());
        }

    }


    protected void copyDetails(UserDto source, UserDao target) {
        if ( StringUtils.isNotBlank(source.getEmail()) ) {
            target.setEmail(source.getEmail());
        }

        if ( StringUtils.isNotBlank(source.getFirstName()) ) {
            target.setFirstName(source.getFirstName());
        }

        if ( StringUtils.isNotBlank(source.getLastName()) ) {
            target.setLastName(source.getLastName());
        }

        if ( StringUtils.isNotBlank(source.getPhoneNumber()) ) {
            target.setPhoneNumber(source.getPhoneNumber());
        }

        if ( StringUtils.isNotBlank(source.getProfileImage()) ) {
            target.setImageName(source.getProfileImage());
        }
    }

    protected UserDto mapUserDto(UserDao it) {
        var user = mapper.daoToDto(it);
        user.setProfileImage(imageUrl + it.getImageName());
        return user;
    }
}
