-- ============================================================
-- 宇健口腔医疗系统 - 系统管理初始化数据
-- ============================================================
USE `yujian`;

-- 诊所
INSERT INTO `sys_clinic` (`id`, `parent_id`, `clinic_name`, `clinic_code`, `short_name`, `contact_name`, `contact_phone`, `address`, `sort_order`, `status`)
VALUES (1, 0, '宇健口腔', 'YJ001', '宇健', '管理员', '13800000000', '示例市示例路1号', 1, 0);

-- 部门
INSERT INTO `sys_dept` (`id`, `clinic_id`, `parent_id`, `dept_name`, `dept_code`, `sort_order`, `status`)
VALUES (1, 1, 0, '宇健口腔', 'DEPT001', 1, 0);

-- 角色
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort_order`, `data_scope`, `status`, `remark`) VALUES
(1, '管理员', 'admin', 1, 1, 0, '系统管理员'),
(2, '主任', 'director', 2, 2, 0, '诊所主任'),
(3, '医生', 'doctor', 3, 4, 0, '医生'),
(4, '前台', 'front', 4, 2, 0, '前台接待'),
(5, '护士', 'nurse', 5, 2, 0, '护士'),
(6, '收银员', 'cashier', 6, 2, 0, '收银员'),
(7, '技师', 'technician', 7, 2, 0, '技师');

-- 菜单（网页版示例）
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `path`, `component`, `perms`, `menu_type`, `platform`, `icon`, `visible`, `status`) VALUES
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

-- 预约子菜单示例
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `path`, `perms`, `menu_type`, `platform`, `visible`, `status`) VALUES
(31, '日视图',   3, 1, 'day',    'appointment:day:list',    'C', 'web', 0, 0),
(32, '周视图',   3, 2, 'week',   'appointment:week:list',   'C', 'web', 0, 0),
(33, '月视图',   3, 3, 'month',  'appointment:month:list',  'C', 'web', 0, 0),
(34, '列表',     3, 4, 'list',   'appointment:list:list',   'C', 'web', 0, 0),
(35, '在线预约', 3, 5, 'online', 'appointment:online:list', 'C', 'web', 0, 0),
(36, '回收站',   3, 6, 'recycle','appointment:recycle:list','C', 'web', 0, 0);

-- 移动端菜单示例
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort_order`, `path`, `perms`, `menu_type`, `platform`, `visible`, `status`) VALUES
(100, '工作台', 0, 1, 'workbench', NULL, 'M', 'mobile', 0, 0),
(101, '我的预约', 100, 1, 'myAppoint', 'mobile:appoint:list', 'C', 'mobile', 0, 0);

-- 管理员默认拥有全部菜单
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, id FROM `sys_menu` WHERE del_flag = 0;

-- 管理员账号（密码：123456，BCrypt）
-- 可用 SecurityUtils.encryptPassword("123456") 重新生成
INSERT INTO `sys_employee` (`id`, `name`, `emp_no`, `username`, `password`, `gender`, `mobile`, `clinic_id`, `dept_id`, `position`, `employ_status`, `mobile_link`, `id_type`, `sort_order`, `status`)
VALUES (1, '管理员', '001', 'admin',
        '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
        1, '13800000000', 1, 1, '管理员', 1, 1, '身份证', 1, 0);

INSERT INTO `sys_employee_role` (`employee_id`, `role_id`) VALUES (1, 1);
