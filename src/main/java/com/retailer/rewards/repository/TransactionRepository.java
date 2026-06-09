package com.retailer.rewards.repository;

import com.retailer.rewards.model.Transaction;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TransactionRepository {

    private final List<Transaction> transactions = Arrays.asList(
            new Transaction(1, 101, "Anita", 120, LocalDate.of(2026, 1, 10)),
            new Transaction(2, 101, "Anita", 75, LocalDate.of(2026, 1, 18)),
            new Transaction(3, 101, "Anita", 200, LocalDate.of(2026, 2, 5)),
            new Transaction(4, 101, "Anita", 50, LocalDate.of(2026, 3, 12)),
            new Transaction(5, 102, "Rahul", 95, LocalDate.of(2026, 1, 7)),
            new Transaction(6, 102, "Rahul", 130, LocalDate.of(2026, 2, 14)),
            new Transaction(7, 102, "Rahul", 45, LocalDate.of(2026, 3, 20)),
            new Transaction(8, 102, "Rahul", 110, LocalDate.of(2026, 3, 25))
    );

    public List<Transaction> findAll() {
        return transactions;
    }

    public List<Transaction> findByCustomerId(long customerId) {
        return transactions.stream()
                .filter(transaction -> transaction.getCustomerId() == customerId)
                .collect(Collectors.toList());
    }
}
