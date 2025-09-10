\connect paymentsdb;

DROP TABLE IF EXISTS ledger_entries CASCADE;
DROP TABLE IF EXISTS accounts CASCADE;
DROP TABLE IF EXISTS transfers CASCADE;
-- =====================================
-- Ledger Service Tables
-- =====================================

CREATE TABLE accounts (
                          id         BIGSERIAL PRIMARY KEY,
                          balance    NUMERIC(19, 2) NOT NULL,
                          version    BIGINT         NOT NULL,
                          created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE ledger_entries (
                                id          BIGSERIAL PRIMARY KEY,
                                transfer_id VARCHAR(64)    NOT NULL,
                                account_id  BIGINT         NOT NULL REFERENCES accounts (id),
                                amount      NUMERIC(19, 2) NOT NULL,
                                type        VARCHAR(10)    NOT NULL CHECK (type IN ('DEBIT', 'CREDIT')),
                                created_at  TIMESTAMP DEFAULT NOW()
);

-- ✅ enforce one DEBIT and one CREDIT per transfer
ALTER TABLE ledger_entries
    ADD CONSTRAINT uq_transfer_type UNIQUE (transfer_id, type);

-- helpful index for lookups by transfer_id
CREATE INDEX idx_ledger_entries_transfer_id ON ledger_entries (transfer_id);

-- index for querying by account_id
CREATE INDEX idx_ledger_entries_account ON ledger_entries (account_id);

-- =====================================
-- Transfer Service Table
-- =====================================

CREATE TABLE transfers (
                           id               BIGSERIAL PRIMARY KEY,
                           transfer_id      VARCHAR(64) NOT NULL UNIQUE,
                           from_account_id  BIGINT      NOT NULL,
                           to_account_id    BIGINT      NOT NULL,
                           amount           NUMERIC(19,2) NOT NULL,
                           status           VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED')),
                           idempotency_key  VARCHAR(64) NOT NULL UNIQUE,
                           created_at       TIMESTAMP DEFAULT NOW()
);
