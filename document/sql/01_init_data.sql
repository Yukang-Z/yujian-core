-- ============================================================
-- 宇健口腔医疗系统 - 初始化数据（统一入口）
-- 依赖：先执行 00_full_schema.sql
-- ============================================================
USE `yujian`;

-- ----------------------------
-- 诊所 / 部门
-- ----------------------------
INSERT INTO `t_clinic` (`id`, `parent_id`, `clinic_name`, `clinic_code`, `short_name`, `contact_name`, `contact_phone`, `address`, `sort_order`, `status`)
VALUES (1, 0, '宇健口腔', 'YJ001', '宇健', '管理员', '13800000000', '示例市示例路1号', 1, 0);

INSERT INTO `t_dept` (`id`, `clinic_id`, `parent_id`, `dept_name`, `dept_code`, `sort_order`, `status`)
VALUES (1, 1, 0, '宇健口腔', 'DEPT001', 1, 0);

-- ----------------------------
-- 角色
-- ----------------------------
INSERT INTO `t_role` (`id`, `role_name`, `role_key`, `sort_order`, `data_scope`, `status`, `remark`) VALUES
(1, '管理员', 'admin', 1, 1, 0, '系统管理员'),
(2, '主任', 'director', 2, 2, 0, '诊所主任'),
(3, '医生', 'doctor', 3, 4, 0, '医生'),
(4, '前台', 'front', 4, 2, 0, '前台接待'),
(5, '护士', 'nurse', 5, 2, 0, '护士'),
(6, '收银员', 'cashier', 6, 2, 0, '收银员'),
(7, '技师', 'technician', 7, 2, 0, '技师');

-- ----------------------------
-- 菜单（网页版）
-- ----------------------------
INSERT INTO `t_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `path`, `component`, `perms`, `menu_type`, `platform`, `icon`, `visible`, `status`) VALUES
(1,  '首页',     0, 1,  'home',        NULL,               NULL,                    'M', 'web', 'home', 0, 0),
(2,  '患者',     0, 2,  'patient',     NULL,               NULL,                    'M', 'web', 'user', 0, 0),
(3,  '预约',     0, 3,  'appointment', NULL,               NULL,                    'M', 'web', 'calendar', 0, 0),
(4,  '设置',     0, 10, 'settings',    NULL,               NULL,                    'M', 'web', 'setting', 0, 0),
(40, '系统管理', 4, 1,  'system',      NULL,               NULL,                    'M', 'web', NULL, 0, 0),
(41, '诊所管理', 40, 1, 'clinic',      'system/clinic/index', 'system:clinic:list', 'C', 'web', NULL, 0, 0),
(42, '员工管理', 40, 2, 'employee',    'system/employee/index', 'system:employee:list', 'C', 'web', NULL, 0, 0),
(43, '角色设置', 40, 3, 'role',        'system/role/index', 'system:role:list',     'C', 'web', NULL, 0, 0),
(44, '权限管理', 40, 4, 'menu',        'system/menu/index', 'system:menu:list',     'C', 'web', NULL, 0, 0),
(411,'诊所查询', 41, 1, '',            NULL,               'system:clinic:query',  'F', 'web', NULL, 0, 0),
(412,'诊所新增', 41, 2, '',            NULL,               'system:clinic:add',    'F', 'web', NULL, 0, 0),
(413,'诊所修改', 41, 3, '',            NULL,               'system:clinic:edit',   'F', 'web', NULL, 0, 0),
(414,'诊所删除', 41, 4, '',            NULL,               'system:clinic:remove', 'F', 'web', NULL, 0, 0),
(421,'员工查询', 42, 1, '',            NULL,               'system:employee:query',  'F', 'web', NULL, 0, 0),
(422,'员工新增', 42, 2, '',            NULL,               'system:employee:add',    'F', 'web', NULL, 0, 0),
(423,'员工修改', 42, 3, '',            NULL,               'system:employee:edit',   'F', 'web', NULL, 0, 0),
(424,'员工删除', 42, 4, '',            NULL,               'system:employee:remove', 'F', 'web', NULL, 0, 0),
(425,'重置密码', 42, 5, '',            NULL,               'system:employee:resetPwd','F', 'web', NULL, 0, 0),
(431,'角色查询', 43, 1, '',            NULL,               'system:role:query',  'F', 'web', NULL, 0, 0),
(432,'角色新增', 43, 2, '',            NULL,               'system:role:add',    'F', 'web', NULL, 0, 0),
(433,'角色修改', 43, 3, '',            NULL,               'system:role:edit',   'F', 'web', NULL, 0, 0),
(434,'角色删除', 43, 4, '',            NULL,               'system:role:remove', 'F', 'web', NULL, 0, 0),
(435,'分配权限', 43, 5, '',            NULL,               'system:role:auth',   'F', 'web', NULL, 0, 0);

INSERT INTO `t_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `path`, `perms`, `menu_type`, `platform`, `visible`, `status`) VALUES
(31, '日视图',   3, 1, 'day',    'appointment:day:list',    'C', 'web', 0, 0),
(32, '周视图',   3, 2, 'week',   'appointment:week:list',   'C', 'web', 0, 0),
(33, '月视图',   3, 3, 'month',  'appointment:month:list',  'C', 'web', 0, 0),
(34, '列表',     3, 4, 'list',   'appointment:list:list',   'C', 'web', 0, 0),
(35, '在线预约', 3, 5, 'online', 'appointment:online:list', 'C', 'web', 0, 0),
(36, '回收站',   3, 6, 'recycle','appointment:recycle:list','C', 'web', 0, 0);

