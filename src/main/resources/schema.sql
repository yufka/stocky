CREATE SCHEMA IF NOT EXISTS stocky;

CREATE TABLE IF NOT EXISTS stocky.subscription_plan (
  id INTEGER NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  call_count BIGINT,
  call_time_limit_millis BIGINT,
  stock_count BIGINT,
  stock_time_limit_millis BIGINT,
  PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS subscription_name_UNIQUE ON stocky.subscription_plan(name);

CREATE TABLE IF NOT EXISTS stocky.api_user (
    id INTEGER  NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    subscription_id INTEGER NOT NULL,
    superuser BOOLEAN NOT NULL DEFAULT FALSE,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_plan TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT FK_USER_SUBSCRIPTION FOREIGN KEY (subscription_id) REFERENCES stocky.subscription_plan(id)
);

CREATE INDEX IF NOT EXISTS user_name_UNIQUE ON stocky.api_user(name);
CREATE INDEX IF NOT EXISTS name_password_idx ON stocky.api_user(name, password);


