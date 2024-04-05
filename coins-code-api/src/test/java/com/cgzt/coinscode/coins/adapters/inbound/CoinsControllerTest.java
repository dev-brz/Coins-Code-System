package com.cgzt.coinscode.coins.adapters.inbound;

import com.cgzt.coinscode.annotations.TestWithUser;
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
@Sql(value =
        {
                "/clear-all-tables-tests.sql",
                "/coins-controller-tests.sql",
        }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class CoinsControllerTest {
    static final String COINS_NAME_IMAGE = "/coins/{name}/image";
    static final String COINS_UID_IMAGE = "/coins/{uid}/image";

    @Autowired
    MockMvc mockMvc;

    @Value("${coin.image.dir}")
    String imageDir;

    static void cleanFile(File coinImagesDir) {
        FileSystemUtils.deleteRecursively(coinImagesDir);
    }

    @TestWithUser(username = "testexist_coin")
    void testGetCoinsByUid() throws Exception {
        mockMvc.perform(get("/coins")

                        .param("uid", "testexist-coin-1")
                        .param("username", "testexist_coin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coins.size()").value(1));
    }

    @TestWithUser(username = "testexist_coin")
    void testGetCoinsByUid_NotFound() throws Exception {
        mockMvc.perform(get("/coins")

                        .param("uid", "not found")
                        .param("username", "testexist_coin"))
                .andExpect(status().isNotFound());
    }

    @TestWithUser(username = "testexist_coin", roles = "ADMIN")
    void testGetCoin() throws Exception {
        mockMvc.perform(get("/coins/{uid}", "testexist-coin-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value("testexist-coin-1"));
    }

    @TestWithUser(username = "another_user")
    void testGetCoin_Forbidden() throws Exception {
        mockMvc.perform(get("/coins/{uid}", "testexist-coin-1"))
                .andExpect(status().isForbidden());
    }

    @TestWithUser(username = "testexist_coin")
    void testGetCoin_NotFound() throws Exception {
        mockMvc.perform(get("/coins/{uid}", "not found"))
                .andExpect(status().isNotFound());
    }

    @TestWithUser(username = "testexist_coin")
    void testGetCoinByUsernameAndName() throws Exception {
        mockMvc.perform(get("/coins")

                        .param("username", "testexist_coin")
                        .param("name", "Test Coin 1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coins.size()").value(1));
    }

    @TestWithUser(username = "testexist_coin")
    void testGetCoinByUsernameAndName_NotFound() throws Exception {
        mockMvc.perform(get("/coins")

                        .param("username", "testexist_coin")
                        .param("name", "not found"))
                .andExpect(status().isNotFound());
    }

    @TestWithUser(username = "testexist_coin")
    void testGetCoinsByUsername() throws Exception {
        mockMvc.perform(get("/coins")

                        .param("username", "testexist_coin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coins.size()").value(6));
    }

    @TestWithUser(username = "testexist_coin")
    void testGetCoinsByUsernameNotExist() throws Exception {
        mockMvc.perform(get("/coins")

                        .param("username", "usernotexist"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "employee", roles = "EMPLOYEE")
    void testGetAllCoins() throws Exception {
        mockMvc.perform(get("/coins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coins.size()").value(5));
    }

    @TestWithUser(username = "testexist_coin")
    void testGetAllCoinsFailUser() throws Exception {
        mockMvc.perform(get("/coins"))
                .andExpect(status().isForbidden());
    }

    @TestWithUser(username = "testexist_coin")
    void testCreateCoin() throws Exception {
        String requestBody = """
                {
                    "name": "coinName",
                    "username": "testexist_coin",
                    "description": "coinDescription"
                }
                """;

        mockMvc.perform(post("/coins")

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @TestWithUser(username = "testexist_coin")
    void testCreateCoin_ExistingCoin() throws Exception {
        String requestBody = """
                {
                    "name": "Test Coin 1",
                    "username": "testexist_coin",
                    "description": "coinDescription"
                }
                """;

        mockMvc.perform(post("/coins")

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }

    @TestWithUser(username = "testexist_coin")
    void testCreateCoin_Forbidden() throws Exception {
        String requestBody = """
                {
                    "name": "Test Coin 1",
                    "username": "another_user",
                    "description": "coinDescription"
                }
                """;

        mockMvc.perform(post("/coins")

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @TestWithUser(username = "testexist_coin")
    void testUpdateCoinNonExisting() throws Exception {
        String requestBody = """
                {
                    "uid": "123",
                    "name": "updatedCoinName",
                    "username": "testexist_coin",
                    "description": ""
                }
                """;

        mockMvc.perform(patch("/coins")

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @TestWithUser(username = "testexist_coin")
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

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @TestWithUser(username = "testexist_coin")
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

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict());
    }

    @TestWithUser(username = "testexist_coin")
    void testUpdateCoin_ExistingCoin_Forbidden() throws Exception {
        String requestBody = """
                {
                    "uid": "testexist-coin-1",
                    "name": "Test Coin 1",
                    "username": "anotherUser",
                    "description": "coinDescription one"
                }
                """;

        mockMvc.perform(patch("/coins")

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }


    @TestWithUser(username = "testexist_coin", roles = "ADMIN")
    void testDeleteCoin() throws Exception {
        mockMvc.perform(delete("/coins/{uid}", "testdelete-coin-1"))
                .andExpect(status().isOk());
    }

    @TestWithUser(username = "another_user")
    void testDeleteCoin_Forbidden() throws Exception {
        mockMvc.perform(delete("/coins/{uid}", "testdelete-coin-1"))
                .andExpect(status().isForbidden());
    }

    @TestWithUser(username = "testexist_coin")
    void testExistsCoin() throws Exception {
        mockMvc.perform(head("/coins")

                        .param("username", "testexist_coin")
                        .param("name", "Test Coin 1"))
                .andExpect(status().isOk());
    }

    @TestWithUser(username = "testexist_coin")
    void testExistsCoin_NotFound() throws Exception {
        mockMvc.perform(head("/coins")

                        .param("username", "not found")
                        .param("name", "testUser"))
                .andExpect(status().isNotFound());
    }

    @TestWithUser(username = "testexist_coin")
    void testUpdateCoinImage() throws Exception {
        var uid = "testexist-coin-1";
        var image = new MockMultipartFile("image", "coin.png", "image/png", new ClassPathResource("coin.png").getInputStream());

        mockMvc.perform(multipart(COINS_UID_IMAGE, uid)
                        .file(image))
                .andExpect(status().isOk());

        var coinImagesDir = new File(imageDir);
        var images = Objects.requireNonNull(coinImagesDir.listFiles());

        assertNotNull(images);
        assertEquals(1, images.length);

        cleanFile(coinImagesDir);
    }

    @TestWithUser(username = "testexist_coin")
    void testUpdateCoinImage_NotFound() throws Exception {
        var uid = "not found";
        var image = new MockMultipartFile("image", uid + ".png", "image/png", uid.getBytes());

        mockMvc.perform(multipart(COINS_UID_IMAGE, uid)
                        .file(image))
                .andExpect(status().isNotFound());
    }

    @TestWithUser(username = "testexist_coin")
    void testGetCoinImage() throws Exception {
        var imageName = "testexist-coin-1.png";
        var image = new ClassPathResource("coin.png");
        var imagesDir = new File(imageDir);
        var coinImage = new File(imageDir + "/" + imageName);

        imagesDir.mkdirs();
        FileSystemUtils.copyRecursively(image.getFile(), coinImage);

        mockMvc.perform(get(COINS_NAME_IMAGE, imageName))
                .andExpect(status().isOk());

        cleanFile(imagesDir);
    }

    @TestWithUser(username = "testexist_coin")
    void testGetCoinImage_NotFound() throws Exception {
        var imageName = "not found";

        mockMvc.perform(get(COINS_NAME_IMAGE, imageName))
                .andExpect(status().isBadRequest());
    }

}
