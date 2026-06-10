package com.retailer.rewards.exception;

/**
 * Indicates that no transactions exist for a requested customer ID.
 */
public class CustomerNotFoundException extends RuntimeException {

    /**
     * Creates the exception for a missing customer.
     *
     * @param customerId requested customer identifier
     */
    public CustomerNotFoundException(long customerId) {
        super("Customer not found with id: " + customerId);
    }
}
