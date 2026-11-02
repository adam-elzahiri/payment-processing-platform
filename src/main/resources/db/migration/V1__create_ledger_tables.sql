CREATE TABLE customer (
    id UUID PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE account (
    id UUID PRIMARY KEY,
    customer_id UUID REFERENCES customer(id) NOT NULL,
    balance NUMERIC(20,4) NOT NULL,
    currency_code char(3) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_account_customer_currency
        UNIQUE (customer_id, currency_code),
    CONSTRAINT ck_account_balance_nonnegative
        CHECK (balance >= 0)
);

CREATE TABLE transfer (
    id UUID PRIMARY KEY,
    source_account_id UUID REFERENCES account(id) NOT NULL,
    destination_account_id UUID REFERENCES account(id) NOT NULL,
    amount NUMERIC(20,4) NOT NULL,
    currency_code char(3) NOT NULL,
    status varchar(16) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_transfer_amount_positive
        CHECK (amount > 0),
    CONSTRAINT ck_transfer_accounts_differ
        CHECK (source_account_id <> destination_account_id),
    CONSTRAINT ck_transfer_status
        CHECK (status IN ('PENDING', 'POSTED', 'REJECTED', 'CANCELED'))
);

CREATE TABLE ledger_posting (
    id UUID PRIMARY KEY,
    transfer_id UUID REFERENCES transfer(id) NOT NULL,
    account_id UUID REFERENCES account(id) NOT NULL,
    amount NUMERIC(20,4) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_posting_amount_nonzero
        CHECK (amount <> 0)
);
