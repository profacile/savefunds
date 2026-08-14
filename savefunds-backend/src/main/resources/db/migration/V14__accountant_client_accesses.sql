CREATE TABLE accountant_client_accesses (
    id BIGSERIAL PRIMARY KEY,
    accountant_id BIGINT NOT NULL REFERENCES users(id),
    entreprise_id BIGINT NOT NULL REFERENCES entreprises(id),
    status VARCHAR(30) NOT NULL CHECK (status IN ('PENDING', 'ACTIVE', 'REJECTED', 'REVOKED')),
    request_note TEXT,
    response_note TEXT,
    decided_by_user_id BIGINT REFERENCES users(id),
    decided_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT uk_accountant_client_access UNIQUE (accountant_id, entreprise_id)
);

CREATE INDEX idx_accountant_client_accesses_accountant_status
    ON accountant_client_accesses(accountant_id, status);

CREATE INDEX idx_accountant_client_accesses_company_status
    ON accountant_client_accesses(entreprise_id, status);
