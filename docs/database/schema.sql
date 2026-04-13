CREATE DATABASE IF NOT EXISTS interview_db;
USE interview_db;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE interviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    job_role VARCHAR(50) NOT NULL,
    experience_level VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'IN_PROGRESS',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    interview_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    question_order INT DEFAULT 0,
    FOREIGN KEY (interview_id) REFERENCES interviews(id)
);

CREATE TABLE answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    interview_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(id),
    FOREIGN KEY (interview_id) REFERENCES interviews(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE feedbacks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    answer_id BIGINT NOT NULL UNIQUE,
    score INT NOT NULL,
    strengths TEXT,
    weaknesses TEXT,
    improved_answer TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (answer_id) REFERENCES answers(id)
);