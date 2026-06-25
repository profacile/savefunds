CREATE TABLE IF NOT EXISTS financial_snapshots (
    id BIGSERIAL PRIMARY KEY,
    entreprise_id BIGINT NOT NULL,
    source VARCHAR(50) NOT NULL,
    source_reference VARCHAR(255),
    chiffre_affaires_mensuel NUMERIC(19, 2),
    charges_mensuelles NUMERIC(19, 2),
    tresorerie NUMERIC(19, 2),
    solde_compte_courant NUMERIC(19, 2),
    dettes_court_terme NUMERIC(19, 2),
    creances_clients NUMERIC(19, 2),
    duree_compte_courant_debiteur INTEGER,
    snapshot_date DATE NOT NULL,
    confidence_score INTEGER,
    warnings TEXT,
    missing_fields TEXT,
    raw_metadata TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_financial_snapshots_entreprise
        FOREIGN KEY (entreprise_id)
        REFERENCES entreprises(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_financial_snapshots_entreprise_date
    ON financial_snapshots(entreprise_id, snapshot_date DESC, created_at DESC);

CREATE TABLE IF NOT EXISTS import_jobs (
    id BIGSERIAL PRIMARY KEY,
    entreprise_id BIGINT NOT NULL,
    snapshot_id BIGINT,
    source VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    file_name VARCHAR(255),
    parser_version VARCHAR(100),
    summary TEXT,
    error_message TEXT,
    created_by_user_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_import_jobs_entreprise
        FOREIGN KEY (entreprise_id)
        REFERENCES entreprises(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_import_jobs_snapshot
        FOREIGN KEY (snapshot_id)
        REFERENCES financial_snapshots(id)
        ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_import_jobs_entreprise_created
    ON import_jobs(entreprise_id, created_at DESC);
