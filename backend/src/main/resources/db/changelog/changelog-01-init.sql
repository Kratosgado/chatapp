-- liquibase formatted sql

-- changeset opencode:1 context:!prod
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(255)
);

-- changeset opencode:2 context:!prod
CREATE TABLE IF NOT EXISTS chat_rooms (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255),
    is_group BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP
);

-- changeset opencode:3
CREATE TABLE IF NOT EXISTS chat_room_members (
    id VARCHAR(255) PRIMARY KEY,
    chat_room_id VARCHAR(255),
    user_id VARCHAR(255),
    role VARCHAR(255) DEFAULT 'MEMBER',
    CONSTRAINT fk_crm_chat_room FOREIGN KEY (chat_room_id) REFERENCES chat_rooms (id),
    CONSTRAINT fk_crm_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- changeset opencode:4 context:!prod
CREATE TABLE IF NOT EXISTS messages (
    id VARCHAR(255) PRIMARY KEY,
    chat_room_id VARCHAR(255),
    sender_id VARCHAR(255),
    content VARCHAR(255) NOT NULL,
    sent_at TIMESTAMP,
    CONSTRAINT fk_msg_chat_room FOREIGN KEY (chat_room_id) REFERENCES chat_rooms (id),
    CONSTRAINT fk_msg_user FOREIGN KEY (sender_id) REFERENCES users (id)
);

-- changeset opencode:5 context:!prod
CREATE TABLE IF NOT EXISTS friend_requests (
    id VARCHAR(255) PRIMARY KEY,
    sender_id VARCHAR(255),
    receiver_id VARCHAR(255),
    status VARCHAR(255) DEFAULT 'PENDING',
    created_at TIMESTAMP,
    CONSTRAINT fk_fr_sender FOREIGN KEY (sender_id) REFERENCES users (id),
    CONSTRAINT fk_fr_receiver FOREIGN KEY (receiver_id) REFERENCES users (id)
);