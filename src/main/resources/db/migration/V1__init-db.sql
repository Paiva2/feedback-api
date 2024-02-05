CREATE TABLE IF NOT EXISTS tb_users (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    password VARCHAR(300) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    profile_picture_url VARCHAR(255) NOT NULL
);

CREATE  FUNCTION update_updated_at_user()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_user_updated_at
    BEFORE UPDATE
    ON
        tb_users
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_user();