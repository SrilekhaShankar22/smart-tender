-- Auth Service Schema: smart_tender_auth
-- Creates users, roles, user_roles, refresh_tokens tables

CREATE TABLE IF NOT EXISTS roles (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    email          VARCHAR(100) NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    first_name     VARCHAR(50),
    last_name      VARCHAR(50),
    enabled        BOOLEAN NOT NULL DEFAULT TRUE,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at     DATETIME,
    updated_at     DATETIME,
    INDEX idx_users_email (email)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    token      VARCHAR(512) NOT NULL UNIQUE,
    user_id    BIGINT NOT NULL,
    expires_at DATETIME NOT NULL,
    revoked    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME,
    CONSTRAINT fk_rt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_rt_token (token)
);

-- Seed default roles
INSERT IGNORE INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');

-- Seed default admin user (password: Admin@123)
INSERT IGNORE INTO users (email, password, first_name, last_name, enabled, email_verified)
VALUES ('admin@smarttender.com',
        '$2a$12$LWt1S/bLp0eNoKN.fQQK4.YqHGKlyHBdGBdPOipKdTBcvQF.6oKW6',
        'System', 'Admin', TRUE, TRUE);

-- Assign admin role
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'admin@smarttender.com' AND r.name = 'ROLE_ADMIN';
