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
('华为', 100, 'HUAWEI consumer electronics and smart devices', 0),
('小米', 96, 'Xiaomi phones and smart home devices', 0),
('苹果', 98, 'Apple phones, computers and accessories', 0),
('戴森', 92, 'Dyson home appliances and personal care devices', 0),
('雅诗兰黛', 88, 'Estee Lauder skincare and makeup', 0),
('兰蔻', 86, 'Lancome skincare and makeup', 0),
('李宁', 84, 'Li-Ning sport shoes and apparel', 0),
('优衣库', 80, 'UNIQLO daily apparel', 0),
('京东京造', 76, 'JD private-label lifestyle products', 0),
('始祖鸟', 82, 'Arc''teryx outdoor apparel', 0);

INSERT INTO category(name, parent_id, is_deleted) VALUES
('手机', 0, 0),
('笔记本电脑', 0, 0),
('耳机', 0, 0),
('家用电器', 0, 0),
('美妆护肤', 0, 0),
('运动鞋', 0, 0),
('服饰', 0, 0),
('箱包', 0, 0);

INSERT INTO category_attribute(category_id, attr_name, attr_scope, required_flag, data_type, is_deleted)
SELECT c.id, a.attr_name, a.attr_scope, a.required_flag, 'TEXT', 0
FROM category c
JOIN (
    SELECT '材质' attr_name, 'SPU' attr_scope, 1 required_flag UNION ALL
    SELECT '适用场景', 'SPU', 1 UNION ALL
    SELECT '核心卖点', 'SPU', 0 UNION ALL
    SELECT '颜色', 'SKU', 1 UNION ALL
    SELECT '规格', 'SKU', 1
) a;

INSERT INTO banned_word(word, enabled, is_deleted) VALUES
('虚假宣传', 1, 0),
('绝对第一', 1, 0),
('医疗治愈', 1, 0),
('违规导流', 1, 0);

