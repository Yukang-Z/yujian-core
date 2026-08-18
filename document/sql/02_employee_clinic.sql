-- ============================================================
-- 员工-诊所一对多：增量迁移（已有库执行）
-- ============================================================
USE `yujian`;

CREATE TABLE IF NOT EXISTS `t_employee_clinic` (
  `id`          BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `employee_id` BIGINT(20) NOT NULL COMMENT '员工ID',
  `clinic_id`   BIGINT(20) NOT NULL COMMENT '诊所ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_employee_clinic` (`employee_id`, `clinic_id`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_clinic_id` (`clinic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工诊所关联表';

-- 将原 t_employee.clinic_id 迁移到关联表（已存在则跳过）
INSERT INTO `t_employee_clinic` (`employee_id`, `clinic_id`)
SELECT e.id, e.clinic_id
FROM `t_employee` e
WHERE e.clinic_id IS NOT NULL
  AND e.is_delete = 0
  AND NOT EXISTS (
    SELECT 1 FROM `t_employee_clinic` ec
    WHERE ec.employee_id = e.id AND ec.clinic_id = e.clinic_id
  );
