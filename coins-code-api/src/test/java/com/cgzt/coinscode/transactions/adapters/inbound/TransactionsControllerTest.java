package com.cgzt.coinscode.transactions.adapters.inbound;

import com.cgzt.coinscode.coins.domain.ports.outbound.repositories.CoinsRepository;
import com.cgzt.coinscode.core.annotations.TestWithUser;
import com.cgzt.coinscode.transactions.domain.ports.inbound.commands.TopUpCommandHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static com.cgzt.coinscode.transactions.adapters.inbound.TransactionsController.*;
import static java.math.RoundingMode.HALF_UP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(value = {
        "/sqls/clear-all-tables-tests.sql",
        "/sqls/transactions-controller-tests.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class TransactionsControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    CoinsRepository coinsRepository;

    @TestWithUser(username = "test_transaction")
    void testGetAllTransactions() throws Exception {
        mockMvc.perform(get(TRANSACTIONS)
                        .param("username", "test_transaction"))
                .andExpect(status().isOk());
    }

    @TestWithUser(username = "test_transaction")
    void testGetAllTransactions_NotFound() throws Exception {
        mockMvc.perform(get(TRANSACTIONS)
                        .param("username", "nonexistentUser"))
                .andExpect(status().isForbidden());
    }

    @TestWithUser(username = "test_transaction")
    void testGetAllTransactions_Forbidden() throws Exception {
        mockMvc.perform(get(TRANSACTIONS))
                .andExpect(status().isForbidden());
    }

    @TestWithUser(username = "emp", roles = "EMPLOYEE")
    void testGetAllTransactions_Employee() throws Exception {
        mockMvc.perform(get(TRANSACTIONS)
                        .param("username", "test_transaction"))
                .andExpect(status().isOk());
    }

    @TestWithUser(username = "test_transaction")
    void testGetTransactionByNumber() throws Exception {
        mockMvc.perform(get(TRANSACTIONS + NUMBER, "TU-10000"))
                .andExpect(status().isOk());
    }

    @TestWithUser(username = "test_transaction")
    void testGetTransactionByNumber_NotFound() throws Exception {
        mockMvc.perform(get(TRANSACTIONS + NUMBER, "nonexistentCode"))
                .andExpect(status().isNotFound());
    }

    @TestWithUser(username = "test_transaction")
    void testTopUp() throws Exception {
        var command = new TopUpCommandHandler.Command(
                "test_transaction_coin",
                "test_transaction",
                BigDecimal.valueOf(100),
                "Test description");
        var requestBody = objectMapper.writeValueAsString(command);

        var expected = BigDecimal.valueOf(200.00).setScale(2, HALF_UP);

        mockMvc.perform(post(TRANSACTIONS + TOPUP)
                        .content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var actual = coinsRepository.findByUid("test_transaction_coin").getAmount();

        assertEquals(expected, actual);
    }

    @TestWithUser(username = "test_transaction")
    void testTopUp_InvalidRequest() throws Exception {
        var command = new TopUpCommandHandler.Command(
                "test_transaction",
                "test",
                BigDecimal.valueOf(-100),
                "test");
        String requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(post(TRANSACTIONS + TOPUP)
                        .content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @TestWithUser(username = "test_transaction")
    void testTopUp_NotFound() throws Exception {
        var command = new TopUpCommandHandler.Command(
                "nonexistentUid",
                "test_transaction",
                BigDecimal.valueOf(100),
                "Test description");
        String requestBody = objectMapper.writeValueAsString(command);

        mockMvc.perform(post(TRANSACTIONS + TOPUP)
                        .content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}