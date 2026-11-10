-- RozgarOne — MySQL schema
-- Import: mysql -u root -p < schema.sql

CREATE DATABASE IF NOT EXISTS rozgarone
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE rozgarone;

CREATE TABLE IF NOT EXISTS admin_users (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  username      VARCHAR(60)  NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  name          VARCHAR(100) NOT NULL DEFAULT 'Administrator',
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS posts (
  id              INT AUTO_INCREMENT PRIMARY KEY,
  postid          VARCHAR(40)  NOT NULL,
  feed            ENUM('govtjob','currentaffairs','resultpost','dailynews') NOT NULL,
  job_title       VARCHAR(255) NOT NULL,
  job_content     MEDIUMTEXT   NOT NULL,
  publish_date    DATE         NOT NULL,
  formatted_date  VARCHAR(60)  NOT NULL DEFAULT '',
  expired_date    VARCHAR(60)  NULL,
  is_expired      TINYINT(1)   NOT NULL DEFAULT 0,
  is_deleted      TINYINT(1)   NOT NULL DEFAULT 0,
  created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_feed (feed, is_deleted),
  KEY idx_expired (is_expired, feed)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS filters (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  group_name    ENUM('Category','Profession','Qualification','Location') NOT NULL,
  category_name VARCHAR(120) NOT NULL,
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  KEY idx_group (group_name)
) ENGINE=InnoDB;

-- Which filter labels (Category / Profession / Qualification / Location values) a govt-job
-- post belongs to. getpostbyfilter.php returns the posts linked to the requested label.
CREATE TABLE IF NOT EXISTS post_labels (
  post_id   INT NOT NULL,
  filter_id INT NOT NULL,
  PRIMARY KEY (post_id, filter_id),
  KEY idx_filter (filter_id),
  CONSTRAINT fk_pl_post   FOREIGN KEY (post_id)   REFERENCES posts(id)   ON DELETE CASCADE,
  CONSTRAINT fk_pl_filter FOREIGN KEY (filter_id) REFERENCES filters(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS notifications (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  main_title   VARCHAR(180) NOT NULL,
  second_title VARCHAR(255) NOT NULL DEFAULT '',
  publish_date DATE NOT NULL,
  created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS app_settings (
  setting_key   VARCHAR(60) PRIMARY KEY,
  setting_value VARCHAR(255) NOT NULL DEFAULT ''
) ENGINE=InnoDB;

INSERT IGNORE INTO app_settings (setting_key, setting_value) VALUES
  ('admob_banner',       'ca-app-pub-3940256099942544/6300978111'),
  ('admob_interstitial', 'ca-app-pub-3940256099942544/1033173712'),
  ('admob_native',       'ca-app-pub-3940256099942544/2247696110'),
  ('admob_appopen',      'ca-app-pub-3940256099942544/9257395921');

-- Default login: admin / admin123
-- Regenerate hash: php -r "echo password_hash('admin123', PASSWORD_BCRYPT), PHP_EOL;"
INSERT IGNORE INTO admin_users (username, password_hash, name) VALUES
  ('admin', '$2y$10$e0MYzXyjpJS7Pd0RVvHwHe1HlCS4bZJ18JuywdEPqPBOJzWQO2IGW', 'Administrator');
