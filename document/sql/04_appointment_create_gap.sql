-- ============================================================
-- 新增预约缺口：多项目明细 + 就诊类型「新诊」
-- 已有库执行本脚本；新建库请同步 00_full_schema / 01_init_data
-- ============================================================
USE `yujian`;

-- 预约项目明细（多选）
CREATE TABLE IF NOT EXISTS `t_appointment_item` (
  `id`             BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `appointment_id` BIGINT(20)   NOT NULL COMMENT '预约ID',
  `item_id`        BIGINT(20)   DEFAULT NULL COMMENT '项目ID',
  `item_name`      VARCHAR(100) DEFAULT NULL COMMENT '项目名称',
  `duration`       INT(11)      DEFAULT 30 COMMENT '时长分钟',
  `sort_order`     INT(11)      DEFAULT 0 COMMENT '排序（越小越靠前）',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_appointment_id` (`appointment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约项目明细';

-- 就诊类型：新诊=3（已存在则跳过）
INSERT INTO `t_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort_order`, `status`)
SELECT 'visit_type', '新诊', '3', 3, 0
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `t_dict_data` WHERE `dict_type` = 'visit_type' AND `dict_value` = '3' AND `is_delete` = 0
);

UPDATE `t_dict_type` SET `remark` = '初诊/复诊/新诊' WHERE `dict_type` = 'visit_type';
