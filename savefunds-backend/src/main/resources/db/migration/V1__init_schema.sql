CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS entreprises (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255),
    user_id BIGINT NOT NULL,
    raison_sociale VARCHAR(255) NOT NULL,
    numero_entreprise VARCHAR(255) NOT NULL,
    forme_juridique VARCHAR(255),
    secteur_activite VARCHAR(255),
    chiffre_affaires_mensuel NUMERIC(19, 2),
    charges_mensuelles NUMERIC(19, 2),
    tresorerie NUMERIC(19, 2),
    solde_compte_courant NUMERIC(19, 2),
    date_debut_debiteur_cc DATE,
    statut VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_entreprises_user_id ON entreprises(user_id);

CREATE TABLE IF NOT EXISTS analyses_prelevement (
    id BIGSERIAL PRIMARY KEY,
    entreprise_id BIGINT NOT NULL,
    montant_souhaite NUMERIC(19, 2) NOT NULL,
    statut VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_analyses_entreprise
        FOREIGN KEY (entreprise_id)
        REFERENCES entreprises(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_analyse_entreprise ON analyses_prelevement(entreprise_id);
CREATE INDEX IF NOT EXISTS idx_analyse_statut ON analyses_prelevement(statut);
CREATE INDEX IF NOT EXISTS idx_analyse_created ON analyses_prelevement(created_at);

CREATE TABLE IF NOT EXISTS resultats_analyse (
    id BIGSERIAL PRIMARY KEY,
    analyse_id BIGINT NOT NULL UNIQUE,
    decision_globale VARCHAR(50) NOT NULL,
    details_decision_globale TEXT,
    recommandation_globale TEXT,
    score_tresorerie NUMERIC(19, 2),
    score_ratio_ca_charges NUMERIC(5, 2),
    score_compte_courant_debiteur INTEGER,
    montant_max_prelevable NUMERIC(19, 2),
    decision_tresorerie VARCHAR(50),
    decision_ratio_ca_charges VARCHAR(50),
    decision_compte_courant VARCHAR(50),
    details_tresorerie TEXT,
    details_ratio_ca_charges TEXT,
    details_compte_courant TEXT,
    recommandation_tresorerie TEXT,
    recommandation_ratio_ca_charges TEXT,
    recommandation_compte_courant TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_resultats_analyse
        FOREIGN KEY (analyse_id)
        REFERENCES analyses_prelevement(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_resultat_analyse ON resultats_analyse(analyse_id);

CREATE TABLE IF NOT EXISTS situations_financieres (
    id BIGSERIAL PRIMARY KEY,
    entreprise_id BIGINT NOT NULL,
    chiffre_affaires_mensuel NUMERIC(19, 2) NOT NULL,
    charges_mensuelles NUMERIC(19, 2) NOT NULL,
    tresorerie NUMERIC(19, 2) NOT NULL,
    solde_compte_courant NUMERIC(19, 2) NOT NULL,
    ratio_ca_charges NUMERIC(5, 2),
    tresorerie_en_mois NUMERIC(19, 2),
    duree_compte_courant_debiteur INTEGER,
    captured_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    source VARCHAR(255),
    notes TEXT
);

CREATE INDEX IF NOT EXISTS idx_situations_entreprise_captured
    ON situations_financieres(entreprise_id, captured_at DESC);
