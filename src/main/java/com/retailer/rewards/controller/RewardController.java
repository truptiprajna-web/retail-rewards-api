package com.retailer.rewards.controller;

import com.retailer.rewards.model.CustomerReward;
import com.retailer.rewards.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * Exposes REST endpoints for customer reward reports.
 */
@Validated
@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    /**
     * Returns reward reports for all customers.
     *
     * @return all customer reward reports
     */
    @GetMapping
    public List<CustomerReward> getAllCustomerRewards() {
        return rewardService.getAllCustomerRewards();
    }

    /**
     * Returns the reward report for a specific customer.
     *
     * @param customerId positive customer identifier
     * @return the customer's reward report
     */
    @GetMapping("/{customerId}")
    public CustomerReward getCustomerReward(
            @PathVariable @Min(value = 1, message = "Customer id must be positive")
            long customerId) {
        return rewardService.getCustomerReward(customerId);
    }
}
