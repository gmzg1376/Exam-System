
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'STUDENT',
    avatar VARCHAR(255),
    email VARCHAR(100),
    phone VARCHAR(20),
    create_time DATETIME,
    update_time DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS exam (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    duration INT NOT NULL,
    start_time DATETIME,
    end_time DATETIME,
    status INT DEFAULT 0,
    random_question BOOLEAN DEFAULT FALSE,
    random_option BOOLEAN DEFAULT FALSE,
    description TEXT,
    create_time DATETIME,
    update_time DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    exam_id BIGINT,
    type INT NOT NULL,
    content TEXT NOT NULL,
    options TEXT,
    score INT DEFAULT 10,
    answer TEXT,
    knowledge_point VARCHAR(100),
    analysis TEXT,
    create_time DATETIME,
    update_time DATETIME,
    FOREIGN KEY (exam_id) REFERENCES exam(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS answer_sheet (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    exam_id BIGINT,
    submit_time DATETIME,
    score INT DEFAULT 0,
    status INT DEFAULT 0,
    spend_time BIGINT,
    screen_switch_count INT DEFAULT 0,
    create_time DATETIME,
    update_time DATETIME,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (exam_id) REFERENCES exam(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS answer_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sheet_id BIGINT,
    question_id BIGINT,
    user_answer TEXT,
    is_correct BOOLEAN DEFAULT FALSE,
    spend_time BIGINT,
    create_time DATETIME,
    FOREIGN KEY (sheet_id) REFERENCES answer_sheet(id),
    FOREIGN KEY (question_id) REFERENCES question(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS wrong_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    question_id BIGINT,
    wrong_count INT DEFAULT 1,
    last_wrong_time DATETIME,
    create_time DATETIME,
    update_time DATETIME,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (question_id) REFERENCES question(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

INSERT INTO user (username, password, role, create_time, update_time) VALUES 
('admin', '$2a$10$eHifBG8EkUrVmRVwQTuaHeXNxjTHErwRV9.uiN1tYshc43VFoqW/W', 'ADMIN', NOW(), NOW());