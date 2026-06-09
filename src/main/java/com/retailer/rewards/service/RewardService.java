package com.retailer.rewards.service;

import com.retailer.rewards.exception.CustomerNotFoundException;
import com.retailer.rewards.model.CustomerReward;
import com.retailer.rewards.model.MonthlyReward;
import com.retailer.rewards.model.Transaction;
import com.retailer.rewards.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RewardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RewardService.class);
    private static final DateTimeFormatter MONTH_FORMAT =
            DateTimeFormatter.ofPattern("MMMM yyyy");

    private final TransactionRepository transactionRepository;

    public RewardService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<CustomerReward> getAllCustomerRewards() {
        Map<Long, List<Transaction>> transactionsByCustomer =
                transactionRepository.findAll().stream()
                        .collect(Collectors.groupingBy(
                                Transaction::getCustomerId,
                                LinkedHashMap::new,
                                Collectors.toList()));

        List<CustomerReward> rewards = new ArrayList<>();
        for (List<Transaction> transactions : transactionsByCustomer.values()) {
            rewards.add(createCustomerReward(transactions));
        }
        return rewards;
    }

    public CustomerReward getCustomerReward(long customerId) {
        List<Transaction> transactions =
                transactionRepository.findByCustomerId(customerId);

        if (transactions.isEmpty()) {
            throw new CustomerNotFoundException(customerId);
        }
        return createCustomerReward(transactions);
    }

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

    private CustomerReward createCustomerReward(List<Transaction> transactions) {
        Transaction firstTransaction = transactions.get(0);
        Map<YearMonth, Integer> pointsByMonth = new LinkedHashMap<>();

        for (Transaction transaction : transactions) {
            YearMonth month = YearMonth.from(transaction.getTransactionDate());
            int points = calculatePoints(transaction.getAmount());
            pointsByMonth.merge(month, points, Integer::sum);
        }

        List<MonthlyReward> monthlyRewards = new ArrayList<>();
        int totalPoints = 0;
        for (Map.Entry<YearMonth, Integer> entry : pointsByMonth.entrySet()) {
            monthlyRewards.add(new MonthlyReward(
                    entry.getKey().format(MONTH_FORMAT), entry.getValue()));
            totalPoints += entry.getValue();
        }

        LOGGER.info("Calculated {} points for customer {}",
                totalPoints, firstTransaction.getCustomerId());

        return new CustomerReward(
                firstTransaction.getCustomerId(),
                firstTransaction.getCustomerName(),
                monthlyRewards,
                totalPoints);
    }
}
