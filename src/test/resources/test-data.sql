-- Очистка таблиц перед тестами
DELETE FROM transactions;
DELETE FROM products;
DELETE FROM users;

-- Тестовые пользователи из ТЗ
INSERT INTO users (id, name) VALUES
('cd515076-5d8a-44be-930e-8d4fcb79f42d', 'Invest 500 User'),
('d4a4d619-9a0c-4fc5-b0cb-76c49409546b', 'Top Saving User'),
('1f9b149c-6577-448a-bc94-16bea229b71a', 'Simple Credit User');

-- Продукты
INSERT INTO products (id, type, name) VALUES
('p1', 'DEBIT', 'Дебетовая карта'),
('p2', 'SAVING', 'Накопительный счет'),
('p3', 'INVEST', 'Инвестиционный счет'),
('p4', 'CREDIT', 'Кредитная карта');

-- Транзакции для пользователя Invest 500
INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES
('t1', 'cd515076-5d8a-44be-930e-8d4fcb79f42d', 'p1', 'DEPOSIT', 50000),
('t2', 'cd515076-5d8a-44be-930e-8d4fcb79f42d', 'p2', 'DEPOSIT', 1500);

-- Транзакции для пользователя Top Saving
INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES
('t3', 'd4a4d619-9a0c-4fc5-b0cb-76c49409546b', 'p1', 'DEPOSIT', 60000),
('t4', 'd4a4d619-9a0c-4fc5-b0cb-76c49409546b', 'p1', 'WITHDRAWAL', 20000);

-- Транзакции для пользователя Simple Credit
INSERT INTO transactions (id, user_id, product_id, type, amount) VALUES
('t5', '1f9b149c-6577-448a-bc94-16bea229b71a', 'p1', 'DEPOSIT', 200000),
('t6', '1f9b149c-6577-448a-bc94-16bea229b71a', 'p1', 'WITHDRAWAL', 150000);