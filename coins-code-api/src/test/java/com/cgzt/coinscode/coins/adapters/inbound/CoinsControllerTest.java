package com.cgzt.coinscode.coins.adapters.inbound;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.util.Objects;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(value = "/coins-controller-tests.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class CoinsControllerTest {
    static final String COINS_NAME_IMAGE = "/coins/{name}/image";
    static final String COINS_UID_IMAGE = "/coins/{uid}/image";
    static String token = "Basic " + Base64.encodeBase64String("testexist_coin:resu".getBytes());
    static String employeeToken = "Basic " + Base64.encodeBase64String("employee:resu".getBytes());

    @Autowired
    MockMvc mockMvc;

    @Value("${coin.image.dir}")
    String imageDir;

    static void cleanFile(File coinImagesDir) {
        FileSystemUtils.deleteRecursively(coinImagesDir);
    }

    @Test
    void testGetCoinsByUid() throws Exception {
        mockMvc.perform(get("/coins")
                        .header("Authorization", token)
                        .param("uid", "testexist-coin-1")
                        .param("username", "testexist_coin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coins.size()").value(1));
    }

    @Test
    void testGetCoinsByUid_NotFound() throws Exception {
        mockMvc.perform(get("/coins")
                        .header("Authorization", token)
                        .param("uid", "not found")
                        .param("username", "testexist_coin"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCoin() throws Exception {
        mockMvc.perform(get("/coins/{uid}", "testexist-coin-1")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value("testexist-coin-1"));
    }

    @Test
    void testGetCoin_NotFound() throws Exception {
        mockMvc.perform(get("/coins/{uid}", "not found")
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCoinByUsernameAndName() throws Exception {
        mockMvc.perform(get("/coins")
                        .header("Authorization", token)
                        .param("username", "testexist_coin")
                        .param("name", "Test Coin 1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coins.size()").value(1));
    }

    @Test
    void testGetCoinByUsernameAndName_NotFound() throws Exception {
        mockMvc.perform(get("/coins")
                        .header("Authorization", token)
                        .param("username", "testexist_coin")
                        .param("name", "not found"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCoinsByUsername() throws Exception {
        mockMvc.perform(get("/coins")
                        .header("Authorization", token)
                        .param("username", "testexist_coin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coins.size()").value(6));
    }

    @Test
    void testGetCoinsByUsernameNotExist() throws Exception {
        mockMvc.perform(get("/coins")
                        .header("Authorization", token)
                        .param("username", "usernotexist"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAllCoins() throws Exception {
        mockMvc.perform(get("/coins")
                        .header("Authorization", employeeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coins.size()").value(5));
    }

    @Test
    void testGetAllCoinsFailUser() throws Exception {
        mockMvc.perform(get("/coins")
                        .header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateCoin() throws Exception {
        String requestBody = """
                {
                    "name": "coinName",
                    "username": "testexist_coin",
                    "description": "coinDescription"
                }
                """;

        mockMvc.perform(post("/coins")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateCoin_ExistingCoin() throws Exception {
        String requestBody = """
                {
                    "name": "Test Coin 1",
                    "username": "testexist_coin",
                    "description": "coinDescription"
                }
                """;

        mockMvc.perform(post("/coins")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }

    @Test
    void testUpdateCoinNonExisting() throws Exception {
        String requestBody = """
                {
                    "uid": "123",
                    "name": "updatedCoinName",
                    "username": "testUser",
                    "description": ""
                }
                """;

        mockMvc.perform(patch("/coins")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateCoin_ExistingCoin() throws Exception {
        String requestBody = """
                {
                    "uid": "testexist-coin-1",
                    "name": "Test Coin 1",
                    "username": "testexist_coin",
                    "description": "coinDescription one"
                }
                """;

        mockMvc.perform(patch("/coins")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateCoin_ExistingCoin_Conflict() throws Exception {
        String requestBody = """
                {
                    "uid": "testexist-coin-1",
                    "name": "Test Coin 2",
                    "username": "testexist_coin",
                    "description": "coinDescription one"
                }
                """;

        mockMvc.perform(patch("/coins")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }

    @Test
    void testDeleteCoin() throws Exception {
        mockMvc.perform(delete("/coins/{uid}", "testdelete-coin-1")
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    void testExistsCoin() throws Exception {
        mockMvc.perform(head("/coins")
                        .header("Authorization", token)
                        .param("username", "testexist_coin")
                        .param("name", "Test Coin 1"))
                .andExpect(status().isOk());
    }

    @Test
    void testExistsCoin_NotFound() throws Exception {
        mockMvc.perform(head("/coins")
                        .header("Authorization", token)
                        .param("username", "not found")
                        .param("name", "testUser"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateCoinImage() throws Exception {
        var uid = "testexist-coin-1";
        var image = new MockMultipartFile("image", "coin.png", "image/png", new ClassPathResource("coin.png").getInputStream());

        mockMvc.perform(multipart(COINS_UID_IMAGE, uid)
                        .file(image)
                        .header("Authorization", token))
                .andExpect(status().isOk());

        var coinImagesDir = new File(imageDir);
        var images = Objects.requireNonNull(coinImagesDir.listFiles());

        assertNotNull(images);
        assertEquals(1, images.length);

        cleanFile(coinImagesDir);
    }

    @Test
    void testUpdateCoinImage_NotFound() throws Exception {
        var uid = "not found";
        var image = new MockMultipartFile("image", uid + ".png", "image/png", uid.getBytes());

        mockMvc.perform(multipart(COINS_UID_IMAGE, uid)
                        .file(image)
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCoinImage() throws Exception {
        var imageName = "testexist-coin-1.png";
        var image = new ClassPathResource("coin.png");
        var imagesDir = new File(imageDir);
        var coinImage = new File(imageDir + "/" + imageName);

        imagesDir.mkdirs();
        FileSystemUtils.copyRecursively(image.getFile(), coinImage);

        mockMvc.perform(get(COINS_NAME_IMAGE, imageName)
                        .header("Authorization", token))
                .andExpect(status().isOk());

        cleanFile(imagesDir);
    }

    @Test
    void testGetCoinImage_NotFound() throws Exception {
        var imageName = "not found";

        mockMvc.perform(get(COINS_NAME_IMAGE, imageName)
                        .header("Authorization", token))
                .andExpect(status().isBadRequest());
    }

}
