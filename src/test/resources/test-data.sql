-- Очистка таблиц перед тестами
DELETE FROM transactions;
DELETE FROM products;
DELETE FROM users;

-- Тестовые пользователи из ТЗ
INSERT INTO users (id, name) VALUES
('cd515076-5d8a-44be-930e-8d4fcb79f42d', 'Invest 500 User'),
('d4a4d619-9a0c-4fc5-b0cb-76c49409546b', 'Top Saving User'),
('1f9b149c-6577-448a-bc94-16bea229b71a', 'Simple Credit User');

-- Дополнительные тестовые пользователи
INSERT INTO users (id, name) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Active User Test'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Transaction Sum Test'),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'No Transactions User'),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Multiple Rules User');

-- Продукты
INSERT INTO products (id, type, name) VALUES
('p1', 'DEBIT', 'Дебетовая карта'),
('p2', 'SAVING', 'Накопительный счет'),
('p3', 'INVEST', 'Инвестиционный счет'),
('p4', 'CREDIT', 'Кредитная карта'),
('p5', 'DEBIT', 'Дебетовая карта Премиум'),
('p6', 'CREDIT', 'Кредитная карта Премиум');

-- Транзакции для пользователя Invest 500
INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES
('t1', 'cd515076-5d8a-44be-930e-8d4fcb79f42d', 'p1', 'DEPOSIT', 50000),
('t2', 'cd515076-5d8a-44be-930e-8d4fcb79f42d', 'p2', 'DEPOSIT', 1500);

-- Транзакции для пользователя Top Saving
INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES
('t3', 'd4a4d619-9a0c-4fc5-b0cb-76c49409546b', 'p1', 'DEPOSIT', 60000),
('t4', 'd4a4d619-9a0c-4fc5-b0cb-76c49409546b', 'p1', 'WITHDRAW', 20000);

-- Транзакции для пользователя Simple Credit
INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES
('t5', '1f9b149c-6577-448a-bc94-16bea229b71a', 'p1', 'DEPOSIT', 200000),
('t6', '1f9b149c-6577-448a-bc94-16bea229b71a', 'p1', 'WITHDRAW', 150000);

-- Транзакции для Active User Test (5+ транзакций)
INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES
('t7', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'p1', 'DEPOSIT', 10000),
('t8', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'p1', 'WITHDRAW', 5000),
('t9', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'p1', 'DEPOSIT', 20000),
('t10', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'p1', 'WITHDRAW', 3000),
('t11', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'p1', 'DEPOSIT', 15000),
('t12', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'p4', 'DEPOSIT', 50000);

-- Транзакции для Transaction Sum Test
INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES
('t13', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'p1', 'DEPOSIT', 100000),
('t14', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'p1', 'WITHDRAW', 30000),
('t15', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'p2', 'DEPOSIT', 50000);

-- Транзакции для Multiple Rules User
INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES
('t16', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'p1', 'DEPOSIT', 300000),
('t17', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'p1', 'WITHDRAW', 50000),
('t18', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'p2', 'DEPOSIT', 2000);

-- Пользователь с кредитом для теста negate условий
INSERT INTO users (id, name) VALUES
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'Credit User With Negate Test');

-- Продукт кредита
INSERT INTO products (id, type, name) VALUES
('p7', 'CREDIT', 'Тестовая кредитная карта');

INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES
('t19', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'p7', 'DEPOSIT', 10000);