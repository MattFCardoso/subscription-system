CREATE TABLE subscriptions (
    subscription_id UUID PRIMARY KEY DEFAULT GEN_RANDOM_UUID(),
    user_id UUID NOT NULL,
    plan VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    expiration_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    renewal_attempts INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_subscriptions_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT chk_status CHECK (status IN ('ATIVA', 'SUSPENSA', 'CANCELADA', 'EXPIRADA')),
    CONSTRAINT chk_plan CHECK (plan IN ('BASICO', 'PREMIUM', 'FAMILIA'))
);

-- PERFORMANCE INDEXESc
CREATE INDEX idx_subscriptions_user_id ON subscriptions(user_id);
CREATE INDEX idx_subscriptions_status ON subscriptions(status);
CREATE INDEX idx_subscriptions_expiration_date ON subscriptions(expiration_date);

-- UNIQUE INDEX TO ENSURE A USER CAN HAVE ONLY ONE ACTIVE SUBSCRIPTION AT A TIME
CREATE UNIQUE INDEX uk_user_active_subscription
    ON subscriptions(user_id)
    WHERE status = 'ATIVA';