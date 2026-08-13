-- ============================================================
-- 宇健口腔医疗系统 - 系统管理表结构
-- 库名：yujian
-- 字符集：utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS `yujian` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `yujian`;

-- ----------------------------
-- 诊所表
-- ----------------------------
DROP TABLE IF EXISTS `sys_clinic`;
CREATE TABLE `sys_clinic` (
    `id`              bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `clinic_code`     varchar(64)     NOT NULL COMMENT '诊所编码',
    `clinic_name`     varchar(128)    NOT NULL COMMENT '诊所名称',
    `parent_id`       bigint unsigned NOT NULL DEFAULT 0 COMMENT '父诊所ID，0表示顶级',
    `contact_name`    varchar(64)              DEFAULT NULL COMMENT '联系人',
    `contact_phone`   varchar(20)              DEFAULT NULL COMMENT '联系电话',
    `province`        varchar(64)              DEFAULT NULL COMMENT '省',
    `city`            varchar(64)              DEFAULT NULL COMMENT '市',
    `district`        varchar(64)              DEFAULT NULL COMMENT '区',
    `address`         varchar(255)             DEFAULT NULL COMMENT '详细地址',
    `status`          tinyint unsigned NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
    `sort_no`         int             NOT NULL DEFAULT 0 COMMENT '排序号，越小越靠前',
    `remark`          varchar(500)             DEFAULT NULL COMMENT '备注',
    `create_by`       bigint unsigned          DEFAULT NULL COMMENT '创建人ID',
    `create_time`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       bigint unsigned          DEFAULT NULL COMMENT '更新人ID',
    `update_time`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`       tinyint unsigned NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_clinic_code` (`clinic_code`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_clinic_name` (`clinic_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='诊所表';

-- ----------------------------
-- 部门表
-- ----------------------------
DROP TABLE IF EXISTS `sys_department`;
CREATE TABLE `sys_department` (
    `id`              bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `clinic_id`       bigint unsigned NOT NULL COMMENT '所属诊所ID，关联sys_clinic.id',
    `dept_name`       varchar(128)    NOT NULL COMMENT '部门名称',
    `parent_id`       bigint unsigned NOT NULL DEFAULT 0 COMMENT '父部门ID，0表示顶级',
    `sort_no`         int             NOT NULL DEFAULT 0 COMMENT '排序号',
    `status`          tinyint unsigned NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
    `remark`          varchar(500)             DEFAULT NULL COMMENT '备注',
    `create_by`       bigint unsigned          DEFAULT NULL COMMENT '创建人ID',
    `create_time`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       bigint unsigned          DEFAULT NULL COMMENT '更新人ID',
    `update_time`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`       tinyint unsigned NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    PRIMARY KEY (`id`),
    KEY `idx_clinic_id` (`clinic_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ----------------------------
-- 员工表
-- ----------------------------
DROP TABLE IF EXISTS `sys_staff`;
CREATE TABLE `sys_staff` (
    `id`              bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `staff_name`      varchar(64)     NOT NULL COMMENT '姓名',
    `job_no`          varchar(32)     NOT NULL COMMENT '工号',
    `gender`          tinyint unsigned NOT NULL DEFAULT 0 COMMENT '性别：0未知 1男 2女',
    `birthday`        date                     DEFAULT NULL COMMENT '生日',
    `mobile`          varchar(20)     NOT NULL COMMENT '手机号码',
    `clinic_id`       bigint unsigned NOT NULL COMMENT '工作诊所ID，关联sys_clinic.id',
    `dept_id`         bigint unsigned          DEFAULT NULL COMMENT '所属部门ID，关联sys_department.id',
    `position_name`   varchar(64)              DEFAULT NULL COMMENT '岗位名称',
    `work_status`     tinyint unsigned NOT NULL DEFAULT 1 COMMENT '在职状态：1在职 0离职',
    `online_status`   tinyint unsigned NOT NULL DEFAULT 1 COMMENT '账号上线状态：1上线 0下线',
    `mobile_link`     tinyint unsigned NOT NULL DEFAULT 0 COMMENT '手机关联：1允许 0不允许',
    `id_type`         varchar(32)              DEFAULT NULL COMMENT '证件类型，如：身份证',
    `id_no`           varchar(64)              DEFAULT NULL COMMENT '证件号码',
    `login_name`      varchar(64)     NOT NULL COMMENT '登录账号',
    `password`        varchar(128)    NOT NULL COMMENT '登录密码（BCrypt）',
    `avatar`          varchar(255)             DEFAULT NULL COMMENT '头像地址',
    `email`           varchar(128)             DEFAULT NULL COMMENT '邮箱',
    `remark`          varchar(500)             DEFAULT NULL COMMENT '备注',
    `create_by`       bigint unsigned          DEFAULT NULL COMMENT '创建人ID',
    `create_time`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       bigint unsigned          DEFAULT NULL COMMENT '更新人ID',
    `update_time`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`       tinyint unsigned NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_job_no` (`job_no`),
    UNIQUE KEY `uk_login_name` (`login_name`),
    KEY `idx_mobile` (`mobile`),
    KEY `idx_clinic_id` (`clinic_id`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_staff_name` (`staff_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- ----------------------------
-- 角色表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id`              bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_code`       varchar(64)     NOT NULL COMMENT '角色编码',
    `role_name`       varchar(64)     NOT NULL COMMENT '角色名称',
    `sort_no`         int             NOT NULL DEFAULT 0 COMMENT '排序号，越小越靠前',
    `status`          tinyint unsigned NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
    `remark`          varchar(500)             DEFAULT NULL COMMENT '备注',
    `create_by`       bigint unsigned          DEFAULT NULL COMMENT '创建人ID',
    `create_time`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       bigint unsigned          DEFAULT NULL COMMENT '更新人ID',
    `update_time`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`       tinyint unsigned NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`),
    KEY `idx_role_name` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- 权限/菜单表
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
    `id`              bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `parent_id`       bigint unsigned NOT NULL DEFAULT 0 COMMENT '父权限ID，0表示顶级',
    `perm_name`       varchar(128)    NOT NULL COMMENT '权限名称',
    `perm_code`       varchar(128)             DEFAULT NULL COMMENT '权限编码（按钮级鉴权用）',
    `perm_type`       tinyint unsigned NOT NULL COMMENT '权限类型：1目录 2菜单 3按钮',
    `platform`        tinyint unsigned NOT NULL DEFAULT 1 COMMENT '平台：1网页版 2移动版 3数据权限',
    `path`            varchar(255)             DEFAULT NULL COMMENT '路由路径',
    `component`       varchar(255)             DEFAULT NULL COMMENT '前端组件',
    `icon`            varchar(64)              DEFAULT NULL COMMENT '图标',
    `sort_no`         int             NOT NULL DEFAULT 0 COMMENT '排序号',
    `status`          tinyint unsigned NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
    `remark`          varchar(500)             DEFAULT NULL COMMENT '备注',
    `create_by`       bigint unsigned          DEFAULT NULL COMMENT '创建人ID',
    `create_time`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       bigint unsigned          DEFAULT NULL COMMENT '更新人ID',
    `update_time`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`       tinyint unsigned NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_platform` (`platform`),
    KEY `idx_perm_code` (`perm_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限菜单表';

-- ----------------------------
-- 员工角色关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_staff_role`;
CREATE TABLE `sys_staff_role` (
    `id`              bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `staff_id`        bigint unsigned NOT NULL COMMENT '员工ID，关联sys_staff.id',
    `role_id`         bigint unsigned NOT NULL COMMENT '角色ID，关联sys_role.id',
    `create_by`       bigint unsigned          DEFAULT NULL COMMENT '创建人ID',
    `create_time`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       bigint unsigned          DEFAULT NULL COMMENT '更新人ID',
    `update_time`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`       tinyint unsigned NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_staff_role` (`staff_id`, `role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工角色关联表';

-- ----------------------------
-- 角色权限关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
    `id`              bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_id`         bigint unsigned NOT NULL COMMENT '角色ID，关联sys_role.id',
    `permission_id`   bigint unsigned NOT NULL COMMENT '权限ID，关联sys_permission.id',
    `create_by`       bigint unsigned          DEFAULT NULL COMMENT '创建人ID',
    `create_time`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       bigint unsigned          DEFAULT NULL COMMENT '更新人ID',
    `update_time`     datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_delete`       tinyint unsigned NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ----------------------------
-- 初始化数据
-- ----------------------------
INSERT INTO `sys_clinic` (`id`, `clinic_code`, `clinic_name`, `parent_id`, `contact_name`, `contact_phone`, `status`, `sort_no`)
VALUES (1, 'YJ001', '宇健口腔总院', 0, '管理员', '13800000000', 1, 1);

INSERT INTO `sys_department` (`id`, `clinic_id`, `dept_name`, `parent_id`, `sort_no`, `status`)
VALUES (1, 1, '综合管理部', 0, 1, 1);

-- 密码为 Yujian@123 的 BCrypt（示例哈希，实际以运行时编码为准）
INSERT INTO `sys_staff` (`id`, `staff_name`, `job_no`, `gender`, `mobile`, `clinic_id`, `dept_id`,
                         `position_name`, `work_status`, `online_status`, `mobile_link`, `id_type`,
                         `login_name`, `password`)
VALUES (1, 'Administrator', '001', 1, '13800000000', 1, 1,
        '管理员', 1, 1, 1, '身份证',
        'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi');

INSERT INTO `sys_role` (`id`, `role_code`, `role_name`, `sort_no`, `status`) VALUES
(1, 'ADMIN', '管理员', 1, 1),
(2, 'DIRECTOR', '主任', 2, 1),
(3, 'DOCTOR', '医生', 3, 1),
(4, 'FRONT', '前台', 4, 1),
(5, 'NURSE', '护士', 5, 1),
(6, 'CASHIER', '收银员', 6, 1),
(7, 'TECH', '技师', 7, 1);

INSERT INTO `sys_staff_role` (`staff_id`, `role_id`) VALUES (1, 1);

INSERT INTO `sys_permission` (`id`, `parent_id`, `perm_name`, `perm_code`, `perm_type`, `platform`, `path`, `sort_no`, `status`) VALUES
(1, 0, '系统管理', 'system', 1, 1, '/system', 90, 1),
(2, 1, '诊所管理', 'system:clinic', 2, 1, '/system/clinic', 1, 1),
(3, 1, '员工管理', 'system:staff', 2, 1, '/system/staff', 2, 1),
(4, 1, '角色设置', 'system:role', 2, 1, '/system/role', 3, 1),
(5, 1, '权限管理', 'system:permission', 2, 1, '/system/permission', 4, 1),
(10, 2, '诊所新增', 'system:clinic:add', 3, 1, NULL, 1, 1),
(11, 2, '诊所编辑', 'system:clinic:edit', 3, 1, NULL, 2, 1),
(12, 2, '诊所删除', 'system:clinic:delete', 3, 1, NULL, 3, 1),
(20, 3, '员工新增', 'system:staff:add', 3, 1, NULL, 1, 1),
(21, 3, '员工编辑', 'system:staff:edit', 3, 1, NULL, 2, 1),
(22, 3, '员工删除', 'system:staff:delete', 3, 1, NULL, 3, 1),
(23, 3, '重置密码', 'system:staff:resetPwd', 3, 1, NULL, 4, 1),
(30, 4, '角色新增', 'system:role:add', 3, 1, NULL, 1, 1),
(31, 4, '角色编辑', 'system:role:edit', 3, 1, NULL, 2, 1),
(32, 4, '角色删除', 'system:role:delete', 3, 1, NULL, 3, 1),
(33, 4, '分配权限', 'system:role:assign', 3, 1, NULL, 4, 1);

INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `sys_permission` WHERE `is_delete` = 0;
