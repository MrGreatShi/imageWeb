-- Active: 1763280620260@@127.0.0.1@3306@imageWeb
CREATE DATABASE IF NOT EXISTS imageWeb;
USE imageWeb;
DROP TABLE IF EXISTS labelLinks;
DROP TABLE IF EXISTS labels;
DROP TABLE IF EXISTS images;
DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE KEY NOT NULL,
    email VARCHAR(255) UNIQUE KEY NOT NULL,
    password VARCHAR(255) NOT NULL,
    INDEX idx(id)
);
CREATE TABLE IF NOT EXISTS images
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    user_id   INT          NOT NULL,
    title     VARCHAR(255) NOT NULL,
    path      VARCHAR(255) NOT NULL,
    stored_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx(id)
);
CREATE TABLE IF NOT EXISTS labels(
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx(id)
);
CREATE TABLE IF NOT EXISTS labelLinks(
    p_id INT NOT NULL,
    l_id INT NOT NULL,
    PRIMARY KEY (p_id,l_id),
    FOREIGN KEY (p_id) REFERENCES images(id) ON DELETE CASCADE,
    FOREIGN KEY (l_id) REFERENCES labels(id) ON DELETE CASCADE,
    INDEX idx(p_id,l_id)
);
INSERT INTO users (username, email, password) VALUES
('user1', '111111','pwd111'),
('user2', '222222','pwd222');
INSERT INTO images (user_id, title, path) VALUES
(1, 'Image1', '/sample1.jpg'),
(1, 'Image2', '/sample2.jpg'),
(2, 'Image3', '/sample3.jpg');
INSERT INTO labels (title, user_id) VALUES
('Nature', 1),
('Vacation', 1),
('Work', 2);
INSERT INTO labelLinks (p_id, l_id) VALUES
(1, 1),
(1, 2),
(2, 1),
(3, 3);