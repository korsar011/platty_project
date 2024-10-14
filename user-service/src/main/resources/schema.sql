-- Schema for 'users' table
CREATE TABLE users (
                       id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                       username VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       firstName VARCHAR(255),
                       lastName VARCHAR(255),
                       email VARCHAR(255),
                       birthday DATE,
                       country VARCHAR(255),
                       oAuthSub VARCHAR(255),
                       image_url TEXT
);

-- Schema for 'user_languages' table
CREATE TABLE user_languages (
                                user_id BIGINT NOT NULL,
                                language VARCHAR(255),
                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_languages_user_id ON user_languages(user_id);