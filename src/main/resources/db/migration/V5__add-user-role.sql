CREATE TYPE userRole AS ENUM ('ADMIN', 'USER');

ALTER TABLE tb_users ADD role userRole NOT NULL;