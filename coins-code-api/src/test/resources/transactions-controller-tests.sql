INSERT INTO user_account(id, username, first_name, last_name, email, phone_number, number_of_sends, number_of_receives,
                         active, send_limits, image_name)
VALUES (3000, 'test_transaction', 'John', 'Doe', 'john.doe@example.com3', '12345678902', 0, 0, 1, 100,
        '9x5uWsChomR9y1TeA0ZxqNME5up4-PnD7k7uMF7J8S4=.png');


INSERT INTO coins(id, uid, user_account_id, name, image_name, description, amount)
VALUES (2000, 'test_transaction_coin', 3000, 'Test Coin 1', '9x5uWsChomR9y1TeA0ZxqNME5up4-PnD7k7uMF7J8S4=.png',
        'Test Coin 1 Description', 100.00);

INSERT INTO transactions(id, number, source_id, dest_id, source_coin_id, dest_coin_id, amount, status, t_type,
                         description)
VALUES (10000, 'TU-10000', 3000, 3000, 2000, 2000, 10.00, 'APPROVED', 'TOP_UP', 'Test Transaction to up');