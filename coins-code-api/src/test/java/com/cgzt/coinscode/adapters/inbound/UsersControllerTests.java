package com.cgzt.coinscode.adapters.inbound;

import com.cgzt.coinscode.domain.ports.inbound.UserLoginCommandHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static com.cgzt.coinscode.adapters.inbound.UsersController.LOGIN_URL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = "/user-controller-tests.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class UsersControllerTests {
    static String USERS_LOGIN_URL = "/users"+LOGIN_URL;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

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
}