CREATE TEMPORARY TABLE seed_product (
    seq INT PRIMARY KEY,
    brand_name VARCHAR(128) NOT NULL,
    category_name VARCHAR(128) NOT NULL,
    title VARCHAR(256) NOT NULL,
    description TEXT NOT NULL,
    material VARCHAR(128) NOT NULL,
    scene VARCHAR(128) NOT NULL,
    keywords VARCHAR(512) NOT NULL,
    sku_prefix VARCHAR(32) NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    base_sales INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO seed_product VALUES
(1, '华为', '手机', '华为 Mate 60 Pro 旗舰手机 12GB+256GB', '支持 5G 场景体验、昆仑玻璃、长焦拍照、卫星通信，适合商务、旅行和重度移动办公。可用 huawei mate60 mate 60 pro 检索。', '昆仑玻璃 玄武架构', '商务办公 旅行拍摄', '旗舰手机 拍照手机 卫星通信 鸿蒙 huawei mate60pro', 'HW-M60P', 6999, 18600),
(2, '华为', '手机', '华为 Pura 70 Pro 人像摄影手机', '主打人像、夜景、长焦和轻薄手感，适合喜欢拍照和短视频创作的用户。支持 pura70 p70 huawei 检索。', '铝合金 玻璃', '人像摄影 短视频', '拍照手机 人像 夜景 长焦 pura p70', 'HW-P70P', 6499, 14200),
(3, '小米', '手机', '小米 14 Ultra 徕卡影像旗舰手机', '徕卡 Summilux 镜头、澎湃系统、大电池，适合摄影发烧友和安卓旗舰用户。可通过 xiaomi mi14 ultra 搜索。', '龙晶玻璃 素皮', '摄影 旅行 游戏', '小米14 ultra 徕卡 影像旗舰 xiaomi mi', 'MI-14U', 5999, 17120),
(4, '小米', '手机', 'Redmi K70 性能电竞手机', '第二代骁龙平台、高刷直屏、快充，适合游戏、学生和高性价比购机场景。支持 redmi k70 红米 检索。', '金属中框 玻璃', '游戏 学生 日常', '红米 redmi k70 电竞手机 性价比', 'MI-K70', 2499, 23600),
(5, '苹果', '手机', 'Apple iPhone 15 Pro 钛金属手机', 'A17 Pro 芯片、钛金属边框、ProMotion 屏幕，适合视频拍摄、移动剪辑和 iOS 用户。', '钛金属 超瓷晶面板', '视频创作 移动办公', '苹果 apple iphone 15 pro ios 钛金属', 'AP-IP15P', 7999, 20310),
(6, '苹果', '手机', 'Apple iPhone 15 标准版 5G 手机', '轻量化设计、灵动岛、双摄系统，适合日常拍照和家庭用户。支持 iphone15 apple 检索。', '铝金属 玻璃', '日常通勤 家庭拍照', '苹果 iphone15 5g 手机 灵动岛', 'AP-IP15', 5999, 18100),
(7, '苹果', '笔记本电脑', 'MacBook Air 13 英寸 M3 轻薄本', 'M3 芯片、长续航、安静无风扇设计，适合学生、办公、文档和轻量开发。可通过 macbook air m3 搜索。', '再生铝金属', '办公 学习 轻量开发', '苹果电脑 macbook air m3 轻薄本 办公本', 'AP-MBA13', 8999, 12900),
(8, '华为', '笔记本电脑', '华为 MateBook X Pro 高端轻薄办公本', '高分辨率触控屏、轻薄金属机身、多屏协同，适合商务演示、移动办公和高效会议。', '镁铝合金', '商务办公 会议演示', 'matebook x pro 华为电脑 轻薄本 办公本', 'HW-MBX', 9999, 8600),
(9, '小米', '笔记本电脑', '小米 RedmiBook Pro 14 高性能轻薄本', '高刷屏、标压处理器、金属机身，适合学生、编程、办公和轻度创作。', '铝合金', '学习 编程 办公', 'redmibook pro 小米笔记本 轻薄本 编程', 'MI-RBP14', 5299, 11120),
(10, '苹果', '耳机', 'AirPods Pro 2 主动降噪真无线耳机', '主动降噪、通透模式、空间音频，适合通勤、运动和 iPhone 用户。支持 airpods pro 蓝牙耳机搜索。', '亲肤硅胶 塑料', '通勤 运动 会议', 'airpods pro 降噪耳机 真无线 蓝牙耳机', 'AP-APP2', 1899, 26000),
(11, '华为', '耳机', '华为 FreeBuds Pro 3 智能降噪耳机', '智慧动态降噪、清晰通话、双设备连接，适合办公会议和地铁通勤。', '塑料 硅胶', '办公 通勤 通话', 'freebuds pro 华为耳机 降噪 蓝牙', 'HW-FBP3', 1199, 14320),
(12, '小米', '耳机', '小米 Buds 5 Pro 高保真降噪耳机', '高解析音频、深度降噪、低延迟游戏模式，适合音乐、视频和游戏。', '塑料 硅胶', '音乐 游戏 视频', 'xiaomi buds 小米耳机 降噪 低延迟', 'MI-BUDS5', 799, 13200),
(13, '戴森', '家用电器', '戴森 V12 Detect Slim 无线吸尘器', '激光显尘、轻量化机身、多刷头组合，适合宠物家庭、地毯和木地板清洁。', 'ABS 碳纤维', '家庭清洁 宠物家庭', 'dyson v12 戴森吸尘器 无线吸尘 激光显尘', 'DY-V12', 4290, 7400),
(14, '戴森', '家用电器', '戴森 Supersonic 负离子吹风机', '高速马达、智能温控、顺滑造型，适合长发、细软发和沙龙护理。', '尼龙 玻纤', '洗护 造型 旅行', 'dyson 吹风机 负离子 高速 干发', 'DY-HD15', 3290, 9800),
(15, '京东京造', '家用电器', '京东京造 智能空气炸锅 5L 可视窗口', '少油烹饪、可视窗口、预约菜单，适合家庭早餐、夜宵和厨房新手。', '不粘涂层 食品级塑料', '厨房 早餐 夜宵', '空气炸锅 可视窗口 少油 京东京造', 'JD-AF5', 399, 22100),
(16, '京东京造', '家用电器', '京东京造 自清洁洗地机 Pro', '吸拖洗一体、自清洁滚刷、沿边清洁，适合厨房油污、宠物毛发和日常地面清洁。', 'ABS 不锈钢', '地面清洁 宠物家庭', '洗地机 自清洁 吸拖一体 京东京造', 'JD-WM-PRO', 1699, 8700),
(17, '雅诗兰黛', '美妆护肤', '雅诗兰黛 小棕瓶 第七代精华液', '修护保湿、抗老维稳，适合熬夜肌、初老肌和换季敏感肌。支持 estee lauder 小棕瓶搜索。', '精华液 玻璃瓶', '夜间修护 抗老保湿', '小棕瓶 精华 抗老 保湿 修护 estee lauder', 'EL-ANR', 690, 16800),
(18, '雅诗兰黛', '美妆护肤', '雅诗兰黛 白金级黑钻面霜', '丰润质地、紧致提亮、滋养修护，适合干皮和熟龄肌高端护理。', '面霜 玻璃瓶', '滋养修护 紧致', '白金面霜 黑钻面霜 抗老 滋润', 'EL-CREAM', 2480, 3900),
(19, '兰蔻', '美妆护肤', '兰蔻 小黑瓶 肌底精华液', '维稳修护、细腻肌肤、轻薄易吸收，适合换季敏感和日常护肤。支持 lancome 小黑瓶检索。', '精华液 玻璃瓶', '日常护肤 维稳', '小黑瓶 兰蔻 精华 肌底液 lancome', 'LC-GEN', 760, 15200),
(20, '兰蔻', '美妆护肤', '兰蔻 菁纯持妆粉底液', '细腻遮瑕、持久妆效、自然光泽，适合通勤、约会和重要场合。', '粉底液 玻璃瓶', '通勤 妆容 约会', '粉底液 持妆 遮瑕 菁纯 兰蔻', 'LC-FDT', 590, 9800),
(21, '李宁', '运动鞋', '李宁 赤兔 7 Pro 跑步鞋', '轻量回弹、中底缓震、透气鞋面，适合日常慢跑、健身房和通勤跑。支持 lining chitu 搜索。', '工程网布 发泡中底', '慢跑 健身 通勤', '赤兔 跑步鞋 跑鞋 缓震 李宁 lining', 'LN-CT7', 599, 19400),
(22, '李宁', '运动鞋', '李宁 驭帅 18 篮球鞋', '稳定支撑、耐磨外底、包裹脚感，适合室内外篮球和锋线打法。', '织物 TPU 橡胶', '篮球 训练 比赛', '驭帅 篮球鞋 李宁 支撑 耐磨', 'LN-YS18', 899, 13280),
(23, '小米', '运动鞋', '小米生态链 轻弹休闲运动鞋', '简洁鞋型、轻弹脚感、适合城市通勤、校园和轻运动。', '织物 EVA', '通勤 校园 轻运动', '休闲运动鞋 轻弹 小米生态链', 'MI-SHOE', 299, 10400),
(24, '优衣库', '服饰', '优衣库 AIRism 凉感防晒衣 UPF50+', '轻薄透气、凉感触感、UPF50+ 防晒，适合夏季通勤、骑行和旅行。支持 uniqlo airism 搜索。', '锦纶 聚氨酯', '夏季通勤 骑行 旅行', 'airism 防晒衣 防晒服 凉感 upf50 uniqlo', 'UQ-AIR', 199, 25100),
(25, '优衣库', '服饰', '优衣库 HEATTECH 保暖内衣套装', '吸湿发热、柔软贴身，适合冬季通勤、居家和北方出行。', '粘纤 腈纶 氨纶', '冬季通勤 居家', 'heattech 保暖内衣 发热 优衣库', 'UQ-HT', 149, 23700),
(26, '始祖鸟', '服饰', '始祖鸟 Beta LT GORE-TEX 冲锋衣', '轻量防水、防风透气、压胶工艺，适合徒步、登山和城市户外。支持 arcteryx beta 搜索。', 'GORE-TEX 尼龙', '徒步 登山 城市户外', '始祖鸟 beta lt 冲锋衣 硬壳 goretex arcteryx', 'ARC-BETA', 4200, 7300),
(27, '京东京造', '服饰', '京东京造 90 白鹅绒轻暖羽绒服', '高蓬松白鹅绒、轻量保暖、通勤版型，适合冬季城市出行。', '白鹅绒 尼龙', '冬季通勤 保暖', '羽绒服 白鹅绒 轻暖 京东京造', 'JD-DOWN', 699, 13600),
(28, '京东京造', '箱包', '京东京造 商务通勤双肩包 15.6 英寸', '独立电脑仓、防泼水面料、多隔层收纳，适合上班、差旅和学生。', '聚酯纤维 防泼水面料', '商务通勤 差旅 学习', '双肩包 电脑包 通勤包 15.6 京东京造', 'JD-BACKPACK', 199, 20100),
(29, '苹果', '箱包', 'Apple 原厂 MagSafe 卡包 精织斜纹', '磁吸设计、精织斜纹材质、适配 iPhone MagSafe 生态，适合轻出行。', '精织斜纹 磁吸', '轻出行 日常', 'magsafe 卡包 苹果 磁吸 斜纹', 'AP-WALLET', 479, 8100),
(30, '优衣库', '箱包', '优衣库 Round Mini 半月斜挎包', '轻量耐磨、小巧容量、休闲百搭，适合逛街、旅行和日常通勤。', '尼龙 聚酯纤维', '逛街 旅行 通勤', '半月包 斜挎包 mini round uniqlo', 'UQ-MINI', 99, 28800),
(31, '华为', '耳机', '华为 Eyewear 智能音频眼镜', '开放式聆听、通话降噪、轻量镜框，适合办公、骑行和长时间佩戴。', '钛合金 镜片', '办公 骑行 通话', '智能眼镜 音频眼镜 华为 eyewear', 'HW-EYE', 1699, 5200),
(32, '戴森', '家用电器', '戴森 Zone 空气净化耳机', '空气净化与降噪耳机结合，适合通勤、飞行和新奇科技体验。', '复合材料 过滤组件', '通勤 飞行 科技体验', 'dyson zone 空气净化耳机 降噪', 'DY-ZONE', 5990, 1600),
(33, '兰蔻', '美妆护肤', '兰蔻 净澈焕肤双精华', '水油双相质地，主打细腻肤感和提亮肤色，适合夜间护理。', '双相精华 玻璃瓶', '夜间护理 提亮', '焕肤 双精华 提亮 兰蔻', 'LC-PEEL', 820, 3100),
(34, '李宁', '运动鞋', '李宁 绝影 2 Essential 竞速跑鞋', '碳板推进、轻量回弹，适合马拉松训练和竞速跑者。', '织物 碳板 发泡中底', '马拉松 竞速训练', '绝影 碳板 跑鞋 竞速 李宁', 'LN-JY2', 1299, 6800),
(35, '小米', '家用电器', '米家 扫地机器人 免洗拖布版', '自动集尘、免洗拖布、激光导航，适合大户型和懒人清洁。', 'ABS 传感器组件', '地面清洁 大户型', '米家 扫地机器人 自动集尘 免洗拖布', 'MI-ROBOT', 2299, 10200),
(36, '华为', '笔记本电脑', '华为 MateBook 14 酷睿版', '高分辨率屏幕、金属机身、多设备协同，适合学生和移动办公。', '铝合金', '学习 办公', 'matebook14 华为电脑 酷睿 轻薄本', 'HW-MB14', 5999, 9400);

INSERT INTO spu(title, brand_id, category_id, description, publish_status, publish_strategy, scheduled_publish_time, reject_reason, is_deleted)
SELECT p.title,
       b.id,
       c.id,
       CONCAT(p.description, ' 关键词: ', p.keywords),
       CASE
           WHEN p.seq <= 32 THEN 'PUBLISHED'
           WHEN p.seq <= 34 THEN 'PENDING_REVIEW'
           ELSE 'DRAFT'
       END,
       CASE
           WHEN p.seq <= 30 THEN 'IMMEDIATE'
           WHEN p.seq <= 34 THEN 'MANUAL_AFTER_REVIEW'
           ELSE 'SCHEDULED'
       END,
       CASE WHEN p.seq > 34 THEN DATE_ADD(NOW(), INTERVAL p.seq HOUR) ELSE NULL END,
       NULL,
       0
FROM seed_product p
JOIN brand b ON b.name = p.brand_name
JOIN category c ON c.name = p.category_name
ORDER BY p.seq;

CREATE TEMPORARY TABLE seed_variant (
    seq INT PRIMARY KEY,
    code_suffix VARCHAR(8) NOT NULL,
    color VARCHAR(64) NOT NULL,
    price_delta DECIMAL(10,2) NOT NULL,
    stock_delta INT NOT NULL,
    sales_delta INT NOT NULL
) ENGINE=Memory DEFAULT CHARSET=utf8mb4;

INSERT INTO seed_variant VALUES
(1, 'A', '曜石黑', 0, 80, 0),
(2, 'B', '冰川银', 300, 40, -860);

INSERT INTO sku(spu_id, sku_code, sku_name, price, stock, sales, sku_status, is_deleted)
SELECT s.id,
       CONCAT('SKU-', p.sku_prefix, '-', v.code_suffix),
       CONCAT(p.title, ' ', v.color, ' ',
              CASE p.category_name
                  WHEN '手机' THEN IF(v.seq = 1, '12GB+256GB', '16GB+512GB')
                  WHEN '笔记本电脑' THEN IF(v.seq = 1, '16GB+512GB', '32GB+1TB')
                  WHEN '耳机' THEN IF(v.seq = 1, '标准套装', '无线充套装')
                  WHEN '家用电器' THEN IF(v.seq = 1, '标准版', '增强版')
                  WHEN '美妆护肤' THEN IF(v.seq = 1, '30ml', '50ml')
                  WHEN '运动鞋' THEN IF(v.seq = 1, '42码', '43码')
                  WHEN '服饰' THEN IF(v.seq = 1, 'M码', 'L码')
                  ELSE IF(v.seq = 1, '标准款', '大容量款')
              END),
       p.base_price + v.price_delta,
       120 + p.seq * 3 + v.stock_delta,
       GREATEST(p.base_sales + v.sales_delta, 0),
       CASE WHEN s.publish_status = 'PUBLISHED' THEN 'ON_SALE' ELSE 'DRAFT' END,
       0
FROM seed_product p
JOIN spu s ON s.title = p.title
JOIN seed_variant v
ORDER BY p.seq, v.seq;

INSERT INTO spu_attr_value(spu_id, attr_name, attr_value, is_deleted)
SELECT s.id, '材质', p.material, 0
FROM seed_product p JOIN spu s ON s.title = p.title;

INSERT INTO spu_attr_value(spu_id, attr_name, attr_value, is_deleted)
SELECT s.id, '适用场景', p.scene, 0
FROM seed_product p JOIN spu s ON s.title = p.title;

INSERT INTO spu_attr_value(spu_id, attr_name, attr_value, is_deleted)
SELECT s.id, '核心卖点', p.keywords, 0
FROM seed_product p JOIN spu s ON s.title = p.title;

INSERT INTO sku_attr_value(sku_id, attr_name, attr_value, is_deleted)
SELECT sk.id, '颜色', SUBSTRING_INDEX(SUBSTRING_INDEX(sk.sku_name, ' ', -2), ' ', 1), 0
FROM sku sk;

INSERT INTO sku_attr_value(sku_id, attr_name, attr_value, is_deleted)
SELECT sk.id, '规格', SUBSTRING_INDEX(sk.sku_name, ' ', -1), 0
FROM sku sk;

INSERT INTO publish_task(spu_id, strategy, scheduled_time, task_status, fail_reason, created_by, is_deleted)
SELECT s.id,
       s.publish_strategy,
       s.scheduled_publish_time,
       CASE
           WHEN s.publish_status = 'PUBLISHED' THEN 'PUBLISHED'
           WHEN s.publish_status = 'PENDING_REVIEW' THEN 'PENDING_REVIEW'
           ELSE 'WAITING_PUBLISH'
       END,
       NULL,
       'seed_loader',
       0
FROM spu s
WHERE s.publish_status <> 'DRAFT';

INSERT INTO review_record(publish_task_id, spu_id, decision, comment, reviewer, is_deleted)
SELECT pt.id,
       pt.spu_id,
       'APPROVE',
       'seed approve for search demo',
       'seed_reviewer',
       0
FROM publish_task pt
WHERE pt.task_status = 'PUBLISHED';

INSERT INTO operation_log(biz_type, biz_id, action, operator, detail, is_deleted)
SELECT 'SPU',
       s.id,
       'SEED_RECREATE',
       'seed_loader',
       CONCAT('seed product recreated: ', s.title),
       0
FROM spu s;

INSERT INTO operation_log(biz_type, biz_id, action, operator, detail, is_deleted)
SELECT 'SEARCH',
       0,
       'SEED_READY',
       'seed_loader',
       'MySQL seed data is ready for IK + pinyin Elasticsearch reindex',
       0;
