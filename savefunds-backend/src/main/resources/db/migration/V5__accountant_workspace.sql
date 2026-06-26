CREATE TABLE financial_obligations (
    id BIGSERIAL PRIMARY KEY,
    entreprise_id BIGINT NOT NULL REFERENCES entreprises(id),
    type VARCHAR(30) NOT NULL CHECK (type IN ('TVA', 'ONSS', 'PRECOMPTE', 'IMPOT_SOCIETES', 'AUTRE')),
    due_date DATE NOT NULL,
    estimated_amount NUMERIC(19, 2),
    status VARCHAR(20) NOT NULL CHECK (status IN ('OPEN', 'PAID', 'POSTPONED')),
    note TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_financial_obligations_entreprise_due
    ON financial_obligations(entreprise_id, status, due_date);

CREATE TABLE accountant_notes (
    id BIGSERIAL PRIMARY KEY,
    entreprise_id BIGINT NOT NULL REFERENCES entreprises(id),
    accountant_id BIGINT NOT NULL REFERENCES users(id),
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_accountant_notes_entreprise_updated
    ON accountant_notes(entreprise_id, updated_at DESC, created_at DESC);

CREATE TABLE validation_decisions (
    id BIGSERIAL PRIMARY KEY,
    entreprise_id BIGINT NOT NULL REFERENCES entreprises(id),
    requested_by_user_id BIGINT NOT NULL REFERENCES users(id),
    decided_by_accountant_id BIGINT REFERENCES users(id),
    decision_type VARCHAR(40) NOT NULL CHECK (decision_type IN ('RETRAIT_DIRIGEANT', 'PAIEMENT_FOURNISSEUR', 'DEPENSE_EXCEPTIONNELLE')),
    requested_amount NUMERIC(19, 2) NOT NULL,
    status VARCHAR(40) NOT NULL CHECK (status IN ('PENDING', 'APPROVED', 'APPROVED_WITH_CONDITION', 'CORRECTION_REQUESTED', 'POSTPONED', 'REJECTED')),
    condition_text TEXT,
    comment TEXT,
    decided_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_validation_decisions_entreprise_status
    ON validation_decisions(entreprise_id, status, created_at DESC);
