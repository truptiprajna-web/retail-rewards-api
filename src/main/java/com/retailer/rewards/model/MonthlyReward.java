package com.retailer.rewards.model;

/**
 * Contains the reward points earned during one calendar month.
 */
public class MonthlyReward {

    private final String month;
    private final int points;

    /**
     * Creates a monthly reward result.
     *
     * @param month formatted month and year
     * @param points points earned in the month
     */
    public MonthlyReward(String month, int points) {
        this.month = month;
        this.points = points;
    }

    /**
     * @return formatted month and year
     */
    public String getMonth() {
        return month;
    }

    /**
     * @return reward points for the month
     */
    public int getPoints() {
        return points;
    }
}
