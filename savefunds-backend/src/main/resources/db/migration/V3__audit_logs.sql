CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    entreprise_id BIGINT REFERENCES entreprises(id) ON DELETE SET NULL,
    user_id BIGINT NOT NULL,
    user_email VARCHAR(255) NOT NULL,
    action VARCHAR(80) NOT NULL,
    outcome VARCHAR(20) NOT NULL,
    resource_type VARCHAR(80) NOT NULL,
    resource_id BIGINT,
    details TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_entreprise_created_at ON audit_logs(entreprise_id, created_at DESC);
CREATE INDEX idx_audit_logs_user_created_at ON audit_logs(user_id, created_at DESC);
