CREATE TABLE omok_moves (
    id INT AUTO_INCREMENT PRIMARY KEY, -- 고유한 ID (자동 증가)
    room_name VARCHAR(255) NOT NULL,  -- 방 이름
    player VARCHAR(255) NOT NULL,     -- 플레이어 이름
    x INT NOT NULL,                   -- 돌의 x 좌표
    y INT NOT NULL,                   -- 돌의 y 좌표
    stone_color INT NOT NULL,         -- 돌 색상 (예: 1=흑돌, 2=백돌)
    move_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- 돌이 놓인 시간
);

