DROP TABLE IF EXISTS purchase_transactions;

CREATE TABLE purchase_transactions (
    id BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount >= 0),
    transaction_date DATE NOT NULL
);

