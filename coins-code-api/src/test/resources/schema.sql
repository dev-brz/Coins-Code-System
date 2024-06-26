CREATE TABLE IF NOT EXISTS users
(
    username VARCHAR_IGNORECASE(50)  NOT NULL PRIMARY KEY,
    password VARCHAR_IGNORECASE(500) NOT NULL,
    enabled  BOOLEAN                 NOT NULL
);

CREATE TABLE IF NOT EXISTS authorities
(
    username  VARCHAR_IGNORECASE(50) NOT NULL,
    authority VARCHAR_IGNORECASE(50) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES users (username)
);
CREATE UNIQUE INDEX IF NOT EXISTS ix_auth_username ON authorities (username, authority);

CREATE TABLE IF NOT EXISTS user_account
(
    id                 bigint AUTO_INCREMENT PRIMARY KEY,
    username           VARCHAR(255) NOT NULL UNIQUE,
    first_name         VARCHAR(255),
    last_name          VARCHAR(255),
    email              VARCHAR(255) NOT NULL UNIQUE,
    phone_number       VARCHAR(255) NOT NULL UNIQUE,
    image_name         VARCHAR(255),
    number_of_sends    INT,
    number_of_receives INT,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active             boolean   DEFAULT TRUE,
    send_limits        INT
);
CREATE UNIQUE INDEX IF NOT EXISTS user_account_username ON user_account (username);

CREATE TABLE IF NOT EXISTS coins
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    uid             VARCHAR(36)  NOT NULL DEFAULT (UUID()),
    user_account_id BIGINT       NOT NULL,
    name            VARCHAR(255) NOT NULL,
    image_name      VARCHAR(255),
    description     VARCHAR(255) NOT NULL,
    amount          DECIMAL(19, 2)        DEFAULT 0,
    FOREIGN KEY (user_account_id) REFERENCES user_account (id)
);

CREATE TABLE IF NOT EXISTS transactions
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    number         VARCHAR(30)  NOT NULL UNIQUE,
    source_id      BIGINT       NOT NULL,
    dest_id        BIGINT       NOT NULL,
    source_coin_id BIGINT       NOT NULL,
    dest_coin_id   BIGINT       NOT NULL,
    amount         DECIMAL(19, 2) DEFAULT 0,
    status         VARCHAR(30)  NOT NULL,
    t_type         VARCHAR(30)  NOT NULL,
    created_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    description    VARCHAR(255) NOT NULL,
    FOREIGN KEY (source_id) REFERENCES user_account (id),
    FOREIGN KEY (dest_id) REFERENCES user_account (id),
    FOREIGN KEY (source_coin_id) REFERENCES coins (id),
    FOREIGN KEY (dest_coin_id) REFERENCES coins (id)
);

CREATE TABLE IF NOT EXISTS transaction_codes
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    code        INT,
    coin_id     BIGINT NOT NULL,
    owner_id    BIGINT NOT NULL,
    amount      DECIMAL(19, 4),
    expires_at  TIMESTAMP,
    description VARCHAR(255),
    CONSTRAINT fk_transaction_codes_coins FOREIGN KEY (coin_id) REFERENCES coins (id),
    CONSTRAINT fk_transaction_codes_user_account FOREIGN KEY (owner_id) REFERENCES user_account (id)
);

ALTER TABLE user_account
    ALTER COLUMN send_limits DECIMAL(19, 4) DEFAULT 100.00;

ALTER TABLE user_account
    ADD COLUMN IF NOT EXISTS current_send_limits DECIMAL(19, 4) DEFAULT 100.00;

CREATE TABLE IF NOT EXISTS article_images
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    content     BLOB(2M) NOT NULL
);

CREATE TABLE IF NOT EXISTS articles
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    uid         VARCHAR(36)  NOT NULL,
    version     INT NOT NULL,
    title       VARCHAR(128) NOT NULL,
    description VARCHAR(128),
    content     VARCHAR(128) NOT NULL,
    created_at  TIMESTAMP,
    image_id    BIGINT,
    CONSTRAINT fk_articles_article_images FOREIGN KEY (image_id) REFERENCES article_images(id)
);
