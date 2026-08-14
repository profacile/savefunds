ALTER TABLE situations_financieres
    ADD COLUMN IF NOT EXISTS ratio_ca_charges NUMERIC(5, 2);
