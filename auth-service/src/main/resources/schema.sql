CREATE TABLE user_credentials (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                username VARCHAR(255) NOT NULL,
                                email VARCHAR(255),
                                password VARCHAR(255) NOT NULL
);
