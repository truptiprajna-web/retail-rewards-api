package com.retailer.rewards.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

/**
 * Represents one recorded customer purchase stored in the database.
 */
@Entity
@Table(name = "purchase_transactions")
public class Transaction {

    @Id
    private long id;

    @Column(name = "customer_id", nullable = false)
    private long customerId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(nullable = false)
    private double amount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    protected Transaction() {
        // Required by JPA.
    }

    /**
     * Creates a transaction.
     *
     * @param id transaction identifier
     * @param customerId customer identifier
     * @param customerName customer display name
     * @param amount purchase amount
     * @param transactionDate purchase date
     */
    public Transaction(long id, long customerId, String customerName,
                       double amount, LocalDate transactionDate) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    /**
     * @return transaction identifier
     */
    public long getId() {
        return id;
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
     * @return purchase amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @return purchase date
     */
    public LocalDate getTransactionDate() {
        return transactionDate;
    }
}
