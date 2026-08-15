-- Notification Service Schema: smart_tender_notifications

CREATE TABLE IF NOT EXISTS notification_log (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    tender_id     VARCHAR(200),
    channel       VARCHAR(20) COMMENT 'EMAIL|PUSH|SMS',
    recipient     VARCHAR(200),
    subject       VARCHAR(500),
    status        VARCHAR(20) COMMENT 'PENDING|SENT|FAILED',
    error_message TEXT,
    retry_count   INT DEFAULT 0,
    sent_at       DATETIME,
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_nl_user      (user_id),
    INDEX idx_nl_tender    (tender_id),
    INDEX idx_nl_status    (status),
    INDEX idx_nl_created   (created_at)
);
