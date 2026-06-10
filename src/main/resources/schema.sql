DROP TABLE IF EXISTS purchase_transactions;

CREATE TABLE purchase_transactions (
    id BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL CHECK (amount >= 0),
    transaction_date DATE NOT NULL
);

CREATE INDEX idx_transaction_customer
    ON purchase_transactions (customer_id);

CREATE INDEX idx_transaction_date
    ON purchase_transactions (transaction_date);
