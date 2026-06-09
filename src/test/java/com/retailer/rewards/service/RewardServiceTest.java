package com.retailer.rewards.service;

import com.retailer.rewards.exception.CustomerNotFoundException;
import com.retailer.rewards.model.CustomerReward;
import com.retailer.rewards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RewardServiceTest {

    private RewardService rewardService;

    @BeforeEach
    void setUp() {
        rewardService = new RewardService(new TransactionRepository());
    }

    @Test
    void shouldCalculatePointsForDifferentAmounts() {
        assertEquals(0, rewardService.calculatePoints(50));
        assertEquals(25, rewardService.calculatePoints(75));
        assertEquals(90, rewardService.calculatePoints(120));
        assertEquals(250, rewardService.calculatePoints(200));
    }

    @Test
    void shouldReturnMonthlyAndTotalPointsForCustomer() {
        CustomerReward reward = rewardService.getCustomerReward(101);

        assertEquals("Anita", reward.getCustomerName());
        assertEquals(3, reward.getMonthlyRewards().size());
        assertEquals(365, reward.getTotalPoints());
    }

    @Test
    void shouldThrowExceptionForUnknownCustomer() {
        assertThrows(CustomerNotFoundException.class,
                () -> rewardService.getCustomerReward(999));
    }
}
