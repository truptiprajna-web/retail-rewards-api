package com.retailer.rewards.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Contains the reward points earned during one calendar month.
 */
@Getter
@AllArgsConstructor
public class MonthlyReward {

    private final String month;
    private final int points;
}
