CREATE TABLE bnb_annual_accounts_lookups (
    id BIGSERIAL PRIMARY KEY,
    entreprise_id BIGINT NOT NULL REFERENCES entreprises(id) ON DELETE CASCADE,
    enterprise_number VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL CHECK (status IN ('FOUND', 'NOT_FOUND', 'UNAVAILABLE')),
    consult_url VARCHAR(1000) NOT NULL,
    xbrl_url VARCHAR(1000),
    source VARCHAR(255) NOT NULL,
    message TEXT,
    raw_metadata TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_bnb_lookup_entreprise_created_at
    ON bnb_annual_accounts_lookups(entreprise_id, created_at DESC);
