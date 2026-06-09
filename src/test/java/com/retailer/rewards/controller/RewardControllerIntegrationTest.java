package com.retailer.rewards.controller;

import com.retailer.rewards.RewardsApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = RewardsApiApplication.class)
@AutoConfigureMockMvc
class RewardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnRewardsForAllCustomers() throws Exception {
        mockMvc.perform(get("/api/rewards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("Anita"))
                .andExpect(jsonPath("$[0].totalPoints").value(365))
                .andExpect(jsonPath("$[1].customerName").value("Rahul"));
    }

    @Test
    void shouldReturnNotFoundForUnknownCustomer() throws Exception {
        mockMvc.perform(get("/api/rewards/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Customer not found with id: 999"));
    }
}
