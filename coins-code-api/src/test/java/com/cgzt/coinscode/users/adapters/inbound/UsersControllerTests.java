package com.cgzt.coinscode.users.adapters.inbound;

import com.cgzt.coinscode.annotations.TestWithUser;
import com.cgzt.coinscode.users.domain.models.User;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.CreateUserCommandHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.UpdateUserCommandHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.UserLoginCommandHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.model.GetUserResult;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.model.GetUsersResult;
import com.cgzt.coinscode.users.domain.ports.outbound.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static com.cgzt.coinscode.users.adapters.inbound.UsersController.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value =
        {
                "/clear-all-tables-tests.sql",
                "/user-controller-tests.sql",
        }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class UsersControllerTests {
    static final String PROFILE_IMAGE_NAME = "9x5uWsChomR9y1TeA0ZxqNME5up4-PnD7k7uMF7J8S4=.png";
    static String USERS_LOGIN_URL = USERS + LOGIN;

    @Value("${user.profile.dir}")
    String imageDir;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;

    @Test
    void login_shouldReturn400_whenNoRequestBodyIsPresent() throws Exception {
        mockMvc.perform(post(USERS_LOGIN_URL)).andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturn401_whenUserDoesNotExist() throws Exception {
        String requestBody = objectMapper.writeValueAsString(new UserLoginCommandHandler.Command("testnotexist", new char[]{}));

        mockMvc.perform(post(USERS_LOGIN_URL).contentType(APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_shouldReturn401_whenProvidedCredentialsAreInvalid() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                new UserLoginCommandHandler.Command("testexist", "bad".toCharArray()));

        mockMvc.perform(post(USERS_LOGIN_URL).contentType(APPLICATION_JSON).content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_shouldReturn2xx_whenProvidedCredentialsAreValid() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                new UserLoginCommandHandler.Command("testexist", "resu".toCharArray()));

        mockMvc.perform(post(USERS_LOGIN_URL).contentType(APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void register_shouldReturn400_whenUsernameIsTaken() throws Exception {
        String user = objectMapper.writeValueAsString(
                new CreateUserCommandHandler.Command(
                        "testexist",
                        "test",
                        "test",
                        "test",
                        "test",
                        "password".toCharArray()));

        mockMvc.perform(post(USERS).content(user).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturn2xx_whenProvidedBodyIsValid() throws Exception {
        String user = objectMapper.writeValueAsString(
                new CreateUserCommandHandler.Command(
                        "test",
                        "test",
                        "test",
                        "test",
                        "test",
                        "password".toCharArray()));

        mockMvc.perform(post(USERS).content(user).contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @TestWithUser(username = "username")
    void update_shouldReturn403_whenUserTriesToUpdateOtherUser() throws Exception {
        var command = new UpdateUserCommandHandler.Command("anotherUsername", "", "", "", "");
        String requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(patch(USERS).content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @TestWithUser(username = "nonexistent")
    void update_shouldReturn404_whenProvidedUserDoesNotExist() throws Exception {
        String user = objectMapper.writeValueAsString(
                new UpdateUserCommandHandler.Command(
                        "nonexistent",
                        "test",
                        "test",
                        "test",
                        "test"));

        mockMvc.perform(patch(USERS)
                        .content(user)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @TestWithUser(username = "testexist")
    void update_shouldBeSuccessful_whenProvidedBodyIsValid() throws Exception {
        var updates = new UpdateUserCommandHandler.Command(
                "testexist",
                "test",
                "test",
                null,
                null);

        var user = objectMapper.writeValueAsString(updates);

        mockMvc.perform(patch(USERS)
                        .contentType(APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isOk());

        User updated = userRepository
                .findByUsername(updates.username())
                .orElseThrow();

        assertEquals(updates.firstName(), updated.getFirstName());
        assertEquals(updates.lastName(), updated.getLastName());
        assertEquals("john.doe@example.com", updated.getEmail());
        assertEquals("1234567890", updated.getPhoneNumber());
    }

    @TestWithUser(username = "username")
    void delete_shouldReturn403_whenUserTriesToDeleteOtherUser() throws Exception {
        String anotherUsername = "anotherUsername";

        mockMvc.perform(delete(USERS + USERNAME, anotherUsername).contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @TestWithUser(username = "testdelete")
    void delete_shouldBeSuccessful_whenUserExisted() throws Exception {
        var username = "testdelete";

        mockMvc.perform(delete("%s/%s".formatted(USERS, username)))
                .andExpect(status().isOk());

        assertFalse(userRepository.existsByUsername(username));
    }

    @TestWithUser(username = "userWithoutProperRole")
    void getUsers_shouldReturn403_whenUserLacksProperRole() throws Exception {
        mockMvc.perform(get(USERS)).andExpect(status().isForbidden());
    }

    @TestWithUser(username = "testexist", roles = "ADMIN")
    void getUsers_shouldBeSuccessful() throws Exception {
        var results = mockMvc.perform(get(USERS)).andExpect(status().isOk()).andReturn();

        var body = results.getResponse().getContentAsString();
        GetUsersResult users = objectMapper.readValue(body, GetUsersResult.class);

        assertFalse(users.users().isEmpty());
    }

    @TestWithUser(username = "userWithoutProperRole")
    void getUser_shouldReturn403_whenUserTriesToGetNotSelf() throws Exception {
        String anotherUsername = "anotherUsername";

        mockMvc.perform(get("%s/%s".formatted(USERS, anotherUsername)))
                .andExpect(status().isForbidden());
    }

    @TestWithUser(username = "testexist")
    void getUser_shouldBeSuccessful() throws Exception {
        var username = "testexist";

        var results = mockMvc.perform(get("%s/%s".formatted(USERS, username))).andExpect(status().isOk()).andReturn();

        var body = results.getResponse().getContentAsString();
        GetUserResult user = objectMapper.readValue(body, GetUserResult.class);

        assertEquals(username, user.username());
        assertEquals(PROFILE_IMAGE_NAME, user.imageName());
    }

    @Test
    void isUserExisting_shouldReturnOk_whenUserExists() throws Exception {
        var username = "testexist";

        mockMvc.perform(head(USERS).queryParam("username", username))
                .andExpect(status().isOk());
    }

    @Test
    void isUserExisting_shouldReturn404_whenUserDoesNotExist() throws Exception {
        var username = "testnonexist";

        mockMvc.perform(head(USERS).queryParam("username", username))
                .andExpect(status().isNotFound());
    }

    @TestWithUser(username = "testexist")
    void uploadImage_shouldSaveImageProperly() throws Exception {
        var image = new MockMultipartFile("image", "profile.png", "image/png", new ClassPathResource("profile.png").getInputStream());

        mockMvc.perform(multipart(USERS + "/image")
                        .file(image))
                .andExpect(status().isOk());

        var userProfilesDir = new File(imageDir);
        var images = Objects.requireNonNull(userProfilesDir.listFiles());

        assertNotNull(images);
        assertEquals(1, images.length);

        //CLEAN IMAGE
        FileSystemUtils.deleteRecursively(userProfilesDir);
    }

    @TestWithUser(username = "testexist")
    void getImage_shouldReturnProperImage_whenUserAndImageExist() throws Exception {
        var imageName = userRepository.findByUsername("testexist")
                .orElseThrow().getImageName();
        var image = new ClassPathResource("profile.png");
        var profiles = new File(imageDir);
        var profile = new File(imageDir + "/" + PROFILE_IMAGE_NAME);

        profiles.mkdirs();
        FileSystemUtils.copyRecursively(image.getFile(), profile);

        mockMvc.perform(get(USERS + IMAGE)
                        .queryParam("imageName", imageName))
                .andExpect(status().isOk());


        FileSystemUtils.deleteRecursively(profiles);
    }

    @TestWithUser(username = "testnoimage")
    void deleteImage_shouldReturn204_whenUserHasNoImage() throws Exception {
        mockMvc.perform(delete(USERS + IMAGE))
                .andExpect(status().isNoContent());
    }

    @TestWithUser(username = "testexist")
    void deleteImage_shouldDeleteImageFromDb_whenUserHasImage() throws Exception {
        Path directory = Path.of(imageDir);
        Path imagePath = directory.resolve(PROFILE_IMAGE_NAME);
        Files.createDirectory(directory);
        Files.createFile(imagePath);

        mockMvc.perform(delete(USERS + IMAGE)).andExpect(status().isNoContent());
        String imageName = userRepository.findByUsername("testexist").orElseThrow().getImageName();

        assertFalse(Files.exists(imagePath));
        assertNull(imageName);

        FileSystemUtils.deleteRecursively(directory);
    }
}
