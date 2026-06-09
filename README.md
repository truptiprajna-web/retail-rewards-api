# Retail Rewards API

This is a simple Spring Boot REST API that calculates reward points for
customers based on their purchases.

## Reward rules

- 2 points for every dollar spent over $100.
- 1 point for every dollar spent between $50 and $100.
- Example: a $120 purchase earns 90 points.

## Technology

- Java 8
- Spring Boot 2.7.18
- Maven
- JUnit 5

## Run the project

```bash
mvn spring-boot:run
```

## REST endpoints

Get rewards for all customers:

```text
GET http://localhost:8080/api/rewards
```

Get rewards for one customer:

```text
GET http://localhost:8080/api/rewards/101
```

## Run tests

```bash
mvn test
```

The sample data is stored in `TransactionRepository`. It includes multiple
customers and transactions across three months. Month names and reward totals
are calculated from the transaction dates and amounts.
