-- ============================================================
-- 预约列表缺口：待确定 appointType=pending 字典
-- ============================================================
USE `yujian`;

INSERT INTO `t_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort_order`, `status`)
SELECT 'appoint_type', '待确定', 'pending', 4, 0
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM `t_dict_data` WHERE `dict_type` = 'appoint_type' AND `dict_value` = 'pending' AND `is_delete` = 0
);

UPDATE `t_dict_type` SET `remark` = '普通/散客/网络/待确定' WHERE `dict_type` = 'appoint_type';
