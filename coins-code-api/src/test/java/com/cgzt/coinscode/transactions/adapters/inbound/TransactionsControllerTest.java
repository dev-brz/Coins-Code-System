package com.cgzt.coinscode.transactions.adapters.inbound;

import com.cgzt.coinscode.annotations.TestWithUser;
import com.cgzt.coinscode.coins.domain.ports.outbound.repository.CoinsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static com.cgzt.coinscode.transactions.adapters.inbound.TransactionsController.TRANSACTIONS;
import static com.cgzt.coinscode.transactions.adapters.inbound.TransactionsController.TRANSACTIONS_NUMBER;
import static com.cgzt.coinscode.transactions.adapters.inbound.TransactionsController.TRANSACTIONS_TOPUP;
import static java.math.RoundingMode.HALF_UP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value =
        {
                "/clear-all-tables-tests.sql",
                "/transactions-controller-tests.sql",
        }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class TransactionsControllerTest {

    @Autowired
    MockMvc mockMvc;

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
        mockMvc.perform(get(TRANSACTIONS_NUMBER, "TU-10000"))
                .andExpect(status().isOk());
    }

    @TestWithUser(username = "test_transaction")
    void testGetTransactionByNumber_NotFound() throws Exception {
        mockMvc.perform(get(TRANSACTIONS_NUMBER, "nonexistentCode"))
                .andExpect(status().isNotFound());
    }

    @TestWithUser(username = "test_transaction")
    void testTopUp() throws Exception {
        var requestBody = """
                {
                    "username": "test_transaction",
                    "amount": 100,
                    "description": "Test description",
                    "coinUid": "test_transaction_coin"
                }
                """;

        var expected = BigDecimal.valueOf(200.00).setScale(2, HALF_UP);

        mockMvc.perform(post(TRANSACTIONS_TOPUP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        var actual = coinsRepository.findByUid("test_transaction_coin").getAmount();

        assertEquals(expected, actual);
    }

    @TestWithUser(username = "test_transaction")
    void testTopUp_InvalidRequest() throws Exception {
        String requestBody = """
                {
                    "username": "test_transaction",
                    "amount": -100
                }
                """;

        mockMvc.perform(post(TRANSACTIONS_TOPUP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @TestWithUser(username = "test_transaction")
    void testTopUp_NotFound() throws Exception {
        String requestBody = """
                {
                    "coinUid": "nonexistentUid",
                    "username": "test_transaction",
                    "amount": 100,
                    "description": "Test description"
                }
                """;

        mockMvc.perform(post(TRANSACTIONS_TOPUP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }
}