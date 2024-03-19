create table IF NOT EXISTS users(
	username varchar_ignorecase(50) not null primary key,
	password varchar_ignorecase(500) not null,
	enabled boolean not null
);

create table IF NOT EXISTS authorities (
	username varchar_ignorecase(50) not null,
	authority varchar_ignorecase(50) not null,
	constraint fk_authorities_users foreign key(username) references users(username)
);
create unique index IF NOT EXISTS ix_auth_username on authorities (username, authority);

CREATE TABLE IF NOT EXISTS user_account (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    username varchar(255) NOT NULL UNIQUE,
    first_name varchar(255),
    last_name varchar(255),
    email varchar(255) NOT NULL UNIQUE,
    phone_number varchar(255) NOT NULL UNIQUE,
    image_name varchar(255),
    number_of_sends int,
    number_of_receives int,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active boolean DEFAULT TRUE,
    send_limits int
);

CREATE UNIQUE INDEX IF NOT EXISTS user_account_username ON user_account (username);

CREATE TABLE IF NOT EXISTS coins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uid VARCHAR(255) DEFAULT CAST(UUID() as CHAR),
    user_account_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    image_name VARCHAR(255),
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(19,2) DEFAULT 0,
    FOREIGN KEY (user_account_id) REFERENCES user_account(id)
);

 CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(30) NOT NULL UNIQUE,
    source_id BIGINT NOT NULL,
    dest_id BIGINT NOT NULL,
    source_coin_id BIGINT NOT NULL,
    dest_coin_id BIGINT NOT NULL,
    amount DECIMAL(19,2) DEFAULT 0,
    status VARCHAR(30) NOT NULL,
    t_type VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(255) NOT NULL,
    FOREIGN KEY (source_id) REFERENCES user_account(id),
    FOREIGN KEY (dest_id) REFERENCES user_account(id),
    FOREIGN KEY (source_coin_id) REFERENCES coins(id),
    FOREIGN KEY (dest_coin_id) REFERENCES coins(id)
 );