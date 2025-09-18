use Omok;
drop table users;

CREATE TABLE users (
    id VARCHAR(50) PRIMARY KEY,
    nickname VARCHAR(50) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    birth_date VARCHAR(11) NOT NULL,
    gender ENUM('남자', '여자') NOT NULL,
    email VARCHAR(50) NOT NULL,
    zipcode VARCHAR(50) NOT NULL,
    address VARCHAR(100) NOT NULL,
    detailedAddress VARCHAR(100) NOT NULL,
    profile_picture BLOB,
    profile_character INT DEFAULT 0,
    win INT DEFAULT 0,
    lose INT DEFAULT 0
);