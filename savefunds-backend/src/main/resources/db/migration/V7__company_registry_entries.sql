CREATE TABLE company_registry_entries (
    id BIGSERIAL PRIMARY KEY,
    enterprise_number VARCHAR(32) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    legal_form VARCHAR(80),
    status VARCHAR(80) NOT NULL,
    address VARCHAR(255),
    postal_code VARCHAR(20),
    city VARCHAR(120),
    nace_code VARCHAR(40),
    activity_label VARCHAR(255),
    active BOOLEAN NOT NULL,
    source VARCHAR(80) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_company_registry_entries_name
    ON company_registry_entries(name);

CREATE INDEX idx_company_registry_entries_city
    ON company_registry_entries(city);

CREATE INDEX idx_company_registry_entries_active
    ON company_registry_entries(active);
