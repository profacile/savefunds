CREATE TABLE bank_transactions (
    id BIGSERIAL PRIMARY KEY,
    entreprise_id BIGINT NOT NULL REFERENCES entreprises(id) ON DELETE CASCADE,
    financial_snapshot_id BIGINT REFERENCES financial_snapshots(id) ON DELETE SET NULL,
    transaction_date DATE NOT NULL,
    description TEXT NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    balance NUMERIC(19, 2),
    classification VARCHAR(64) NOT NULL CHECK (classification IN (
        'REMUNERATION',
        'RETRAIT_CC',
        'ACHAT_PRIVE_SUSPECTE',
        'CHARGE_PROFESSIONNELLE',
        'REMBOURSEMENT_DIRIGEANT',
        'INCERTAIN'
    )),
    review_status VARCHAR(64) NOT NULL CHECK (review_status IN (
        'AUTO_CLASSIFIED',
        'NEEDS_REVIEW',
        'CONFIRMED',
        'CORRECTED'
    )),
    confidence_score INTEGER,
    impacts_director_current_account BOOLEAN NOT NULL DEFAULT FALSE,
    director_current_account_impact NUMERIC(19, 2),
    ai_reason TEXT,
    reviewed_by_user_id BIGINT,
    reviewed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_bank_transactions_entreprise_date
    ON bank_transactions(entreprise_id, transaction_date DESC);

CREATE INDEX idx_bank_transactions_review_status
    ON bank_transactions(review_status);
