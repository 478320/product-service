USE product_service;
SET NAMES utf8mb4;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE operation_log;
TRUNCATE TABLE review_record;
TRUNCATE TABLE publish_task;
TRUNCATE TABLE sku_attr_value;
TRUNCATE TABLE spu_attr_value;
TRUNCATE TABLE sku;
TRUNCATE TABLE spu;
TRUNCATE TABLE banned_word;
TRUNCATE TABLE category_attribute;
TRUNCATE TABLE category;
TRUNCATE TABLE brand;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO brand(name, priority, description, is_deleted) VALUES
(CONVERT(0xE5B7B4E9BB8EE4B896E5AEB6 USING utf8mb4), 100, 'Balenciaga sample brand', 0),
(CONVERT(0xE58FA4E9A9B0 USING utf8mb4), 90, 'Gucci sample brand', 0),
(CONVERT(0xE9A699E5A588E584BF USING utf8mb4), 88, 'Chanel sample brand', 0),
(CONVERT(0xE699AEE68B89E8BEBE USING utf8mb4), 85, 'Prada sample brand', 0),
(CONVERT(0xE8BFAAE5A5A5 USING utf8mb4), 82, 'Dior sample brand', 0),
(CONVERT(0xE58D9AE69F8FE588A9 USING utf8mb4), 80, 'Burberry sample brand', 0),
(CONVERT(0xE89186E89DB6E5AEB6 USING utf8mb4), 78, 'Bottega sample brand', 0),
(CONVERT(0xE58D8EE4BCA6E5A4A9E5A5B4 USING utf8mb4), 76, 'Valentino sample brand', 0);

INSERT INTO category(name, parent_id, is_deleted) VALUES
(CONVERT(0xE8BF9EE8A1A3E8A399 USING utf8mb4), 0, 0),
(CONVERT(0xE58D8AE8BAABE8A399 USING utf8mb4), 0, 0),
(CONVERT(0xE8A1ACE8A1AB USING utf8mb4), 0, 0),
(CONVERT(0xE5A496E5A597 USING utf8mb4), 0, 0),
(CONVERT(0xE99E8BE99DB4 USING utf8mb4), 0, 0),
(CONVERT(0xE6898BE8A28B USING utf8mb4), 0, 0);

INSERT INTO category_attribute(category_id, attr_name, attr_scope, required_flag, data_type, is_deleted) VALUES
(1, CONVERT(0xE69D90E8B4A8 USING utf8mb4), 'SPU', 1, 'TEXT', 0),
(1, CONVERT(0xE9A38EE6A0BC USING utf8mb4), 'SPU', 0, 'TEXT', 0),
(1, CONVERT(0xE9A29CE889B2 USING utf8mb4), 'SKU', 1, 'TEXT', 0),
(1, CONVERT(0xE5B0BAE7A081 USING utf8mb4), 'SKU', 1, 'TEXT', 0),
(2, CONVERT(0xE69D90E8B4A8 USING utf8mb4), 'SPU', 1, 'TEXT', 0),
(2, CONVERT(0xE9A29CE889B2 USING utf8mb4), 'SKU', 1, 'TEXT', 0),
(2, CONVERT(0xE5B0BAE7A081 USING utf8mb4), 'SKU', 1, 'TEXT', 0),
(3, CONVERT(0xE69D90E8B4A8 USING utf8mb4), 'SPU', 1, 'TEXT', 0),
(3, CONVERT(0xE9A29CE889B2 USING utf8mb4), 'SKU', 1, 'TEXT', 0),
(3, CONVERT(0xE5B0BAE7A081 USING utf8mb4), 'SKU', 1, 'TEXT', 0),
(4, CONVERT(0xE69D90E8B4A8 USING utf8mb4), 'SPU', 1, 'TEXT', 0),
(4, CONVERT(0xE9A29CE889B2 USING utf8mb4), 'SKU', 1, 'TEXT', 0),
(4, CONVERT(0xE5B0BAE7A081 USING utf8mb4), 'SKU', 1, 'TEXT', 0),
(5, CONVERT(0xE69D90E8B4A8 USING utf8mb4), 'SPU', 1, 'TEXT', 0),
(5, CONVERT(0xE9A29CE889B2 USING utf8mb4), 'SKU', 1, 'TEXT', 0),
(5, CONVERT(0xE5B0BAE7A081 USING utf8mb4), 'SKU', 1, 'TEXT', 0),
(6, CONVERT(0xE69D90E8B4A8 USING utf8mb4), 'SPU', 1, 'TEXT', 0),
(6, CONVERT(0xE9A29CE889B2 USING utf8mb4), 'SKU', 1, 'TEXT', 0),
(6, CONVERT(0xE5B0BAE7A081 USING utf8mb4), 'SKU', 1, 'TEXT', 0);

