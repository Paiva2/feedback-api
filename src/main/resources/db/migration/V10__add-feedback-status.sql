CREATE TYPE feedback_status AS ENUM ('LIVE', 'SUGGESTION', 'PLANNED', 'IN_PROGRESS');

ALTER TABLE tb_feedbacks ADD status feedback_status NOT NULL DEFAULT 'SUGGESTION';