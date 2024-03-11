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
