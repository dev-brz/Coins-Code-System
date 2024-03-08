package com.cgzt.coinscode.users.adapters.inbound;

import com.cgzt.coinscode.users.domain.models.User;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.CreateUserCommandHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.UpdateUserCommandHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.commands.UserLoginCommandHandler;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.model.GetUserResult;
import com.cgzt.coinscode.users.domain.ports.inbound.queries.model.GetUsersResult;
import com.cgzt.coinscode.users.domain.ports.outbound.repository.UserRepository;
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
import java.util.Objects;

import static com.cgzt.coinscode.users.adapters.inbound.UsersController.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = "/user-controller-tests.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class UsersControllerTests {
    static final String PROFILE_IMAGE_NAME = "9x5uWsChomR9y1TeA0ZxqNME5up4-PnD7k7uMF7J8S4=.png";
    static String USERS_LOGIN_URL = USERS + LOGIN;
    static String token = "Basic " + Base64.encodeBase64String("testexist:resu".getBytes());
    static String deleteToken = "Basic " + Base64.encodeBase64String("testdelete:resu".getBytes());
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

    @Test
    void update_shouldBeSuccessful_whenProvidedBodyIsValid() throws Exception {
        var updates = new UpdateUserCommandHandler.Command(
                "testexist",
                "test",
                "test",
                null,
                null);

        var user = objectMapper.writeValueAsString(updates);

        mockMvc.perform(patch(USERS)
                        .header("Authorization", token)
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

    @Test
    void update_shouldReturn404_whenProvidedUserDoesNotExist() throws Exception {
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
    void delete_shouldBeSuccessful_whenUserExisted() throws Exception {
        var username = "testdelete";

        mockMvc.perform(delete("%s/%s".formatted(USERS, username)).header("Authorization", deleteToken))
                .andExpect(status().isOk());

        assertFalse(userRepository.existsByUsername(username));
    }

    @Test
    void getUsers_shouldBeSuccessful() throws Exception {
        var results = mockMvc.perform(get(USERS).header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        var body = results.getResponse().getContentAsString();
        GetUsersResult users = objectMapper.readValue(body, GetUsersResult.class);

        assertFalse(users.users().isEmpty());
    }

    @Test
    void getUser_shouldBeSuccessful() throws Exception {
        var username = "testexist";

        var results = mockMvc.perform(get("%s/%s".formatted(USERS, username)).header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        var body = results.getResponse().getContentAsString();
        GetUserResult user = objectMapper.readValue(body, GetUserResult.class);

        assertEquals(username, user.username());
        assertEquals(PROFILE_IMAGE_NAME, user.imageName());
    }

    @Test
    void isUserExisting_shouldReturnOk_whenUserExists() throws Exception {
        var username = "testexist";
        mockMvc.perform(head("%s/%s".formatted(USERS, username)))
                .andExpect(status().isOk());
    }

    @Test
    void isUserExisting_shouldReturn404_whenUserDoesNotExist() throws Exception {
        var username = "testnonexist";
        mockMvc.perform(head("%s/%s".formatted(USERS, username)))
                .andExpect(status().isNotFound());
    }

    @Test
    void uploadImage_shouldSaveImageProperly_whenUserDoesNotExist() throws Exception {
        var image = new MockMultipartFile("image", "profile.png", "image/png", new ClassPathResource("profile.png").getInputStream());

        mockMvc.perform(multipart(USERS + "/image")
                        .file(image)
                        .header("Authorization", token))
                .andExpect(status().isOk());

        var userProfilesDir = new File(imageDir);
        var images = Objects.requireNonNull(userProfilesDir.listFiles());

        assertNotNull(images);
        assertEquals(1, images.length);

        //CLEAN IMAGE
        FileSystemUtils.deleteRecursively(userProfilesDir);
    }

    @Test
    void getImage_shouldReturnProperImage_whenUserAndImageExist() throws Exception {
        var imageName = userRepository.findByUsername("testexist")
                .orElseThrow().getImageName();
        var image = new ClassPathResource("profile.png");
        var profiles = new File(imageDir);
        var profile = new File(imageDir + "/" + PROFILE_IMAGE_NAME);

        profiles.mkdirs();
        FileSystemUtils.copyRecursively(image.getFile(), profile);

        mockMvc.perform(get(USERS + IMAGE)
                        .queryParam("imageName", imageName)
                        .header("Authorization", token))
                .andExpect(status().isOk());


        FileSystemUtils.deleteRecursively(profiles);
    }
}
