use Omok;
drop table users;
drop table usersTrash;

CREATE TABLE users (
    id VARCHAR(50) PRIMARY KEY,
    nickname VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) DEFAULT '없음',
    phone_number VARCHAR(20) DEFAULT '010-1111-1111',
    birth_date VARCHAR(11) DEFAULT '2000/01/01',
    gender ENUM('남자', '여자') DEFAULT '남자',
    email VARCHAR(50) DEFAULT 'null@naver.com',
    zipcode VARCHAR(50) DEFAULT '11111',
    address VARCHAR(100) DEFAULT '없음',
    detailedAddress VARCHAR(100) DEFAULT '없음',
    profile_picture BLOB,
    profile_character INT DEFAULT 0,
    win INT DEFAULT 0,
    lose INT DEFAULT 0
);

CREATE TABLE usersTrash (
    id VARCHAR(50) PRIMARY KEY,
    nickname VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) DEFAULT '없음',
    phone_number VARCHAR(20) DEFAULT '010-1111-1111',
    birth_date VARCHAR(11) DEFAULT '2000/01/01',
    gender ENUM('남자', '여자') DEFAULT '남자',
    email VARCHAR(50) DEFAULT 'null@naver.com',
    zipcode VARCHAR(50) DEFAULT '11111',
    address VARCHAR(100) DEFAULT '없음',
    detailedAddress VARCHAR(100) DEFAULT '없음',
    profile_picture BLOB,
    profile_character INT DEFAULT 0,
    win INT DEFAULT 0,
    lose INT DEFAULT 0
);

