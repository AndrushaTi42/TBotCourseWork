-- liquibase formatted sql

-- changeset atikhomirov:1

CREATE TABLE notification_task(
  id BIGSERIAL PRIMARY KEY,
  chat_id BIGINT NOT NULL,
  text_message TEXT NOT NULL,
  date_time TIMESTAMP NOT NULL
);