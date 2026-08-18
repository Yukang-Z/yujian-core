-- ============================================================
-- 员工取消关联部门、去掉手机关联字段（已有库执行）
-- ============================================================
USE `yujian`;

ALTER TABLE `t_employee` DROP COLUMN `dept_id`;
ALTER TABLE `t_employee` DROP COLUMN `mobile_link`;
