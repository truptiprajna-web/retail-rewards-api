package com.retailer.rewards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starts the retail rewards Spring Boot application.
 */
@SpringBootApplication
public class RewardsApiApplication {

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(RewardsApiApplication.class, args);
    }
}
