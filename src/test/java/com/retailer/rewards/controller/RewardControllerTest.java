package com.retailer.rewards.controller;

import com.retailer.rewards.exception.CustomerNotFoundException;
import com.retailer.rewards.model.CustomerReward;
import com.retailer.rewards.model.MonthlyReward;
import com.retailer.rewards.service.RewardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RewardController.class)
class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RewardService rewardService;

    @Test
    void shouldReturnAllCustomerRewards() throws Exception {
        CustomerReward reward = new CustomerReward(
                101,
                "Anita",
                Arrays.asList(new MonthlyReward("January 2026", 90)),
                90);
        when(rewardService.getAllCustomerRewards())
                .thenReturn(Collections.singletonList(reward));

        mockMvc.perform(get("/api/rewards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(101))
                .andExpect(jsonPath("$[0].totalPoints").value(90));
    }

    @Test
    void shouldReturnSpecificCustomerReward() throws Exception {
        CustomerReward reward = new CustomerReward(
                101,
                "Anita",
                Arrays.asList(new MonthlyReward("January 2026", 90)),
                90);
        when(rewardService.getCustomerReward(101)).thenReturn(reward);

        mockMvc.perform(get("/api/rewards/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Anita"));
    }

    @Test
    void shouldReturnNotFoundForUnknownCustomer() throws Exception {
        when(rewardService.getCustomerReward(999))
                .thenThrow(new CustomerNotFoundException(999));

        mockMvc.perform(get("/api/rewards/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Customer not found with id: 999"));
    }

    @Test
    void shouldRejectNonPositiveCustomerId() throws Exception {
        mockMvc.perform(get("/api/rewards/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Customer id must be positive"));
    }

    @Test
    void shouldRejectNonNumericCustomerId() throws Exception {
        mockMvc.perform(get("/api/rewards/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Customer id must be a number"));
    }
}