INSERT INTO banned_word(word, enabled, is_deleted) VALUES
('political-banned', 1, 0),
('terror-banned', 1, 0),
('vulgar-banned', 1, 0),
('fake-promise', 1, 0);

INSERT INTO spu(
    title, brand_id, category_id, description, publish_status, publish_strategy,
    scheduled_publish_time, reject_reason, is_deleted
)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 500
)
SELECT
    CASE
        WHEN n <= 120 THEN CONCAT((SELECT name FROM brand WHERE id = 1), (SELECT name FROM category WHERE id = 1), ' ', LPAD(n, 3, '0'))
        WHEN MOD(n, 6) = 1 THEN CONCAT((SELECT name FROM brand WHERE id = 2), (SELECT name FROM category WHERE id = 2), ' ', LPAD(n, 3, '0'))
        WHEN MOD(n, 6) = 2 THEN CONCAT((SELECT name FROM brand WHERE id = 3), (SELECT name FROM category WHERE id = 3), ' ', LPAD(n, 3, '0'))
        WHEN MOD(n, 6) = 3 THEN CONCAT((SELECT name FROM brand WHERE id = 4), (SELECT name FROM category WHERE id = 4), ' ', LPAD(n, 3, '0'))
        WHEN MOD(n, 6) = 4 THEN CONCAT((SELECT name FROM brand WHERE id = 5), (SELECT name FROM category WHERE id = 5), ' ', LPAD(n, 3, '0'))
        WHEN MOD(n, 6) = 5 THEN CONCAT((SELECT name FROM brand WHERE id = 6), (SELECT name FROM category WHERE id = 6), ' ', LPAD(n, 3, '0'))
        ELSE CONCAT((SELECT name FROM brand WHERE id = 8), (SELECT name FROM category WHERE id = 1), ' ', LPAD(n, 3, '0'))
    END AS title,
    CASE
        WHEN n <= 120 THEN 1
        ELSE MOD(n, 8) + 1
    END AS brand_id,
    MOD(n - 1, 6) + 1 AS category_id,
    CONCAT(
        'sample product for publish and search experiment. brand=',
        CASE
            WHEN n <= 120 THEN (SELECT name FROM brand WHERE id = 1)
            WHEN MOD(n, 8) = 0 THEN (SELECT name FROM brand WHERE id = 8)
            WHEN MOD(n, 8) = 1 THEN (SELECT name FROM brand WHERE id = 1)
            WHEN MOD(n, 8) = 2 THEN (SELECT name FROM brand WHERE id = 2)
            WHEN MOD(n, 8) = 3 THEN (SELECT name FROM brand WHERE id = 3)
            WHEN MOD(n, 8) = 4 THEN (SELECT name FROM brand WHERE id = 4)
            WHEN MOD(n, 8) = 5 THEN (SELECT name FROM brand WHERE id = 5)
            WHEN MOD(n, 8) = 6 THEN (SELECT name FROM brand WHERE id = 6)
            ELSE (SELECT name FROM brand WHERE id = 7)
        END,
        ', category=',
        CASE MOD(n - 1, 6) + 1
            WHEN 1 THEN (SELECT name FROM category WHERE id = 1)
            WHEN 2 THEN (SELECT name FROM category WHERE id = 2)
            WHEN 3 THEN (SELECT name FROM category WHERE id = 3)
            WHEN 4 THEN (SELECT name FROM category WHERE id = 4)
            WHEN 5 THEN (SELECT name FROM category WHERE id = 5)
            ELSE (SELECT name FROM category WHERE id = 6)
        END,
        ', verify brand-first ranking.'
    ) AS description,
    CASE
        WHEN n <= 380 THEN 'PUBLISHED'
        WHEN n <= 430 THEN 'WAITING_PUBLISH'
        WHEN n <= 460 THEN 'PENDING_REVIEW'
        WHEN n <= 480 THEN 'REVIEW_REJECTED'
        ELSE 'DRAFT'
    END AS publish_status,
    CASE
        WHEN MOD(n, 3) = 0 THEN 'IMMEDIATE'
        WHEN MOD(n, 3) = 1 THEN 'SCHEDULED'
        ELSE 'MANUAL_AFTER_REVIEW'
    END AS publish_strategy,
    CASE
        WHEN n > 380 AND n <= 430 THEN DATE_ADD(NOW(), INTERVAL (MOD(n, 60) - 30) MINUTE)
        ELSE NULL
    END AS scheduled_publish_time,
    CASE
        WHEN n > 460 AND n <= 480 THEN 'seed rejected: bad image'
        ELSE NULL
    END AS reject_reason,
    0
FROM seq;

