DELETE
FROM coins;
DELETE
FROM authorities;
DELETE
FROM user_account;
DELETE
FROM users;

INSERT INTO user_account(id, username, first_name, last_name, email, phone_number, number_of_sends, number_of_receives,
                         active, send_limits, image_name)
VALUES (2000, 'testexist_coin', 'John', 'Doe', 'john.doe@example.com2', '12345678902', 0, 0, 1, 100,
        '9x5uWsChomR9y1TeA0ZxqNME5up4-PnD7k7uMF7J8S4=.png');
INSERT INTO user_account(id, username, first_name, last_name, email, phone_number, number_of_sends, number_of_receives,
                         active, send_limits, image_name)
VALUES (2001, 'employee', 'Jan', 'Kowalski', 'jan.kowalski@example.com2', '09812374652', 0, 0, 1, 100,
        '9x5uWsChomR9y1TeA0ZxqNME5up4-PnD7k7uMF7J8S4=.png');

INSERT INTO users(username, password, enabled)
VALUES ('testexist_coin', '$2a$10$sJwXPrpXwhksRfZyn.1G1ujS7USRqelh3kSkh8GWMeLXdbllVWBDG', true);
INSERT INTO users(username, password, enabled)
VALUES ('employee', '$2a$10$sJwXPrpXwhksRfZyn.1G1ujS7USRqelh3kSkh8GWMeLXdbllVWBDG', true);

INSERT INTO authorities(username, authority)
VALUES ('testexist_coin', 'ROLE_USER');
INSERT INTO authorities(username, authority)
VALUES ('employee', 'ROLE_EMPLOYEE');


INSERT INTO coins(id, uid, user_account_id, name, image_name, description, amount)
VALUES (2000, 'testexist-coin-1', 2000, 'Test Coin 1', '9x5uWsChomR9y1TeA0ZxqNME5up4-PnD7k7uMF7J8S4=.png',
        'Test Coin 1 Description', 100.00);

INSERT INTO coins(id, uid, user_account_id, name, image_name, description, amount)
VALUES (1001, 'testexist-coin-2', 2000, 'Test Coin 2', '9x5uWsChomR9y1TeA0ZxqNME5up4-PnD7k7uMF7J8S4=.png',
        'Test Coin 2 Description', 200.00);

INSERT INTO coins(id, uid, user_account_id, name, image_name, description, amount)
VALUES (1002, 'testexist-coin-3', 2000, 'Test Coin 3', '9x5uWsChomR9y1TeA0ZxqNME5up4-PnD7k7uMF7J8S4=.png',
        'Test Coin 3 Description', 300.00);

INSERT INTO coins(id, uid, user_account_id, name, image_name, description, amount)
VALUES (1003, 'testexist-coin-4', 2000, 'Test Coin 4', '9x5uWsChomR9y1TeA0ZxqNME5up4-PnD7k7uMF7J8S4=.png',
        'Test Coin 4 Description', 400.00);

INSERT INTO coins(id, uid, user_account_id, name, image_name, description, amount)
VALUES (1004, 'testdelete-coin-1', 2000, 'Test Coin 5', '9x5uWsChomR9y1TeA0ZxqNME5up4-PnD7k7uMF7J8S4=.png',
        'Test Coin 5 Description', 500.00);

