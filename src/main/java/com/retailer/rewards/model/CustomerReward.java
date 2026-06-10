package com.retailer.rewards.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Contains monthly reward results and the total for one customer.
 */
@Getter
@AllArgsConstructor
public class CustomerReward {

    private final long customerId;
    private final String customerName;
    private final List<MonthlyReward> monthlyRewards;
    private final int totalPoints;
}
