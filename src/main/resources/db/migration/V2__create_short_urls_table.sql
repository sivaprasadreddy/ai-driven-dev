CREATE TABLE short_urls (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    short_key VARCHAR(255) NOT NULL UNIQUE,
    original_url TEXT NOT NULL,
    is_private BOOLEAN NOT NULL DEFAULT FALSE,
    created_by_id BIGINT,
    click_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    FOREIGN KEY (created_by_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_short_urls_short_key ON short_urls(short_key);
CREATE INDEX idx_short_urls_is_private ON short_urls(is_private);
CREATE INDEX idx_short_urls_created_by_id ON short_urls(created_by_id);
CREATE INDEX idx_short_urls_created_at ON short_urls(created_at);
CREATE INDEX idx_short_urls_expires_at ON short_urls(expires_at);