package com.cgzt.coinscode.adapters.inbound;

import com.cgzt.coinscode.domain.ports.data.UserDto;
import com.cgzt.coinscode.domain.ports.inbound.CreateUserCommandHandler;
import com.cgzt.coinscode.domain.ports.inbound.UpdateUserCommandHandler;
import com.cgzt.coinscode.domain.ports.inbound.UserLoginCommandHandler;
import com.cgzt.coinscode.domain.ports.outbound.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static com.cgzt.coinscode.adapters.inbound.UsersController.LOGIN_URL;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = "/user-controller-tests.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class UsersControllerTests {
    public static final String PROFILE_IMAGE_NAME = "9x5uWsChomR9y1TeA0ZxqNME5up4-PnD7k7uMF7J8S4=.png";
    static String USERS = "/users";
    static String USERS_LOGIN_URL = USERS + LOGIN_URL;

    static String token = "Basic " + Base64.encodeBase64String("testexist:resu".getBytes());
    static String deleteToken = "Basic " + Base64.encodeBase64String("testdelete:resu".getBytes());
    @Value("${user.profile.image-url}")
    String profileImageUrl;
    @Value("${user.profile.dir}")
    String imageDir;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserService userService;

    @Test
    void login_shouldReturn400_whenNoRequestBodyIsPresent() throws Exception {
        mockMvc.perform(post(USERS_LOGIN_URL)).andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturn401_whenUserDoesNotExist() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                new UserLoginCommandHandler.Command("invalidUsername", new char[]{}));

        mockMvc.perform(post(USERS_LOGIN_URL).contentType(APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_shouldReturn401_whenProvidedCredentialsAreInvalid() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                new UserLoginCommandHandler.Command("validUsername", new char[]{'b', 'a', 'd'}));

        mockMvc.perform(post(USERS_LOGIN_URL).contentType(APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_shouldReturn2xx_whenProvidedCredentialsAreValid() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                new UserLoginCommandHandler.Command("validUsername", new char[]{'g', 'o', 'o', 'd'}));

        mockMvc.perform(post(USERS_LOGIN_URL).contentType(APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void testRegister() throws Exception {
        String user = objectMapper.writeValueAsString(
                new CreateUserCommandHandler.Command(
                        "test",
                        "test",
                        "test",
                        "test",
                        "test",
                        "password"));

        mockMvc.perform(post(USERS).content(user).contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdate() throws Exception {
        var updates = new UpdateUserCommandHandler.Command(
                "testexist",
                "test",
                "test",
                "test",
                "test");

        var user = objectMapper.writeValueAsString(updates);

        mockMvc.perform(patch(USERS)
                        .header("Authorization", token)
                        .contentType(APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isOk());

        UserDto updated = userService.getUser(updates.username());

        assertEquals(updates.firstName(), updated.getFirstName());
        assertEquals(updates.lastName(), updated.getLastName());
        assertEquals(updates.email(), updated.getEmail());
        assertEquals(updates.phoneNumber(), updated.getPhoneNumber());
    }

    @Test
    void testUpdateForNonExist() throws Exception {
        String user = objectMapper.writeValueAsString(
                new UpdateUserCommandHandler.Command(
                        "nonexistent",
                        "test",
                        "test",
                        "test",
                        "test"));

        mockMvc.perform(patch(USERS)
                        .header("Authorization", token)
                        .content(user)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDelete() throws Exception {
        var username = "testdelete";

        mockMvc.perform(delete("%s/%s".formatted(USERS, username)).header("Authorization", deleteToken))
                .andExpect(status().isOk());

        assertFalse(userService.isUserExisting(username));
    }

    @Test
    void testGetUsers() throws Exception {
        var results = mockMvc.perform(get(USERS).header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        var body = results.getResponse().getContentAsString();
        List<UserDto> users = objectMapper.readerForListOf(UserDto.class).readValue(body);

        assertTrue(! users.isEmpty());
    }

    @Test
    void testGetUser() throws Exception {
        var username = "testexist";
        var imageUrl = profileImageUrl + PROFILE_IMAGE_NAME;

        var results = mockMvc.perform(get("%s/%s".formatted(USERS, username)).header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        var body = results.getResponse().getContentAsString();
        UserDto user = objectMapper.readerFor(UserDto.class).readValue(body);

        assertEquals(username, user.getUsername());
        assertEquals(imageUrl, user.getProfileImage());

    }

    @Test
    void isUserExisting() throws Exception {
        var username = "testexist";
        mockMvc.perform(head("%s/%s".formatted(USERS, username)))
                .andExpect(status().isOk());
    }

    @Test
    void userNotExisting() throws Exception {
        var username = "testnonexist";
        mockMvc.perform(head("%s/%s".formatted(USERS, username)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUploadUserImage() throws Exception {
        var image = new MockMultipartFile("image", "profile.png", "image/png", new ClassPathResource("profile.png").getInputStream());

        mockMvc.perform(multipart(USERS + "/image")
                        .file(image)
                        .header("Authorization", token))
                .andExpect(status().isOk());

        var userProfilesDir = new File(imageDir);

        assertNotNull(userProfilesDir.listFiles());

        var images = Objects.requireNonNull(userProfilesDir.listFiles());

        assertEquals(1, images.length);

        //CLEAN IMAGE
        FileSystemUtils.deleteRecursively(userProfilesDir);
    }

    @Test
    void testGetUserImage() throws Exception {
        var imageUrl = userService.getUser("testexist").getProfileImage();
        var image = new ClassPathResource("profile.png");
        var profiles = new File(imageDir);
        var profile = new File(imageDir + "/" + PROFILE_IMAGE_NAME);

        profiles.mkdirs();
        FileSystemUtils.copyRecursively(image.getFile(), profile);

        mockMvc.perform(get(imageUrl).header("Authorization", token))
                .andExpect(status().isOk());


        FileSystemUtils.deleteRecursively(profiles);
    }
}
