CREATE TABLE tb_comments (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    comment VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    fk_user_id UUID REFERENCES tb_users(id) ON DELETE CASCADE,
    fk_feedback_id UUID REFERENCES tb_feedbacks(id) ON DELETE CASCADE
);

CREATE TRIGGER update_updated_at_comments
    BEFORE UPDATE
    ON
        tb_comments
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_tb();