INSERT INTO `t_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `path`, `perms`, `menu_type`, `platform`, `visible`, `status`) VALUES
(100, '工作台', 0, 1, 'workbench', NULL, 'M', 'mobile', 0, 0),
(101, '我的预约', 100, 1, 'myAppoint', 'mobile:appoint:list', 'C', 'mobile', 0, 0);

INSERT INTO `t_role_menu` (`role_id`, `menu_id`)
SELECT 1, id FROM `t_menu` WHERE is_delete = 0;

-- 管理员账号（密码：123456，BCrypt）
INSERT INTO `t_employee` (`id`, `name`, `emp_no`, `username`, `password`, `gender`, `mobile`, `clinic_id`, `position`, `employ_status`, `id_type`, `sort_order`, `status`)
VALUES (1, '管理员', '001', 'admin',
        '$2a$10$9azfvb1/7NDfZvexAcRdmOzC7sqySzyYDvP3u350XB5zmqzDCUak2',
        1, '13800000000', 1, '医生', 1, '身份证', 1, 0);

INSERT INTO `t_employee_role` (`employee_id`, `role_id`) VALUES (1, 1);

-- 管理员关联诊所（可多诊所；初始化关联总部）
INSERT INTO `t_employee_clinic` (`employee_id`, `clinic_id`) VALUES (1, 1);

-- ----------------------------
-- 字典类型 / 字典数据
-- ----------------------------
INSERT INTO `t_dict_type` (`dict_name`, `dict_type`, `status`, `remark`) VALUES
('就诊类型', 'visit_type', 0, '初诊/复诊/新诊'),
('预约状态', 'appoint_status', 0, '预约状态机'),
('手机关系', 'mobile_relation', 0, '本人/亲属'),
('患者类型', 'patient_type', 0, '普通/临时'),
('预约类型', 'appoint_type', 0, '普通/散客/网络/待确定'),
('预约来源', 'appoint_source', 0, '院内/网络/微信'),
('取消原因', 'cancel_reason', 0, '取消/删除原因');

INSERT INTO `t_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort_order`, `status`) VALUES
('visit_type', '初诊', '1', 1, 0),
('visit_type', '复诊', '2', 2, 0),
('visit_type', '新诊', '3', 3, 0);

INSERT INTO `t_dict_data` (`dict_type`, `dict_label`, `dict_value`, `css_class`, `sort_order`, `status`) VALUES
('appoint_status', '已预约', '1', 'booked', 1, 0),
('appoint_status', '已确认', '2', 'confirmed', 2, 0),
('appoint_status', '已到达', '3', 'arrived', 3, 0),
('appoint_status', '治疗中', '4', 'treating', 4, 0),
('appoint_status', '已离开', '5', 'left', 5, 0),
('appoint_status', '已过期', '6', 'expired', 6, 0),
('appoint_status', '已流失', '7', 'lost', 7, 0),
('appoint_status', '预约未到', '8', 'missed', 8, 0);

INSERT INTO `t_dict_data` (`dict_type`, `dict_label`, `dict_value`, `sort_order`, `status`) VALUES
('mobile_relation', '本人', '本人', 1, 0),
('mobile_relation', '父亲', '父亲', 2, 0),
('mobile_relation', '母亲', '母亲', 3, 0),
('mobile_relation', '配偶', '配偶', 4, 0),
('mobile_relation', '子女', '子女', 5, 0),
('mobile_relation', '其他', '其他', 6, 0),
('patient_type', '普通', '1', 1, 0),
('patient_type', '临时', '2', 2, 0),
('appoint_type', '普通预约', 'normal', 1, 0),
('appoint_type', '散客', 'walkin', 2, 0),
('appoint_type', '网络预约', 'online', 3, 0),
('appoint_type', '待确定', 'pending', 4, 0),
('appoint_source', '院内', 'clinic', 1, 0),
('appoint_source', '网络', 'online', 2, 0),
('appoint_source', '微信', 'wechat', 3, 0),
('cancel_reason', '改时间', 'change_time', 1, 0),
('cancel_reason', '患者取消', 'patient_cancel', 2, 0),
('cancel_reason', '医生请假', 'doctor_leave', 3, 0),
('cancel_reason', '重复录入', 'duplicate', 4, 0),
('cancel_reason', '其他', 'other', 5, 0);

-- ----------------------------
-- 患者标签 / 来源 / 诊疗项目
-- ----------------------------
INSERT INTO `t_patient_tag` (`clinic_id`, `tag_name`, `tag_color`, `sort_order`, `status`) VALUES
(1, '新', '#67C23A', 1, 0),
(1, '老', '#409EFF', 2, 0),
(1, 'VIP', '#E6A23C', 3, 0);

INSERT INTO `t_patient_source` (`id`, `clinic_id`, `parent_id`, `source_name`, `sort_order`, `status`) VALUES
(1, 1, 0, '自然到店', 1, 0),
(2, 1, 0, '网络推广', 2, 0),
(3, 1, 2, '美团', 1, 0),
(4, 1, 2, '抖音', 2, 0),
(5, 1, 0, '老带新', 3, 0);

INSERT INTO `t_treatment_item` (`clinic_id`, `item_name`, `item_code`, `duration`, `item_color`, `sort_order`, `status`) VALUES
(1, '检查', 'CHECK', 15, '#67C23A', 1, 0),
(1, '洁牙', 'CLEAN', 30, '#409EFF', 2, 0),
(1, '补牙', 'FILL', 30, '#E6A23C', 3, 0),
(1, '拔牙', 'EXTRACT', 30, '#F56C6C', 4, 0),
(1, '治疗', 'TREAT', 45, '#909399', 5, 0),
(1, '修复', 'RESTORE', 45, '#9B59B6', 6, 0),
(1, '正畸', 'ORTHO', 30, '#9B59B6', 7, 0),
(1, '牙体充填', 'FILLING', 30, '#9B59B6', 8, 0);

-- ----------------------------
-- 示例患者 / 标签 / 预约
-- ----------------------------
INSERT INTO `t_patient` (`clinic_id`, `medical_record_no`, `name`, `name_pinyin`, `gender`, `star_level`,
                         `birthday`, `age`, `mobile`, `mobile_relation`, `doctor_id`, `first_doctor_id`,
                         `first_visit_time`, `patient_type`, `source_id`, `status`)
VALUES (1, '000001', '张三', 'ZS', 1, 3, '1990-05-01', 36, '13800138000', '本人',
        1, 1, NOW(), 1, 1, 0);

INSERT INTO `t_patient_tag_rel` (`patient_id`, `tag_id`)
SELECT p.id, t.id FROM t_patient p, t_patient_tag t
WHERE p.medical_record_no = '000001' AND t.tag_name = '新' AND t.clinic_id = 1 LIMIT 1;

INSERT INTO `t_appointment` (`clinic_id`, `patient_id`, `doctor_id`, `start_time`, `end_time`,
                             `visit_type`, `status`, `item_id`, `item_name`, `item_color`, `remark`)
SELECT 1, p.id, 1,
       CONCAT(CURDATE(), ' 09:00:00'),
       CONCAT(CURDATE(), ' 09:30:00'),
       2, 1, i.id, i.item_name, i.item_color, '示例预约'
FROM t_patient p, t_treatment_item i
WHERE p.medical_record_no = '000001' AND i.item_code = 'FILLING' LIMIT 1;
