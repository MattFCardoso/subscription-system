CREATE TABLE payments (
    payment_id UUID PRIMARY KEY DEFAULT RANDOM_UUID(),
    subscription_id UUID NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    attempt INTEGER NOT NULL,
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    error_message TEXT,

    CONSTRAINT fk_payments_subscriptions FOREIGN KEY (subscription_id) REFERENCES subscriptions(subscription_id),
    CONSTRAINT chk_status_payments CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED'))
);

    -- PERFORMANCE INDEX
    CREATE INDEX idx_payments_subscription_id ON payments(subscription_id);
    CREATE INDEX idx_payments_status ON payments(status);
    CREATE INDEX idx_payment_date ON payments(payment_date);