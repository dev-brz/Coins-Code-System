package com.cgzt.coinscode.coins.adapters.inbound;

import com.cgzt.coinscode.coins.domain.ports.inbound.commands.CreateCoinCommandHandler;
import com.cgzt.coinscode.coins.domain.ports.inbound.commands.UpdateCoinCommandHandler;
import com.cgzt.coinscode.core.annotations.TestWithUser;
import com.cgzt.coinscode.core.utils.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.util.Objects;

import static com.cgzt.coinscode.coins.adapters.inbound.CoinsController.COINS;
import static com.cgzt.coinscode.coins.adapters.inbound.CoinsController.UID;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(value = {
        "/sqls/clear-all-tables-tests.sql",
        "/sqls/coins-controller-tests.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class CoinsControllerTest {
    String COINS_NAME_IMAGE = "/coins/{name}/image";
    String COINS_UID_IMAGE = "/coins/{uid}/image";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Value("${coin.image.dir}")
    String imageDir;

    @TestWithUser(username = "testexist_coin")
    void testGetCoinsByUid() throws Exception {
        mockMvc.perform(get(COINS)
                        .param("uid", "testexist-coin-1")
                        .param("username", "testexist_coin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coins.size()").value(1));
    }

    @TestWithUser(username = "testexist_coin")
    void testGetCoinsByUid_NotFound() throws Exception {
        mockMvc.perform(get(COINS)
                        .param("uid", "not found")
                        .param("username", "testexist_coin"))
                .andExpect(status().isNotFound());
    }

    @TestWithUser(username = "testexist_coin", roles = "ADMIN")
    void testGetCoin() throws Exception {
        mockMvc.perform(get(COINS + UID, "testexist-coin-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value("testexist-coin-1"));
    }

    @TestWithUser(username = "another_user")
    void testGetCoin_Forbidden() throws Exception {
        mockMvc.perform(get(COINS + UID, "testexist-coin-1"))
                .andExpect(status().isForbidden());
    }

    @TestWithUser(username = "testexist_coin")
    void testGetCoin_NotFound() throws Exception {
        mockMvc.perform(get(COINS + UID, "not found"))
                .andExpect(status().isNotFound());
    }

    @TestWithUser(username = "testexist_coin")
    void testGetCoinByUsernameAndName() throws Exception {
        mockMvc.perform(get(COINS)
                        .param("username", "testexist_coin")
                        .param("name", "Test Coin 1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coins.size()").value(1));
    }

    @TestWithUser(username = "testexist_coin")
    void testGetCoinByUsernameAndName_NotFound() throws Exception {
        mockMvc.perform(get(COINS)
                        .param("username", "testexist_coin")
                        .param("name", "not found"))
                .andExpect(status().isNotFound());
    }

    @TestWithUser(username = "testexist_coin")
    void testGetCoinsByUsername() throws Exception {
        mockMvc.perform(get(COINS)
                        .param("username", "testexist_coin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coins.size()").value(6));
    }

    @TestWithUser(username = "testexist_coin")
    void testGetCoinsByUsernameNotExist() throws Exception {
        mockMvc.perform(get(COINS)
                        .param("username", "usernotexist"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "employee", roles = "EMPLOYEE")
    void testGetAllCoins() throws Exception {
        mockMvc.perform(get(COINS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coins.size()").value(5));
    }

    @TestWithUser(username = "testexist_coin")
    void testGetAllCoinsFailUser() throws Exception {
        mockMvc.perform(get(COINS))
                .andExpect(status().isForbidden());
    }

    @TestWithUser(username = "testexist_coin")
    void testCreateCoin() throws Exception {
        var command = new CreateCoinCommandHandler.Command(
                "coinName",
                "testexist_coin",
                "coinDescription");
        var requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(post(COINS)
                        .content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @TestWithUser(username = "testexist_coin")
    void testCreateCoin_ExistingCoin() throws Exception {
        var command = new CreateCoinCommandHandler.Command(
                "Test Coin 1",
                "testexist_coin",
                "coinDescription");
        var requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(post(COINS)
                        .content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @TestWithUser(username = "testexist_coin")
    void testCreateCoin_Forbidden() throws Exception {
        var command = new CreateCoinCommandHandler.Command(
                "Test Coin 1",
                "another_user",
                "coinDescription");
        var requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(post(COINS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @TestWithUser(username = "testexist_coin")
    void testUpdateCoinNonExisting() throws Exception {
        var command = new UpdateCoinCommandHandler.Command(
                "123",
                "updatedCoinName",
                "testexist_coin",
                "");
        var requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(patch(COINS)
                        .content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @TestWithUser(username = "testexist_coin")
    void testUpdateCoin_ExistingCoin() throws Exception {
        var command = new UpdateCoinCommandHandler.Command(
                "testexist-coin-1",
                "Test Coin 1",
                "testexist_coin",
                "coinDescription one");
        var requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(patch(COINS)
                        .content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @TestWithUser(username = "testexist_coin")
    void testUpdateCoin_ExistingCoin_Conflict() throws Exception {
        var command = new UpdateCoinCommandHandler.Command(
                "testexist-coin-1",
                "Test Coin 2",
                "testexist_coin",
                "coinDescription one");
        var requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(patch(COINS)
                        .content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @TestWithUser(username = "testexist_coin")
    void testUpdateCoin_ExistingCoin_Forbidden() throws Exception {
        var command = new UpdateCoinCommandHandler.Command(
                "testexist-coin-1",
                "Test Coin 1",
                "anotherUser",
                "coinDescription one");
        var requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(patch(COINS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }


    @TestWithUser(username = "testexist_coin", roles = "ADMIN")
    void testDeleteCoin() throws Exception {
        mockMvc.perform(delete(COINS + UID, "testdelete-coin-1"))
                .andExpect(status().isOk());
    }

    @TestWithUser(username = "another_user")
    void testDeleteCoin_Forbidden() throws Exception {
        mockMvc.perform(delete(COINS + UID, "testdelete-coin-1"))
                .andExpect(status().isForbidden());
    }

    @TestWithUser(username = "testexist_coin")
    void testExistsCoin() throws Exception {
        mockMvc.perform(head(COINS)
                        .param("username", "testexist_coin")
                        .param("name", "Test Coin 1"))
                .andExpect(status().isOk());
    }

    @TestWithUser(username = "testexist_coin")
    void testExistsCoin_NotFound() throws Exception {
        mockMvc.perform(head(COINS)
                        .param("username", "not found")
                        .param("name", "testUser"))
                .andExpect(status().isNotFound());
    }

    @TestWithUser(username = "testexist_coin")
    void testUpdateCoinImage() throws Exception {
        var uid = "testexist-coin-1";
        var image = new MockMultipartFile(
                "image",
                "coin.png",
                "image/png",
                new ClassPathResource("images/coin.png").getInputStream());

        mockMvc.perform(multipart(COINS_UID_IMAGE, uid)
                        .file(image))
                .andExpect(status().isOk());

        var coinImagesDir = new File(imageDir);
        var images = Objects.requireNonNull(coinImagesDir.listFiles());

        assertNotNull(images);
        assertEquals(1, images.length);

        TestUtils.cleanDirectory(coinImagesDir);
    }

    @TestWithUser(username = "testexist_coin")
    void testUpdateCoinImage_NotFound() throws Exception {
        var uid = "not found";
        var image = new MockMultipartFile(
                "image",
                uid + ".png",
                "image/png",
                uid.getBytes());

        mockMvc.perform(multipart(COINS_UID_IMAGE, uid)
                        .file(image))
                .andExpect(status().isNotFound());
    }

    @TestWithUser(username = "testexist_coin")
    void testGetCoinImage() throws Exception {
        var imageName = "testexist-coin-1.png";
        var image = new ClassPathResource("images/coin.png");
        var imagesDir = new File(imageDir);
        var coinImage = new File(imageDir + "/" + imageName);

        imagesDir.mkdirs();
        FileSystemUtils.copyRecursively(image.getFile(), coinImage);

        mockMvc.perform(get(COINS_NAME_IMAGE, imageName))
                .andExpect(status().isOk());

        TestUtils.cleanDirectory(imagesDir);
    }

    @TestWithUser(username = "testexist_coin")
    void testGetCoinImage_NotFound() throws Exception {
        var imageName = "not found";

        mockMvc.perform(get(COINS_NAME_IMAGE, imageName))
                .andExpect(status().isBadRequest());
    }
}
