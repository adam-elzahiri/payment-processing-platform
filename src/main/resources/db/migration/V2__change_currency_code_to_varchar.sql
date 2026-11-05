ALTER TABLE account
    ALTER COLUMN currency_code TYPE VARCHAR(3);

ALTER TABLE transfer
    ALTER COLUMN currency_code TYPE VARCHAR(3);
