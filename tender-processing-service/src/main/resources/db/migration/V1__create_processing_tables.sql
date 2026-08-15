-- Processing Service Schema: smart_tender_processing

CREATE TABLE IF NOT EXISTS processed_tender (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    tender_id        VARCHAR(200) NOT NULL UNIQUE,
    content_hash     VARCHAR(64),
    title            VARCHAR(500),
    organisation_name VARCHAR(300),
    source_type      VARCHAR(20),
    tender_status    VARCHAR(20),
    relevance_score  DOUBLE DEFAULT 0.0,
    is_duplicate     BOOLEAN DEFAULT FALSE,
    closing_date     DATETIME,
    processed_at     DATETIME,
    es_indexed       BOOLEAN DEFAULT FALSE,
    INDEX idx_pt_tender_id (tender_id),
    INDEX idx_pt_hash      (content_hash),
    INDEX idx_pt_status    (tender_status)
);

CREATE TABLE IF NOT EXISTS processing_log (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    tender_id          VARCHAR(200),
    status             VARCHAR(20),
    error_message      TEXT,
    processing_time_ms BIGINT,
    created_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_pl_tender (tender_id),
    INDEX idx_pl_status (status)
);
