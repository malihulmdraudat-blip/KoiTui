-- FreeSlot Database Schema (PostgreSQL)
-- Postgres can't create/select a database from inside a script the way MySQL's
-- "CREATE DATABASE IF NOT EXISTS ... USE ..." does. Create the DB once, then
-- run this file against it, e.g.:
--   createdb freeslot_db
--   psql -U postgres -d freeslot_db -f schema.sql

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    student_id VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,   -- BCrypt hash, never plain text
    semester VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS routine (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    day VARCHAR(20) NOT NULL,      -- e.g. SATURDAY, SUNDAY, MONDAY...
    period INT NOT NULL,           -- e.g. 1 to 8
    course VARCHAR(100),
    room VARCHAR(50),
    CONSTRAINT unique_slot UNIQUE (user_id, day, period)
);

CREATE TABLE IF NOT EXISTS friends (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    friend_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20) DEFAULT 'ACCEPTED', -- kept simple for MVP: no pending requests yet
    CONSTRAINT unique_friend_pair UNIQUE (user_id, friend_id)
);
