package com.cgzt.coinscode.users.adapters.inbound;

import com.cgzt.coinscode.core.annotations.TestWithUser;
import com.cgzt.coinscode.core.utils.TestUtils;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.CreateUserCommandHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.UpdateUserCommandHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.UserLoginCommandHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.models.GetUserResult;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.models.GetUsersResult;
import com.cgzt.coinscode.users.domain.ports.outbound.repositories.UserRepository;
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
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(value = {
        "/sqls/clear-all-tables-tests.sql",
        "/sqls/user-controller-tests.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class UsersControllerTests {
    String PROFILE_IMAGE_NAME = "9x5uWsChomR9y1TeA0ZxqNME5up4-PnD7k7uMF7J8S4=.png";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;

    @Value("${user.profile.dir}")
    String imageDir;

    @Test
    void login_shouldReturn400_whenNoRequestBodyIsPresent() throws Exception {
        mockMvc.perform(post(USERS + LOGIN))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturn401_whenUserDoesNotExist() throws Exception {
        var command = new UserLoginCommandHandler.Command("testnotexist", "".toCharArray());
        var requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(post(USERS + LOGIN)
                        .content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_shouldReturn401_whenProvidedCredentialsAreInvalid() throws Exception {
        var command = new UserLoginCommandHandler.Command("testexist", "bad".toCharArray());
        var requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(post(USERS + LOGIN)
                        .content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_shouldReturn2xx_whenProvidedCredentialsAreValid() throws Exception {
        var command = new UserLoginCommandHandler.Command("testexist", "resu".toCharArray());
        var requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(post(USERS + LOGIN)
                        .content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void register_shouldReturn400_whenUsernameIsTaken() throws Exception {
        var command = new CreateUserCommandHandler.Command(
                "testexist",
                "test",
                "test",
                "test",
                "test",
                "password".toCharArray());
        var requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(post(USERS)
                        .content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturn2xx_whenProvidedBodyIsValid() throws Exception {
        var command = new CreateUserCommandHandler.Command(
                "test",
                "test",
                "test",
                "test",
                "test",
                "password".toCharArray());
        var requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(post(USERS)
                        .content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @TestWithUser(username = "testexist")
    void update_shouldBeSuccessful_whenProvidedBodyIsValid() throws Exception {
        var command = new UpdateUserCommandHandler.Command(
                "testexist",
                "test",
                "test",
                null,
                null);
        var requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(patch(USERS)
                        .content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        var updatedUser = userRepository.findByUsername(command.username()).orElseThrow();

        assertEquals(command.firstName(), updatedUser.getFirstName());
        assertEquals(command.lastName(), updatedUser.getLastName());
        assertEquals("john.doe@example.com", updatedUser.getEmail());
        assertEquals("1234567890", updatedUser.getPhoneNumber());
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
        var command = new UpdateUserCommandHandler.Command(
                "nonexistent",
                "test",
                "test",
                "test",
                "test");
        var requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(patch(USERS)
                        .content(requestBody).contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
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

        mockMvc.perform(delete(USERS + USERNAME, username))
                .andExpect(status().isOk());

        assertFalse(userRepository.existsByUsername(username));
    }

    @TestWithUser(username = "userWithoutProperRole")
    void getUsers_shouldReturn403_whenUserLacksProperRole() throws Exception {
        mockMvc.perform(get(USERS)).andExpect(status().isForbidden());
    }

    @TestWithUser(username = "testexist", roles = "ADMIN")
    void getUsers_shouldBeSuccessful() throws Exception {
        var responseBody = mockMvc.perform(get(USERS))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        var users = objectMapper.readValue(responseBody, GetUsersResult.class);

        assertFalse(users.users().isEmpty());
    }

    @TestWithUser(username = "userWithoutProperRole")
    void getUser_shouldReturn403_whenUserTriesToGetNotSelf() throws Exception {
        String anotherUsername = "anotherUsername";

        mockMvc.perform(get(USERS + USERNAME, anotherUsername))
                .andExpect(status().isForbidden());
    }

    @TestWithUser(username = "testexist")
    void getUser_shouldBeSuccessful() throws Exception {
        var username = "testexist";

        var responseBody = mockMvc.perform(get(USERS + USERNAME, username))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        var user = objectMapper.readValue(responseBody, GetUserResult.class);

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
        var image = new MockMultipartFile(
                "image",
                "profile.png",
                "image/png",
                new ClassPathResource("images/profile.png").getInputStream());

        mockMvc.perform(multipart(USERS + IMAGE)
                        .file(image))
                .andExpect(status().isOk());

        var userProfilesDir = new File(imageDir);
        var images = Objects.requireNonNull(userProfilesDir.listFiles());

        assertNotNull(images);
        assertEquals(1, images.length);

        TestUtils.cleanDirectory(userProfilesDir);
    }

    @TestWithUser(username = "testexist")
    void getImage_shouldReturnProperImage_whenUserAndImageExist() throws Exception {
        var imageName = userRepository.findByUsername("testexist")
                .orElseThrow().getImageName();
        var image = new ClassPathResource("images/profile.png");
        var profiles = new File(imageDir);
        var profile = new File(imageDir + "/" + PROFILE_IMAGE_NAME);

        profiles.mkdirs();
        FileSystemUtils.copyRecursively(image.getFile(), profile);

        mockMvc.perform(get(USERS + IMAGE)
                        .queryParam("imageName", imageName))
                .andExpect(status().isOk());

        TestUtils.cleanDirectory(profiles);
    }

    @TestWithUser(username = "testnoimage")
    void deleteImage_shouldReturn204_whenUserHasNoImage() throws Exception {
        mockMvc.perform(delete(USERS + IMAGE))
                .andExpect(status().isNoContent());
    }

    @TestWithUser(username = "testexist")
    void deleteImage_shouldDeleteImageFromDb_whenUserHasImage() throws Exception {
        var directory = Path.of(imageDir);
        var imagePath = directory.resolve(PROFILE_IMAGE_NAME);
        Files.createDirectory(directory);
        Files.createFile(imagePath);

        mockMvc.perform(delete(USERS + IMAGE))
                .andExpect(status().isNoContent());

        var imageName = userRepository.findByUsername("testexist").orElseThrow().getImageName();

        assertFalse(Files.exists(imagePath));
        assertNull(imageName);

        TestUtils.cleanDirectory(directory);
    }
}
