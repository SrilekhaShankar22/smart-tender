-- Fetch Service Schema: smart_tender_fetch

CREATE TABLE IF NOT EXISTS fetch_log (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id         VARCHAR(100) NOT NULL,
    started_at     DATETIME NOT NULL,
    completed_at   DATETIME,
    status         VARCHAR(20) NOT NULL COMMENT 'RUNNING|SUCCESS|FAILED',
    pages_fetched  INT DEFAULT 0,
    tenders_found  INT DEFAULT 0,
    new_tenders    INT DEFAULT 0,
    error_message  TEXT,
    source_type    VARCHAR(20),
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_fl_started (started_at),
    INDEX idx_fl_status  (status)
);

CREATE TABLE IF NOT EXISTS fetched_tender (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    tender_id        VARCHAR(200) NOT NULL UNIQUE,
    content_hash     VARCHAR(64)  NOT NULL,
    tender_ref_no    VARCHAR(300),
    title            VARCHAR(500),
    organisation_name VARCHAR(300),
    detail_url       VARCHAR(1000),
    source_type      VARCHAR(20) NOT NULL COMMENT 'CENTRAL|STATE|GEM',
    published_date   DATETIME,
    closing_date     DATETIME,
    first_seen_at    DATETIME NOT NULL,
    fetch_log_id     BIGINT,
    CONSTRAINT fk_ft_log FOREIGN KEY (fetch_log_id) REFERENCES fetch_log(id),
    INDEX idx_ft_tender_id   (tender_id),
    INDEX idx_ft_hash        (content_hash),
    INDEX idx_ft_source      (source_type),
    INDEX idx_ft_closing     (closing_date)
);
