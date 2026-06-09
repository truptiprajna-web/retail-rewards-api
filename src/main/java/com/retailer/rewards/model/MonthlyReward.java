package com.retailer.rewards.model;

public class MonthlyReward {

    private final String month;
    private final int points;

    public MonthlyReward(String month, int points) {
        this.month = month;
        this.points = points;
    }

    public String getMonth() {
        return month;
    }

    public int getPoints() {
        return points;
    }
}
