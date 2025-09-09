\connect paymentsdb;

-- Ledger Service tables
CREATE TABLE accounts
(
    id         SERIAL PRIMARY KEY,
    balance    NUMERIC(19, 2) NOT NULL,
    version    BIGINT         NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE ledger_entries
(
    id          SERIAL PRIMARY KEY,
    transfer_id VARCHAR(64)    NOT NULL UNIQUE,
    account_id  BIGINT         NOT NULL REFERENCES accounts (id),
    amount      NUMERIC(19, 2) NOT NULL,
    type        VARCHAR(10)    NOT NULL CHECK (type IN ('DEBIT', 'CREDIT')),
    created_at  TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_ledger_entries_account ON ledger_entries (account_id);

-- Transfer Service table
CREATE TABLE transfers (
                           id BIGSERIAL PRIMARY KEY,
                           transfer_id VARCHAR(64) NOT NULL UNIQUE,
                           from_account_id BIGINT NOT NULL,
                           to_account_id BIGINT NOT NULL,
                           amount NUMERIC(19,2) NOT NULL,
                           status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED')),
                           idempotency_key VARCHAR(64) NOT NULL UNIQUE,
                           created_at TIMESTAMP DEFAULT NOW()
);
