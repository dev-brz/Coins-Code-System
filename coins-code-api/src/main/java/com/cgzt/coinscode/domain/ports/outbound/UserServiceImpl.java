package com.cgzt.coinscode.domain.ports.outbound;

import com.cgzt.coinscode.domain.ports.data.UserDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static com.cgzt.coinscode.Constants.ANONYMOUS_USER;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepositoryAdapter adapter;
    private final UserDetailsManager userDetailsManager;
    private final ImageService imageService;

    @Value("${user.client.roles}")
    protected String[] roles;

    @Override
    public Optional<UserDto> current() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        var username = ((User) auth.getPrincipal()).getUsername();
        if ( auth.isAuthenticated() && ! StringUtils.equals(username, ANONYMOUS_USER) ) {
            return adapter.findByUsername(username);
        }

        return Optional.empty();
    }

    @Override
    public UserDto getUser(String username) {
        return adapter.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    @Override
    public void deleteUser(String username) {
        adapter.deleteByUsername(username);
    }

    @Override
    public boolean isUserExisting(String username) {
        return adapter.existsByUsername(username);
    }

    @Override
    public List<UserDto> getUsers() {
        return adapter.getAll();
    }

    @Override
    public void createUserAccount(UserDto user, String password) {
        var userDetails = User.builder()
                .username(user.getUsername())
                .password(password)
                .roles(roles)
                .build();

        userDetailsManager.createUser(userDetails);
        adapter.save(user);
    }

    @Override
    public Boolean updateUser(UserDto userDto) {
        if ( ! isUserExisting(userDto.getUsername()) ) {
            throw new ResponseStatusException(NOT_FOUND);
        }
        adapter.save(userDto);
        return true;
    }

    @Override
    public Boolean updateUserImage(MultipartFile image) {
        return current()
                .map(user -> {
                    String imageName = imageService.store(user.getUsername(), image);
                    adapter.updateUserImageUrl(user.getUsername(), imageName);
                    return true;
                })
                .orElse(false);
    }
}
