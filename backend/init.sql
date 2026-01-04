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
('用户1', '111111','pwd111'),
('用户2', '222222','pwd222');
INSERT INTO images (user_id, title, path) VALUES
(1, '趴着的小狗', '/趴着的小狗.webp'),
(1, '樱花林中的高塔', '/樱花林中的高塔.webp'),
(1, '城市高塔', '/城市高塔.webp'),
(1, '森林别墅', '/森林别墅.webp'),
(1, '疯狂动物城2', '/疯狂动物城2.webp'),
(1, '龙猫', '/龙猫.webp'),
(2, '蓝色鹦鹉', '/蓝色鹦鹉.webp');
INSERT INTO labels (title, user_id) VALUES
('自然', 1),
('风景', 1),
('建筑', 1),
('动物', 1),
('动漫', 1),
('动物', 2),
('风景', 2),
('工作', 2);
INSERT INTO labelLinks (p_id, l_id) VALUES
(1, 4),
(2, 1),
(2, 2),
(2, 3),
(3, 3),
(4, 1),
(4, 3),
(5, 4),
(5, 5),
(6, 4),
(6, 5),
(7, 5),
(7, 6);