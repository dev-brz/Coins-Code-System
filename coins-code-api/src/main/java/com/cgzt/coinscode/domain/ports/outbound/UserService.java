package com.cgzt.coinscode.domain.ports.outbound;

import com.cgzt.coinscode.domain.ports.data.UserDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<UserDto> current();

    UserDto getUser(String username);

    void deleteUser(String username);

    boolean isUserExisting(String username);

    List<UserDto> getUsers();

    void createUserAccount(UserDto user, String password);

    Boolean updateUser(UserDto userDto);

    Boolean updateUserImage(MultipartFile image);
}
