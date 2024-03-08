INSERT INTO users(username, password, enabled) VALUES ('validUsername', '$2a$10$OBXooiyQ1n6sz2rblYNvhupBvGsmtAnKVer.N3ER/hnHOv1PN5c4e', true)
INSERT INTO authorities(username, authority) VALUES ('validUsername', 'USER')

INSERT INTO user_account(id,username, first_name, last_name, email, phone_number, number_of_sends, number_of_receives, active, send_limits, image_name) VALUES(1000,'testexist', 'John', 'Doe', 'john.doe@example.com', '1234567890', 0, 0, 1, 100,'9x5uWsChomR9y1TeA0ZxqNME5up4-PnD7k7uMF7J8S4=.png')
INSERT INTO user_account(id,username, first_name, last_name, email, phone_number, number_of_sends, number_of_receives, active, send_limits, image_name) VALUES(1001,'testdelete', 'Jan', 'Kowalski', 'jan.kowalski@example.com', '0981237465', 0, 0, 1, 100,'9x5uWsChomR9y1TeA0ZxqNME5up4-PnD7k7uMF7J8S4=.png')
INSERT INTO users(username, password, enabled) VALUES ('testexist', '$2a$10$sJwXPrpXwhksRfZyn.1G1ujS7USRqelh3kSkh8GWMeLXdbllVWBDG', true)
INSERT INTO users(username, password, enabled) VALUES ('testdelete', '$2a$10$sJwXPrpXwhksRfZyn.1G1ujS7USRqelh3kSkh8GWMeLXdbllVWBDG', true)
INSERT INTO authorities(username, authority) VALUES ('testexist', 'USER')
INSERT INTO authorities(username, authority) VALUES ('testdelete', 'USER')
