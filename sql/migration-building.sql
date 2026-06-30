-- 将楼号从 606 迁移为 06（01-06 格式）
UPDATE `room` SET `building` = '06' WHERE `building` = '606';

-- 为已有数据库补充 01-05 号楼数据（若尚未导入，请执行 demo-data-buildings-01-05.sql）
-- source demo-data-buildings-01-05.sql;
