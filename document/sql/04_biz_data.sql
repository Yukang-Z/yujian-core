-- ============================================================
-- 宇健口腔 - 患者/预约/基础数据 初始化
-- ============================================================
USE `yujian`;

-- 字典类型
INSERT INTO `biz_dict_type` (`dict_name`, `dict_type`, `status`, `remark`) VALUES
('就诊类型', 'visit_type', 0, '初诊/复诊'),
('预约状态', 'appoint_status', 0, '预约状态机'),
('手机关系', 'mobile_relation', 0, '本人/亲属'),
('患者类型', 'patient_type', 0, '普通/临时');

-- 就诊类型
INSERT INTO `biz_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort_order`, `status`) VALUES
('visit_type', '初诊', '1', 1, 0),
('visit_type', '复诊', '2', 2, 0);

-- 预约状态（与截图一致）
INSERT INTO `biz_dict_data` (`dict_type`, `dict_label`, `dict_value`, `css_class`, `sort_order`, `status`) VALUES
('appoint_status', '已预约', '1', 'booked', 1, 0),
('appoint_status', '已确认', '2', 'confirmed', 2, 0),
('appoint_status', '已到达', '3', 'arrived', 3, 0),
('appoint_status', '治疗中', '4', 'treating', 4, 0),
('appoint_status', '已离开', '5', 'left', 5, 0),
('appoint_status', '已过期', '6', 'expired', 6, 0),
('appoint_status', '已流失', '7', 'lost', 7, 0),
('appoint_status', '预约未到', '8', 'missed', 8, 0);

INSERT INTO `biz_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort_order`, `status`) VALUES
('mobile_relation', '本人', '本人', 1, 0),
('mobile_relation', '父亲', '父亲', 2, 0),
('mobile_relation', '母亲', '母亲', 3, 0),
('mobile_relation', '配偶', '配偶', 4, 0),
('mobile_relation', '子女', '子女', 5, 0),
('mobile_relation', '其他', '其他', 6, 0),
('patient_type', '普通', '1', 1, 0),
('patient_type', '临时', '2', 2, 0);

-- 患者标签
INSERT INTO `biz_patient_tag` (`clinic_id`, `tag_name`, `tag_color`, `sort_order`, `status`) VALUES
(1, '新', '#67C23A', 1, 0),
(1, '老', '#409EFF', 2, 0),
(1, 'VIP', '#E6A23C', 3, 0);

-- 患者来源
INSERT INTO `biz_patient_source` (`id`, `clinic_id`, `parent_id`, `source_name`, `sort_order`, `status`) VALUES
(1, 1, 0, '自然到店', 1, 0),
(2, 1, 0, '网络推广', 2, 0),
(3, 1, 2, '美团', 1, 0),
(4, 1, 2, '抖音', 2, 0),
(5, 1, 0, '老带新', 3, 0);

-- 诊疗项目
INSERT INTO `biz_treat_item` (`clinic_id`, `item_name`, `item_code`, `duration`, `sort_order`, `status`) VALUES
(1, '检查', 'CHECK', 15, 1, 0),
(1, '洁牙', 'CLEAN', 30, 2, 0),
(1, '补牙', 'FILL', 30, 3, 0),
(1, '拔牙', 'EXTRACT', 30, 4, 0),
(1, '治疗', 'TREAT', 45, 5, 0),
(1, '修复', 'RESTORE', 45, 6, 0),
(1, '正畸', 'ORTHO', 30, 7, 0),
(1, '牙体充填', 'FILLING', 30, 8, 0);

-- 补充医生岗位员工示例（若已有管理员可改岗位）
UPDATE `sys_employee` SET `position` = '医生' WHERE `id` = 1 AND `emp_no` = '001';

-- 示例患者
INSERT INTO `biz_patient` (`clinic_id`, `medical_record_no`, `name`, `name_pinyin`, `gender`, `star_level`,
                           `birthday`, `age`, `mobile`, `mobile_relation`, `doctor_id`, `first_doctor_id`,
                           `first_visit_time`, `patient_type`, `source_id`, `status`)
VALUES (1, '000001', '张三', 'ZS', 1, 3, '1990-05-01', 36, '13800138000', '本人',
        1, 1, NOW(), 1, 1, 0);

INSERT INTO `biz_patient_tag_rel` (`patient_id`, `tag_id`)
SELECT p.id, t.id FROM biz_patient p, biz_patient_tag t
WHERE p.medical_record_no = '000001' AND t.tag_name = '新' AND t.clinic_id = 1 LIMIT 1;

-- 示例预约（今日）
INSERT INTO `biz_appointment` (`clinic_id`, `patient_id`, `doctor_id`, `start_time`, `end_time`,
                               `visit_type`, `status`, `item_id`, `item_name`, `remark`)
SELECT 1, p.id, 1,
       CONCAT(CURDATE(), ' 09:00:00'),
       CONCAT(CURDATE(), ' 09:30:00'),
       2, 1, i.id, i.item_name, '示例预约'
FROM biz_patient p, biz_treat_item i
WHERE p.medical_record_no = '000001' AND i.item_code = 'FILLING' LIMIT 1;
