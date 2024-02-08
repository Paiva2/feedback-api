CREATE TABLE IF NOT EXISTS tb_categories (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

DROP FUNCTION update_updated_at_user CASCADE;

CREATE  FUNCTION update_updated_at_tb()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_updated_at_users
    BEFORE UPDATE
    ON
        tb_users
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_tb();

CREATE TRIGGER update_updated_at_categories
    BEFORE UPDATE
    ON
        tb_categories
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_tb();