-- ============================================================
-- migrate-time-fields.sql
-- 用途：将现有库 20 张表的 create_time / update_time 从 varchar(50) 迁移为 DATETIME
-- 适用：elder_care_system 库已存在数据（全为 NULL，无损迁移）
-- 执行：mysql -uroot -p --default-character-set=utf8mb4 < 此文件
-- ============================================================

USE elder_care_system;
SET NAMES utf8mb4;

-- bed
ALTER TABLE `bed` MODIFY `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';
ALTER TABLE `bed` MODIFY `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间';

-- bed_record
ALTER TABLE `bed_record` MODIFY `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';
ALTER TABLE `bed_record` MODIFY `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间';

-- check_out_record
ALTER TABLE `check_out_record` MODIFY `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';
ALTER TABLE `check_out_record` MODIFY `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间';

-- customer
ALTER TABLE `customer` MODIFY `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';
ALTER TABLE `customer` MODIFY `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间';

-- diet_calendar
ALTER TABLE `diet_calendar` MODIFY `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';
ALTER TABLE `diet_calendar` MODIFY `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间';

-- diet_calendar_set_meal_mapping
ALTER TABLE `diet_calendar_set_meal_mapping` MODIFY `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';
ALTER TABLE `diet_calendar_set_meal_mapping` MODIFY `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间';

-- dish
ALTER TABLE `dish` MODIFY `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';
ALTER TABLE `dish` MODIFY `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间';

-- nursing_item
ALTER TABLE `nursing_item` MODIFY `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';
ALTER TABLE `nursing_item` MODIFY `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间';

-- nursing_item_record
ALTER TABLE `nursing_item_record` MODIFY `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';
ALTER TABLE `nursing_item_record` MODIFY `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间';

-- nursing_level
ALTER TABLE `nursing_level` MODIFY `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';
ALTER TABLE `nursing_level` MODIFY `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间';

-- nursing_level_item_mapping
ALTER TABLE `nursing_level_item_mapping` MODIFY `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';
ALTER TABLE `nursing_level_item_mapping` MODIFY `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间';

-- nursing_record
ALTER TABLE `nursing_record` MODIFY `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';
ALTER TABLE `nursing_record` MODIFY `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间';

-- outing_record
ALTER TABLE `outing_record` MODIFY `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';
ALTER TABLE `outing_record` MODIFY `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间';

-- role
ALTER TABLE `role` MODIFY `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';
ALTER TABLE `role` MODIFY `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间';

-- room
ALTER TABLE `room` MODIFY `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';
ALTER TABLE `room` MODIFY `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间';

-- set_meal
ALTER TABLE `set_meal` MODIFY `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';
ALTER TABLE `set_meal` MODIFY `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间';

-- set_meal_record
ALTER TABLE `set_meal_record` MODIFY `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';
ALTER TABLE `set_meal_record` MODIFY `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间';

-- set_meal_record_dish_mapping
ALTER TABLE `set_meal_record_dish_mapping` MODIFY `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';
ALTER TABLE `set_meal_record_dish_mapping` MODIFY `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间';

-- set_meal_customer_mapping
ALTER TABLE `set_meal_customer_mapping` MODIFY `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';
ALTER TABLE `set_meal_customer_mapping` MODIFY `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间';

-- user
ALTER TABLE `user` MODIFY `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';
ALTER TABLE `user` MODIFY `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间';

-- ============================================================
-- 迁移完成校验：以下查询应全部返回 datetime
-- ============================================================
SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'elder_care_system'
  AND COLUMN_NAME IN ('create_time', 'update_time')
ORDER BY TABLE_NAME, COLUMN_NAME;
