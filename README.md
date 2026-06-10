# Retail Rewards API

This Spring Boot REST API reads customer purchases from an H2 database and
calculates monthly and total reward points for a configurable reporting period.

## Reward rules

- 2 points for every dollar spent over $100.
- 1 point for every dollar spent between $50 and $100.
- Example: a $120 purchase earns 90 points.

## Technology

- Java 8
- Spring Boot 2.7.18
- Maven
- H2 Database
- Spring Data JPA
- Lombok
- JUnit 5
- Mockito
- JaCoCo

## Project structure

```text
src/main/java/com/retailer/rewards
|-- controller     REST endpoints and input validation
|-- exception      API errors and exception handling
|-- model          database entity and response models
|-- repository     Spring Data JPA database queries
`-- service        reward calculation and month grouping

src/main/resources
|-- application.properties   database and reward-period settings
|-- schema.sql               database table and indexes
`-- data.sql                 demonstration transactions
```

## Implementation

1. Spring loads the table definition from `schema.sql`.
2. Demonstration purchases are inserted from `data.sql`.
3. The service reads `rewards.period.months` from configuration.
4. The latest database transaction determines the end of the reporting period.
5. The start month is calculated from the configured number of months.
6. Transactions are grouped by customer and calendar month.
7. Missing months are included with zero points.
8. Monthly points are added to produce each customer's total.

Month names and totals are never hardcoded. For example, changing:

```properties
rewards.period.months=5
```

to:

```properties
rewards.period.months=3
```

produces a three-month report without changing Java code. Any positive number
can be used.

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

Example validation and negative cases:

```text
GET /api/rewards/999   returns 404 when the customer does not exist
GET /api/rewards/0     returns 400 because the ID must be positive
GET /api/rewards/abc   returns 400 because the ID must be numeric
```

Open the H2 database console:

```text
http://localhost:8080/h2-console
```

Use these connection values:

```text
JDBC URL: jdbc:h2:mem:rewardsdb
User Name: sa
Password: leave blank
```

## Run tests

```bash
mvn test
```

The tests cover:

- Reward thresholds and edge amounts
- Monthly and total calculations
- Three-month, five-month, and eight-month configurations
- Multiple customers with multiple transactions
- Empty database behavior
- Missing and invalid customer IDs
- Controller, service, and full H2 integration behavior

## Code coverage

Running `mvn test` also creates the JaCoCo coverage report:

```text
target/site/jacoco/index.html
```

The `target` directory is ignored by Git and should not be uploaded.
