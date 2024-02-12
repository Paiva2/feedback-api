CREATE TYPE userRoleNew AS ENUM ('ADMIN', 'USER');

ALTER TABLE tb_users ADD role userRoleNew NOT NULL;