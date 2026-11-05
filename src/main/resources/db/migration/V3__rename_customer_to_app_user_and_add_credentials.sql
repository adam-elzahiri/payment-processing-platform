ALTER TABLE account
    RENAME COLUMN customer_id TO user_id;

ALTER TABLE customer RENAME TO app_user;

-- noinspection SqlAddNotNullColumn
-- Adding warning suppression as there is no existing data yet, so we do not need to
-- worry about there being existing columns ending up with empty NOT NULL fields.
ALTER TABLE app_user
    ADD COLUMN email VARCHAR(320) UNIQUE NOT NULL,
    ADD COLUMN password_hash VARCHAR(255) NOT NULL,
    ADD CONSTRAINT ck_app_user_email_lowercase
        CHECK (email = lower(email));
