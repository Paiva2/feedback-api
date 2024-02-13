CREATE TABLE tb_answer (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    answer VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    fk_answering_to UUID REFERENCES tb_users(id) ON DELETE CASCADE,
    fk_user_id UUID REFERENCES tb_users(id) ON DELETE SET NULL,
    fk_comment_id UUID REFERENCES tb_comments(id) ON DELETE CASCADE
);

CREATE TRIGGER update_updated_at_comments
    BEFORE UPDATE
    ON
        tb_answer
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at_tb();