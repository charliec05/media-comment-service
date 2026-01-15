CREATE TABLE comments (
  id BIGINT NOT NULL AUTO_INCREMENT,
  content_type VARCHAR(32) NOT NULL,
  content_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,

  parent_id BIGINT NULL,
  root_id BIGINT NULL,

  body TEXT NOT NULL,
  status TINYINT NOT NULL DEFAULT 0,

  like_count INT NOT NULL DEFAULT 0,
  reply_count INT NOT NULL DEFAULT 0,
  score DOUBLE NOT NULL DEFAULT 0,

  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  PRIMARY KEY (id),

  INDEX idx_content_parent_score (content_type, content_id, parent_id, status, score, id),
  INDEX idx_content_parent_time  (content_type, content_id, parent_id, status, created_at, id),
  INDEX idx_root_time            (root_id, status, created_at, id),
  INDEX idx_parent               (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
