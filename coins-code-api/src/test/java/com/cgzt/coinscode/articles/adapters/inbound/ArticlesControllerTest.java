package com.cgzt.coinscode.articles.adapters.inbound;

import com.cgzt.coinscode.articles.adapters.inbound.models.UpdateArticleRequestBody;
import com.cgzt.coinscode.articles.adapters.outbound.entities.ArticleEntity;
import com.cgzt.coinscode.articles.adapters.outbound.entities.ArticleImageEntity;
import com.cgzt.coinscode.core.annotations.TestWithUser;
import com.cgzt.coinscode.core.repositories.TestArticleImageRepository;
import com.cgzt.coinscode.core.repositories.TestArticleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static com.cgzt.coinscode.articles.adapters.inbound.ArticlesController.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql("/sqls/clear-all-tables-tests.sql")
class ArticlesControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TestArticleRepository articleRepository;
    @Autowired
    TestArticleImageRepository articleImageRepository;

    @Test
    void getArticles_shouldReturn401_whenNotLoggedIn() throws Exception {
        mockMvc.perform(get(ARTICLES))
                .andExpect(status().isUnauthorized());
    }

    @TestWithUser(username = "test")
    void getArticles_shouldReturn200() throws Exception {
        mockMvc.perform(get(ARTICLES))
                .andExpect(status().isOk());
    }

    @TestWithUser(username = "test")
    @Sql({"/sqls/clear-all-tables-tests.sql", "/sqls/articles/add-sample-articles.sql"})
    void getArticles_shouldReturnSecondPageWithTwoElements() throws Exception {
        mockMvc.perform(get(ARTICLES).queryParam("page", "1").queryParam("size", "3"))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.number", is(1)))
                .andExpect(jsonPath("$.totalElements", is(5)));
    }

    @Test
    void getArticle_shouldReturn401_whenNotLoggedIn() throws Exception {
        mockMvc.perform(get(ARTICLES + UID, 1))
                .andExpect(status().isUnauthorized());
    }

    @TestWithUser(username = "test")
    void getArticle_shouldReturn404_whenArticleDoesNotExist() throws Exception {
        mockMvc.perform(get(ARTICLES + UID, 1))
                .andExpect(status().isNotFound());
    }

    @TestWithUser(username = "test")
    @Sql({"/sqls/clear-all-tables-tests.sql", "/sqls/articles/add-sample-article.sql"})
    void getArticle_shouldReturn200() throws Exception {
        mockMvc.perform(get(ARTICLES + UID, 1))
                .andExpect(status().isOk());
    }

    @Test
    void addArticle_shouldReturn401_whenNotLoggedIn() throws Exception {
        mockMvc.perform(multipart(ARTICLES))
                .andExpect(status().isUnauthorized());
    }

    @TestWithUser(username = "test")
    void addArticle_shouldReturn403_whenHasNoPermission() throws Exception {
        mockMvc.perform(multipart(ARTICLES)
                        .part(new MockPart("title", "title".getBytes()))
                        .part(new MockPart("content", "content".getBytes())))
                .andExpect(status().isForbidden());
    }

    @TestWithUser(username = "test", roles = "EMPLOYEE")
    void addArticle_shouldReturn400_whenContentIsInvalid() throws Exception {
        mockMvc.perform(multipart(ARTICLES)
                        .part(new MockPart("title", "".getBytes()))
                        .part(new MockPart("content", "content".getBytes())))
                .andExpect(status().isBadRequest());
    }

    @TestWithUser(username = "test", roles = "EMPLOYEE")
    void addArticle_shouldReturn201_whenContentIsValid() throws Exception {
        mockMvc.perform(multipart(ARTICLES)
                        .part(new MockPart("title", "title".getBytes()))
                        .part(new MockPart("content", "content".getBytes())))
                .andExpect(status().isCreated());
    }

    @TestWithUser(username = "test", roles = "EMPLOYEE")
    void addArticle_shouldReturnUid_inLocationHeader() throws Exception {
        String location = mockMvc.perform(multipart(ARTICLES)
                        .part(new MockPart("title", "title".getBytes()))
                        .part(new MockPart("content", "content".getBytes())))
                .andReturn()
                .getResponse()
                .getHeader(HttpHeaders.LOCATION);

        assertNotNull(location);

        String uid = StringUtils.substringAfterLast(location, "/");
        assertEquals(36, uid.length());
    }

    @TestWithUser(username = "test", roles = "EMPLOYEE")
    void addArticle_shouldPersistProperly() throws Exception {
        mockMvc.perform(multipart(ARTICLES)
                .file(new MockMultipartFile("image", "test".getBytes()))
                .part(new MockPart("title", "title".getBytes()))
                .part(new MockPart("content", "content".getBytes())));

        assertEquals(1, articleRepository.count());
        assertEquals(1, articleImageRepository.count());
    }

    @Test
    void updateArticle_shouldReturn401_whenNotLoggedIn() throws Exception {
        mockMvc.perform(patch(ARTICLES + UID, 1))
                .andExpect(status().isUnauthorized());
    }

    @TestWithUser(username = "test")
    void updateArticle_shouldReturn403_whenHasNoPermission() throws Exception {
        var update = new UpdateArticleRequestBody(0, "title", "description", "content");
        var requestBody = objectMapper.writeValueAsString(update);

        mockMvc.perform(patch(ARTICLES + UID, 1)
                        .content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @TestWithUser(username = "test", roles = "EMPLOYEE")
    void updateArticle_shouldReturn404_whenArticleDoesNotExist() throws Exception {
        var update = new UpdateArticleRequestBody(0, "title", "description", "content");
        var requestBody = objectMapper.writeValueAsString(update);

        mockMvc.perform(patch(ARTICLES + UID, 1)
                        .content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @TestWithUser(username = "test", roles = "EMPLOYEE")
    @Sql({"/sqls/clear-all-tables-tests.sql", "/sqls/articles/add-sample-articles.sql"})
    void updateArticle_shouldReturn400_whenInvalidVersionIsProvided() throws Exception {
        var update = new UpdateArticleRequestBody(0, "title", "description", "content");
        var requestBody = objectMapper.writeValueAsString(update);

        mockMvc.perform(patch(ARTICLES + UID, 5)
                        .content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @TestWithUser(username = "test", roles = "EMPLOYEE")
    @Sql({"/sqls/clear-all-tables-tests.sql", "/sqls/articles/add-sample-articles.sql"})
    void updateArticle_shouldReturn204_whenRequestIsCorrect() throws Exception {
        var update = new UpdateArticleRequestBody(0, null, "test1-updated", null);
        var requestBody = objectMapper.writeValueAsString(update);

        mockMvc.perform(patch(ARTICLES + UID, 1)
                        .content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @TestWithUser(username = "test", roles = "EMPLOYEE")
    @Sql({"/sqls/clear-all-tables-tests.sql", "/sqls/articles/add-sample-articles.sql"})
    void updateArticle_shouldUpdateArticleProperly() throws Exception {
        var update = new UpdateArticleRequestBody(0, null, "test1-updated", null);
        var requestBody = objectMapper.writeValueAsString(update);

        mockMvc.perform(patch(ARTICLES + UID, 1)
                        .content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        ArticleEntity article = articleRepository.findById(1001L).orElseThrow();
        assertNotNull(article.getTitle());
        assertNotNull(article.getContent());
        assertEquals(1, article.getVersion());
        assertEquals("test1-updated", article.getDescription());
    }

    @Test
    void updateArticleImage_shouldReturn401_whenNotLoggedIn() throws Exception {
        mockMvc.perform(multipart(HttpMethod.PATCH, ARTICLES + UID + IMAGE, 1))
                .andExpect(status().isUnauthorized());
    }

    @TestWithUser(username = "test")
    void updateArticleImage_shouldReturn403_whenHasNoPermission() throws Exception {
        mockMvc.perform(multipart(HttpMethod.PATCH, ARTICLES + UID + IMAGE, 1)
                        .part(new MockPart("version", "0".getBytes())))
                .andExpect(status().isForbidden());
    }

    @TestWithUser(username = "test", roles = "EMPLOYEE")
    void updateArticleImage_shouldReturn404_whenArticleDoesNotExist() throws Exception {
        mockMvc.perform(multipart(HttpMethod.PATCH, ARTICLES + UID + IMAGE, 1)
                        .part(new MockPart("version", "0".getBytes())))
                .andExpect(status().isNotFound());
    }

    @TestWithUser(username = "test", roles = "EMPLOYEE")
    @Sql({"/sqls/clear-all-tables-tests.sql", "/sqls/articles/add-sample-articles.sql"})
    void updateArticleImage_shouldReturn400_whenInvalidVersionIsProvided() throws Exception {
        mockMvc.perform(multipart(HttpMethod.PATCH, ARTICLES + UID + IMAGE, 5)
                        .part(new MockPart("version", "0".getBytes())))
                .andExpect(status().isBadRequest());
    }

    @TestWithUser(username = "test", roles = "EMPLOYEE")
    @Sql({"/sqls/clear-all-tables-tests.sql", "/sqls/articles/add-sample-articles.sql"})
    void updateArticleImage_shouldReturn204_whenRequestIsCorrect() throws Exception {
        mockMvc.perform(multipart(HttpMethod.PATCH, ARTICLES + UID + IMAGE, 1)
                        .part(new MockPart("version", "0".getBytes())))
                .andExpect(status().isNoContent());
    }

    @TestWithUser(username = "test", roles = "EMPLOYEE")
    @Sql({"/sqls/clear-all-tables-tests.sql", "/sqls/articles/add-sample-articles.sql"})
    void updateArticleImage_shouldUpdateImageProperly() throws Exception {
        mockMvc.perform(multipart(HttpMethod.PATCH, ARTICLES + UID + IMAGE, 1)
                .file(new MockMultipartFile("image", "test".getBytes()))
                .part(new MockPart("version", "0".getBytes())));

        ArticleImageEntity image = articleRepository.findById(1001L).orElseThrow().getImage();
        assertArrayEquals("test".getBytes(), image.getContent());
        assertEquals(1, articleImageRepository.count());
    }

    @Test
    void deleteArticle_shouldReturn401_whenNotLoggedIn() throws Exception {
        mockMvc.perform(delete(ARTICLES + UID, 1))
                .andExpect(status().isUnauthorized());
    }

    @TestWithUser(username = "test")
    void deleteArticle_shouldReturn403_whenHasNoPermission() throws Exception {
        mockMvc.perform(delete(ARTICLES + UID, 1))
                .andExpect(status().isForbidden());
    }

    @TestWithUser(username = "test", roles = "EMPLOYEE")
    void deleteArticle_shouldReturn204_whenHasPermission() throws Exception {
        mockMvc.perform(delete(ARTICLES + UID, 1))
                .andExpect(status().isNoContent());
    }

    @TestWithUser(username = "test", roles = "EMPLOYEE")
    @Sql({"/sqls/clear-all-tables-tests.sql", "/sqls/articles/add-sample-article.sql"})
    void deleteArticle_shouldDeleteProperly() throws Exception {
        mockMvc.perform(delete(ARTICLES + UID, 1));

        assertEquals(0, articleRepository.count());
        assertEquals(0, articleImageRepository.count());
    }
}
