ALTER TABLE bnb_annual_accounts_lookups
    ADD COLUMN pdf_url VARCHAR(1000),
    ADD COLUMN csv_url VARCHAR(1000),
    ADD COLUMN latest_deposit_id VARCHAR(255),
    ADD COLUMN latest_reference VARCHAR(255),
    ADD COLUMN latest_model_name VARCHAR(255),
    ADD COLUMN latest_period_end_date VARCHAR(64),
    ADD COLUMN latest_deposit_date VARCHAR(64),
    ADD COLUMN deposits_count INTEGER;
