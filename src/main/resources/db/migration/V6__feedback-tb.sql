CREATE TABLE tb_feedbacks (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    details VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    fk_user_id UUID REFERENCES tb_users(id) ON DELETE CASCADE,
    fk_category_id UUID REFERENCES tb_categories(id) ON DELETE CASCADE NOT NULL
);

CREATE TRIGGER update_updated_at_feedbacks
    BEFORE UPDATE
    ON
        tb_feedbacks
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_tb();