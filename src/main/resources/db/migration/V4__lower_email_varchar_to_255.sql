-- In production, you'd of course need to handle existing emails longer than 255 characters.
-- However, I'm skipping that step as this is of course not in production.
ALTER TABLE app_user
    ALTER COLUMN email SET DATA TYPE VARCHAR(255);
