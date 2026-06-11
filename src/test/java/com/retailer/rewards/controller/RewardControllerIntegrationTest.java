package com.retailer.rewards.controller;

import com.retailer.rewards.RewardsApiApplication;
import com.retailer.rewards.model.Transaction;
import com.retailer.rewards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(classes = RewardsApiApplication.class)
@AutoConfigureMockMvc
class RewardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();

        transactionRepository.save(new Transaction(
                1L,
                1L,
                "John",
                120.0,
                LocalDate.of(2026, 1, 10)));

        transactionRepository.save(new Transaction(
                2L,
                1L,
                "John",
                190.0,
                LocalDate.of(2026, 2, 15)));

        transactionRepository.save(new Transaction(
                3L,
                1L,
                "John",
                75.0,
                LocalDate.of(2026, 3, 20)));
    }

    @Test
    void shouldReturnRewardsForCustomer() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/rewards/1"))
                .andReturn();

        int statusCode = result.getResponse().getStatus();
        String response = result.getResponse().getContentAsString();

        String expectedResponse =
                "{\"customerId\":1,\"customerName\":\"John\","
                        + "\"monthlyRewards\":["
                        + "{\"month\":\"November 2025\",\"points\":0},"
                        + "{\"month\":\"December 2025\",\"points\":0},"
                        + "{\"month\":\"January 2026\",\"points\":90},"
                        + "{\"month\":\"February 2026\",\"points\":230},"
                        + "{\"month\":\"March 2026\",\"points\":25}],"
                        + "\"totalPoints\":345}";

        assertEquals(200, statusCode);
        assertEquals(expectedResponse, response);
    }

    @Test
    void shouldReturnRewardsForAllCustomers() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/rewards"))
                .andReturn();

        int statusCode = result.getResponse().getStatus();
        String response = result.getResponse().getContentAsString();

        String expectedResponse =
                "[{\"customerId\":1,\"customerName\":\"John\","
                        + "\"monthlyRewards\":["
                        + "{\"month\":\"November 2025\",\"points\":0},"
                        + "{\"month\":\"December 2025\",\"points\":0},"
                        + "{\"month\":\"January 2026\",\"points\":90},"
                        + "{\"month\":\"February 2026\",\"points\":230},"
                        + "{\"month\":\"March 2026\",\"points\":25}],"
                        + "\"totalPoints\":345}]";

        assertEquals(200, statusCode);
        assertEquals(expectedResponse, response);
    }

    @Test
    void shouldReturnNotFoundForUnknownCustomer() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/rewards/999"))
                .andReturn();

        int statusCode = result.getResponse().getStatus();

        assertEquals(404, statusCode);
    }

    @Test
    void shouldRejectInvalidCustomerId() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/rewards/-1"))
                .andReturn();

        int statusCode = result.getResponse().getStatus();

        assertEquals(400, statusCode);
    }
}
