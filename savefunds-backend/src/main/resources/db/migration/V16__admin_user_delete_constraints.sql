ALTER TABLE accountant_notes
    DROP CONSTRAINT IF EXISTS accountant_notes_accountant_id_fkey;
ALTER TABLE accountant_notes
    ADD CONSTRAINT accountant_notes_accountant_id_fkey
    FOREIGN KEY (accountant_id)
    REFERENCES users(id)
    ON DELETE CASCADE;

ALTER TABLE validation_decisions
    DROP CONSTRAINT IF EXISTS validation_decisions_requested_by_user_id_fkey;
ALTER TABLE validation_decisions
    ADD CONSTRAINT validation_decisions_requested_by_user_id_fkey
    FOREIGN KEY (requested_by_user_id)
    REFERENCES users(id)
    ON DELETE CASCADE;

ALTER TABLE validation_decisions
    DROP CONSTRAINT IF EXISTS validation_decisions_decided_by_accountant_id_fkey;
ALTER TABLE validation_decisions
    ADD CONSTRAINT validation_decisions_decided_by_accountant_id_fkey
    FOREIGN KEY (decided_by_accountant_id)
    REFERENCES users(id)
    ON DELETE SET NULL;

ALTER TABLE accountant_client_accesses
    DROP CONSTRAINT IF EXISTS accountant_client_accesses_accountant_id_fkey;
ALTER TABLE accountant_client_accesses
    ADD CONSTRAINT accountant_client_accesses_accountant_id_fkey
    FOREIGN KEY (accountant_id)
    REFERENCES users(id)
    ON DELETE CASCADE;

ALTER TABLE accountant_client_accesses
    DROP CONSTRAINT IF EXISTS accountant_client_accesses_decided_by_user_id_fkey;
ALTER TABLE accountant_client_accesses
    ADD CONSTRAINT accountant_client_accesses_decided_by_user_id_fkey
    FOREIGN KEY (decided_by_user_id)
    REFERENCES users(id)
    ON DELETE SET NULL;
