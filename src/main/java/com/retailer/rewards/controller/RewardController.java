package com.retailer.rewards.controller;

import com.retailer.rewards.model.CustomerReward;
import com.retailer.rewards.service.RewardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping
    public List<CustomerReward> getAllCustomerRewards() {
        return rewardService.getAllCustomerRewards();
    }

    @GetMapping("/{customerId}")
    public CustomerReward getCustomerReward(@PathVariable long customerId) {
        return rewardService.getCustomerReward(customerId);
    }
}
