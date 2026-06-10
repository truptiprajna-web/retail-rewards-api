package com.retailer.rewards.controller;

import com.retailer.rewards.RewardsApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(classes = RewardsApiApplication.class)
@AutoConfigureMockMvc
class RewardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnRewardsForAllCustomers() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/rewards"))
                .andReturn();

        int statusCode = result.getResponse().getStatus();
        String response = result.getResponse().getContentAsString();
        int monthCount = response.split("\"month\":").length - 1;

        assertEquals(200, statusCode);
        assertEquals(10, monthCount);
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
