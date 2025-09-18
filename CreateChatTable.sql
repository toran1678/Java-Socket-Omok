DROP TABLE chat_logs;
CREATE TABLE chat_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nickname VARCHAR(50),
    room_name VARCHAR(50),
    message TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

