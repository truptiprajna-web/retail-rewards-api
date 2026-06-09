package com.retailer.rewards.model;

import java.time.LocalDate;

public class Transaction {

    private final long id;
    private final long customerId;
    private final String customerName;
    private final double amount;
    private final LocalDate transactionDate;

    public Transaction(long id, long customerId, String customerName,
                       double amount, LocalDate transactionDate) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    public long getId() {
        return id;
    }

    public long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }
}
