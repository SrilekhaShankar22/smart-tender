-- Profile Service Schema: smart_tender_profiles

CREATE TABLE IF NOT EXISTS saved_search (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id            BIGINT NOT NULL,
    name               VARCHAR(100) NOT NULL,
    keywords           VARCHAR(500),
    organisation       VARCHAR(300),
    category           VARCHAR(200),
    source_type        VARCHAR(20),
    state              VARCHAR(100),
    alert_enabled      BOOLEAN DEFAULT TRUE,
    alert_frequency    VARCHAR(20) DEFAULT 'DAILY' COMMENT 'INSTANT|DAILY|WEEKLY',
    created_at         DATETIME,
    updated_at         DATETIME,
    INDEX idx_ss_user (user_id)
);

CREATE TABLE IF NOT EXISTS notification_preferences (
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id                BIGINT NOT NULL UNIQUE,
    email_enabled          BOOLEAN DEFAULT TRUE,
    push_enabled           BOOLEAN DEFAULT FALSE,
    notification_frequency VARCHAR(20) DEFAULT 'DAILY',
    min_relevance_score    DOUBLE DEFAULT 0.5,
    notify_closing_soon    BOOLEAN DEFAULT TRUE,
    closing_soon_days      INT DEFAULT 3,
    INDEX idx_np_user (user_id)
);

CREATE TABLE IF NOT EXISTS user_profiles (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT NOT NULL UNIQUE,
    display_name VARCHAR(100),
    company      VARCHAR(200),
    phone        VARCHAR(20),
    city         VARCHAR(100),
    state        VARCHAR(100),
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME
);
