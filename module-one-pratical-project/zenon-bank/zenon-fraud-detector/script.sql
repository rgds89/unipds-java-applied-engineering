CREATE TABLE transactions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    step BIGINT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    origin_name VARCHAR(255) NOT NULL,
    origin_old_balance DECIMAL(19, 2) NOT NULL,
    origin_new_balance DECIMAL(19, 2) NOT NULL,
    destination_name VARCHAR(255) NOT NULL,
    destination_old_balance DECIMAL(19, 2) NOT NULL,
    destination_new_balance DECIMAL(19, 2) NOT NULL,
    is_fraud BOOLEAN NOT NULL,
    is_flagged_fraud BOOLEAN NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_transactions_origin_name (origin_name)
);
