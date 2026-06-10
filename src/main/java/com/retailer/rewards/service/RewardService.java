package com.retailer.rewards.service;

import com.retailer.rewards.exception.CustomerNotFoundException;
import com.retailer.rewards.model.CustomerReward;
import com.retailer.rewards.model.MonthlyReward;
import com.retailer.rewards.model.Transaction;
import com.retailer.rewards.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
@Service
public class RewardService {

    private static final DateTimeFormatter MONTH_FORMAT =
            DateTimeFormatter.ofPattern("MMMM yyyy");

    @Autowired
    private TransactionRepository transactionRepository;

    @Value("${rewards.period.months}")
    private int rewardPeriodMonths;

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

        Map<Long, List<Transaction>> transactionsByCustomer =
                new LinkedHashMap<>();

        for (Transaction transaction : transactions) {
            long customerId = transaction.getCustomerId();

            if (!transactionsByCustomer.containsKey(customerId)) {
                transactionsByCustomer.put(customerId, new ArrayList<>());
            }

            transactionsByCustomer.get(customerId).add(transaction);
        }

        List<CustomerReward> rewards = new ArrayList<>();
        for (List<Transaction> customerTransactions
                : transactionsByCustomer.values()) {
            rewards.add(createCustomerReward(
                    customerTransactions, periodEnd));
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

        return createCustomerReward(transactions, periodEnd);
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
        if (rewardPeriodMonths < 1) {
            throw new IllegalArgumentException(
                    "Reward period months must be greater than zero");
        }
        return periodEnd.minusMonths(rewardPeriodMonths - 1L).atDay(1);
    }

    private CustomerReward createCustomerReward(
            List<Transaction> transactions, YearMonth periodEnd) {
        Transaction firstTransaction = transactions.get(0);
        Map<YearMonth, Integer> pointsByMonth = new LinkedHashMap<>();

        YearMonth firstMonth =
                periodEnd.minusMonths(rewardPeriodMonths - 1L);
        for (int index = 0; index < rewardPeriodMonths; index++) {
            pointsByMonth.put(firstMonth.plusMonths(index), 0);
        }

        int totalPoints = 0;
        for (Transaction transaction : transactions) {
            YearMonth month = YearMonth.from(transaction.getTransactionDate());
            int points = calculatePoints(transaction.getAmount());
            pointsByMonth.merge(month, points, Integer::sum);
            totalPoints += points;
        }

        List<MonthlyReward> monthlyRewards = new ArrayList<>();
        for (Map.Entry<YearMonth, Integer> entry : pointsByMonth.entrySet()) {
            monthlyRewards.add(new MonthlyReward(
                    entry.getKey().format(MONTH_FORMAT),
                    entry.getValue()));
        }

        log.info("Calculated {} points for customer {}",
                totalPoints, firstTransaction.getCustomerId());

        return new CustomerReward(
                firstTransaction.getCustomerId(),
                firstTransaction.getCustomerName(),
                monthlyRewards,
                totalPoints);
    }
}
