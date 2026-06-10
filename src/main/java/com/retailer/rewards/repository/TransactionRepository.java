package com.retailer.rewards.repository;

import com.retailer.rewards.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Provides database access for recorded customer purchases.
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Finds the most recent transaction in the complete data set.
     *
     * @return the most recent transaction, when data exists
     */
    Optional<Transaction> findTopByOrderByTransactionDateDesc();

    /**
     * Finds the most recent transaction for one customer.
     *
     * @param customerId customer identifier
     * @return the customer's most recent transaction, when the customer exists
     */
    Optional<Transaction> findTopByCustomerIdOrderByTransactionDateDesc(
            long customerId);

    /**
     * Finds all transactions inside an inclusive reporting period.
     *
     * @param startDate first date in the period
     * @param endDate last date in the period
     * @return transactions ordered by customer and date
     */
    List<Transaction>
            findByTransactionDateBetweenOrderByCustomerIdAscTransactionDateAsc(
                    LocalDate startDate, LocalDate endDate);

    /**
     * Finds one customer's transactions inside an inclusive reporting period.
     *
     * @param customerId customer identifier
     * @param startDate first date in the period
     * @param endDate last date in the period
     * @return transactions ordered by date
     */
    List<Transaction>
            findByCustomerIdAndTransactionDateBetweenOrderByTransactionDateAsc(
                    long customerId, LocalDate startDate, LocalDate endDate);
}