INSERT INTO sku(
    spu_id, sku_code, sku_name, price, stock, sales, sku_status, is_deleted
)
SELECT
    s.id,
    CONCAT('SKU', LPAD(s.id, 6, '0')),
    CONCAT(s.title, ' standard'),
    ROUND(699 + MOD(s.id, 80) * 30 + CASE WHEN s.brand_id = 1 THEN 1200 ELSE 0 END, 2),
    100 + MOD(s.id, 260),
    CASE
        WHEN s.publish_status = 'PUBLISHED' THEN 5000 - s.id
        ELSE MOD(s.id, 300)
    END,
    CASE
        WHEN s.publish_status = 'PUBLISHED' THEN 'ON_SALE'
        WHEN s.publish_status = 'OFF_SHELF' THEN 'OFF_SHELF'
        ELSE 'DRAFT'
    END,
    0
FROM spu s;

INSERT INTO spu_attr_value(spu_id, attr_name, attr_value, is_deleted)
SELECT id,
       CONVERT(0xE69D90E8B4A8 USING utf8mb4),
       CASE
           WHEN MOD(id, 4) = 0 THEN 'wool'
           WHEN MOD(id, 4) = 1 THEN 'silk'
           WHEN MOD(id, 4) = 2 THEN 'cotton'
           ELSE 'leather'
       END,
       0
FROM spu;

INSERT INTO spu_attr_value(spu_id, attr_name, attr_value, is_deleted)
SELECT id,
       CONVERT(0xE9A38EE6A0BC USING utf8mb4),
       CASE
           WHEN MOD(id, 5) = 0 THEN 'office'
           WHEN MOD(id, 5) = 1 THEN 'party'
           WHEN MOD(id, 5) = 2 THEN 'casual'
           WHEN MOD(id, 5) = 3 THEN 'minimal'
           ELSE 'retro'
       END,
       0
FROM spu;

INSERT INTO sku_attr_value(sku_id, attr_name, attr_value, is_deleted)
SELECT id,
       CONVERT(0xE9A29CE889B2 USING utf8mb4),
       CASE
           WHEN MOD(id, 6) = 0 THEN 'black'
           WHEN MOD(id, 6) = 1 THEN 'white'
           WHEN MOD(id, 6) = 2 THEN 'beige'
           WHEN MOD(id, 6) = 3 THEN 'red'
           WHEN MOD(id, 6) = 4 THEN 'blue'
           ELSE 'green'
       END,
       0
FROM sku;

INSERT INTO sku_attr_value(sku_id, attr_name, attr_value, is_deleted)
SELECT id,
       CONVERT(0xE5B0BAE7A081 USING utf8mb4),
       CASE
           WHEN MOD(id, 4) = 0 THEN 'S'
           WHEN MOD(id, 4) = 1 THEN 'M'
           WHEN MOD(id, 4) = 2 THEN 'L'
           ELSE 'XL'
       END,
       0
FROM sku;

INSERT INTO publish_task(spu_id, strategy, scheduled_time, task_status, fail_reason, created_by, is_deleted)
SELECT
    s.id,
    s.publish_strategy,
    s.scheduled_publish_time,
    CASE
        WHEN s.publish_status = 'PENDING_REVIEW' THEN 'PENDING_REVIEW'
        WHEN s.publish_status = 'WAITING_PUBLISH' THEN 'WAITING_PUBLISH'
        WHEN s.publish_status = 'REVIEW_REJECTED' THEN 'REJECTED'
        WHEN s.publish_status = 'PUBLISHED' THEN 'PUBLISHED'
        ELSE 'PENDING_REVIEW'
    END AS task_status,
    CASE
        WHEN s.publish_status = 'REVIEW_REJECTED' THEN IFNULL(s.reject_reason, 'seed rejected')
        ELSE NULL
    END AS fail_reason,
    'seed_loader',
    0
FROM spu s
WHERE s.publish_status <> 'DRAFT';

INSERT INTO review_record(publish_task_id, spu_id, decision, comment, reviewer, is_deleted)
SELECT
    pt.id,
    pt.spu_id,
    'APPROVE',
    'seed approve',
    'seed_reviewer',
    0
FROM publish_task pt
WHERE pt.task_status IN ('PUBLISHED', 'WAITING_PUBLISH');

INSERT INTO review_record(publish_task_id, spu_id, decision, comment, reviewer, is_deleted)
SELECT
    pt.id,
    pt.spu_id,
    'REJECT',
    IFNULL(pt.fail_reason, 'seed reject'),
    'seed_reviewer',
    0
FROM publish_task pt
WHERE pt.task_status = 'REJECTED';

INSERT INTO operation_log(biz_type, biz_id, action, operator, detail, is_deleted)
SELECT
    'SPU',
    s.id,
    'SEED_CREATE',
    'seed_loader',
    CONCAT('seed spu: ', s.title),
    0
FROM spu s
WHERE s.id <= 200;

INSERT INTO operation_log(biz_type, biz_id, action, operator, detail, is_deleted)
SELECT
    'PUBLISH_TASK',
    pt.id,
    'SEED_TASK',
    'seed_loader',
    CONCAT('seed task status: ', pt.task_status),
    0
FROM publish_task pt
WHERE pt.id <= 200;
