-- Remove a constraint antiga de status
ALTER TABLE subscriptions DROP CONSTRAINT IF EXISTS chk_status;

-- Permite que start_date e expiration_date sejam NULL
ALTER TABLE subscriptions
    ALTER COLUMN start_date DROP NOT NULL,
    ALTER COLUMN expiration_date DROP NOT NULL;

-- Cria nova constraint que permite NULL para datas quando status = PAGAMENTO_PENDENTE
-- e exige datas NOT NULL quando status for ATIVA, CANCELADA ou SUSPENSA
ALTER TABLE subscriptions ADD CONSTRAINT chk_status_with_dates
CHECK (
    (status = 'PAGAMENTO_PENDENTE') OR
    (status IN ('ATIVA', 'CANCELADA', 'SUSPENSA') AND start_date IS NOT NULL AND expiration_date IS NOT NULL)
);

--- Remove a constraint antiga de status
ALTER TABLE subscriptions DROP CONSTRAINT IF EXISTS chk_status;

-- Permite que start_date e expiration_date sejam NULL
ALTER TABLE subscriptions
    ALTER COLUMN start_date DROP NOT NULL,
    ALTER COLUMN expiration_date DROP NOT NULL;
