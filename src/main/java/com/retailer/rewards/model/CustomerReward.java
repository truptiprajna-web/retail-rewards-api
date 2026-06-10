package com.retailer.rewards.model;

import java.util.List;

/**
 * Contains monthly reward results and the total for one customer.
 */
public class CustomerReward {

    private final long customerId;
    private final String customerName;
    private final List<MonthlyReward> monthlyRewards;
    private final int totalPoints;

    /**
     * Creates a customer reward report.
     *
     * @param customerId customer identifier
     * @param customerName customer display name
     * @param monthlyRewards monthly reward results
     * @param totalPoints total points in the reporting period
     */
    public CustomerReward(long customerId, String customerName,
                          List<MonthlyReward> monthlyRewards, int totalPoints) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.monthlyRewards = monthlyRewards;
        this.totalPoints = totalPoints;
    }

    /**
     * @return customer identifier
     */
    public long getCustomerId() {
        return customerId;
    }

    /**
     * @return customer display name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @return reward results ordered from oldest to newest month
     */
    public List<MonthlyReward> getMonthlyRewards() {
        return monthlyRewards;
    }

    /**
     * @return total reward points in the reporting period
     */
    public int getTotalPoints() {
        return totalPoints;
    }
}
