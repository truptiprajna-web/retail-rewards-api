package com.retailer.rewards.service;

import com.retailer.rewards.exception.CustomerNotFoundException;
import com.retailer.rewards.model.CustomerReward;
import com.retailer.rewards.model.MonthlyReward;
import com.retailer.rewards.model.Transaction;
import com.retailer.rewards.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Calculates monthly and total reward points from stored transactions.
 */
@Service
public class RewardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RewardService.class);
    private static final DateTimeFormatter MONTH_FORMAT =
            DateTimeFormatter.ofPattern("MMMM yyyy");

    private final TransactionRepository transactionRepository;
    private final int rewardPeriodMonths;

    /**
     * Creates the reward service.
     *
     * @param transactionRepository transaction database access
     * @param rewardPeriodMonths number of months included in a reward report
     */
    public RewardService(
            TransactionRepository transactionRepository,
            @Value("${rewards.period.months}") int rewardPeriodMonths) {
        if (rewardPeriodMonths < 1) {
            throw new IllegalArgumentException(
                    "Reward period months must be greater than zero");
        }
        this.transactionRepository = transactionRepository;
        this.rewardPeriodMonths = rewardPeriodMonths;
    }

    /**
     * Calculates reward reports for all customers in the configured period.
     *
     * @return customer reward reports, or an empty list when no data exists
     */
    public List<CustomerReward> getAllCustomerRewards() {
        Optional<Transaction> latestTransaction =
                transactionRepository.findTopByOrderByTransactionDateDesc();

        if (!latestTransaction.isPresent()) {
            return new ArrayList<>();
        }

        YearMonth periodEnd = YearMonth.from(
                latestTransaction.get().getTransactionDate());
        LocalDate startDate = getPeriodStart(periodEnd);
        LocalDate endDate = periodEnd.atEndOfMonth();
        List<Transaction> transactions = transactionRepository
                .findByTransactionDateBetweenOrderByCustomerIdAscTransactionDateAsc(
                        startDate, endDate);

        Map<Long, RewardAccumulator> rewardsByCustomer = new LinkedHashMap<>();
        for (Transaction transaction : transactions) {
            RewardAccumulator accumulator = rewardsByCustomer.computeIfAbsent(
                    transaction.getCustomerId(),
                    customerId -> new RewardAccumulator(
                            customerId,
                            transaction.getCustomerName(),
                            periodEnd));
            accumulator.add(transaction);
        }

        List<CustomerReward> rewards = new ArrayList<>();
        for (RewardAccumulator accumulator : rewardsByCustomer.values()) {
            rewards.add(accumulator.toCustomerReward());
        }
        return rewards;
    }

    /**
     * Calculates the configured reward period for one customer.
     *
     * @param customerId customer identifier
     * @return monthly and total reward points
     * @throws CustomerNotFoundException when no customer exists for the ID
     */
    public CustomerReward getCustomerReward(long customerId) {
        Optional<Transaction> latestTransaction = transactionRepository
                .findTopByCustomerIdOrderByTransactionDateDesc(customerId);

        if (!latestTransaction.isPresent()) {
            throw new CustomerNotFoundException(customerId);
        }

        Transaction latest = latestTransaction.get();
        YearMonth periodEnd = YearMonth.from(latest.getTransactionDate());
        List<Transaction> transactions = transactionRepository
                .findByCustomerIdAndTransactionDateBetweenOrderByTransactionDateAsc(
                        customerId,
                        getPeriodStart(periodEnd),
                        periodEnd.atEndOfMonth());

        RewardAccumulator accumulator = new RewardAccumulator(
                customerId, latest.getCustomerName(), periodEnd);
        for (Transaction transaction : transactions) {
            accumulator.add(transaction);
        }
        return accumulator.toCustomerReward();
    }

    /**
     * Calculates points for a single purchase amount.
     *
     * @param amount purchase amount
     * @return earned points
     */
    public int calculatePoints(double amount) {
        int wholeAmount = (int) Math.floor(amount);
        int points = 0;

        if (wholeAmount > 100) {
            points += (wholeAmount - 100) * 2;
        }
        if (wholeAmount > 50) {
            points += Math.min(wholeAmount, 100) - 50;
        }
        return points;
    }

    private LocalDate getPeriodStart(YearMonth periodEnd) {
        return periodEnd.minusMonths(rewardPeriodMonths - 1L).atDay(1);
    }

    private final class RewardAccumulator {

        private final long customerId;
        private final String customerName;
        private final Map<YearMonth, Integer> pointsByMonth =
                new LinkedHashMap<>();
        private int totalPoints;

        private RewardAccumulator(
                long customerId, String customerName, YearMonth periodEnd) {
            this.customerId = customerId;
            this.customerName = customerName;

            YearMonth firstMonth =
                    periodEnd.minusMonths(rewardPeriodMonths - 1L);
            for (int index = 0; index < rewardPeriodMonths; index++) {
                pointsByMonth.put(firstMonth.plusMonths(index), 0);
            }
        }

        private void add(Transaction transaction) {
            YearMonth month = YearMonth.from(transaction.getTransactionDate());
            int points = calculatePoints(transaction.getAmount());
            pointsByMonth.merge(month, points, Integer::sum);
            totalPoints += points;
        }

        private CustomerReward toCustomerReward() {
            List<MonthlyReward> monthlyRewards = new ArrayList<>();
            for (Map.Entry<YearMonth, Integer> entry
                    : pointsByMonth.entrySet()) {
                monthlyRewards.add(new MonthlyReward(
                        entry.getKey().format(MONTH_FORMAT),
                        entry.getValue()));
            }

            LOGGER.info("Calculated {} points for customer {}",
                    totalPoints, customerId);
            return new CustomerReward(
                    customerId, customerName, monthlyRewards, totalPoints);
        }
    }
}
