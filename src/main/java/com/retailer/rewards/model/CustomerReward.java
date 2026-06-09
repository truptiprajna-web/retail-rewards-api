package com.retailer.rewards.model;

import java.util.List;

public class CustomerReward {

    private final long customerId;
    private final String customerName;
    private final List<MonthlyReward> monthlyRewards;
    private final int totalPoints;

    public CustomerReward(long customerId, String customerName,
                          List<MonthlyReward> monthlyRewards, int totalPoints) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.monthlyRewards = monthlyRewards;
        this.totalPoints = totalPoints;
    }

    public long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<MonthlyReward> getMonthlyRewards() {
        return monthlyRewards;
    }

    public int getTotalPoints() {
        return totalPoints;
    }
}
