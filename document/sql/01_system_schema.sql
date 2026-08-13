-- ============================================================
-- 宇健口腔医疗系统 - 系统管理表结构
-- Database: yujian
-- Charset: utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS `yujian` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `yujian`;

-- ----------------------------
-- 1. 诊所表
-- ----------------------------
DROP TABLE IF EXISTS `sys_clinic`;
CREATE TABLE `sys_clinic` (
  `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '诊所ID',
  `parent_id`       BIGINT(20)   DEFAULT 0 COMMENT '父诊所ID（0为总部）',
  `clinic_name`     VARCHAR(100) NOT NULL COMMENT '诊所名称',
  `clinic_code`     VARCHAR(50)  NOT NULL COMMENT '诊所编码',
  `short_name`      VARCHAR(50)  DEFAULT NULL COMMENT '诊所简称',
  `contact_name`    VARCHAR(50)  DEFAULT NULL COMMENT '联系人',
  `contact_phone`   VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
  `province`        VARCHAR(50)  DEFAULT NULL COMMENT '省',
  `city`            VARCHAR(50)  DEFAULT NULL COMMENT '市',
  `district`        VARCHAR(50)  DEFAULT NULL COMMENT '区',
  `address`         VARCHAR(255) DEFAULT NULL COMMENT '详细地址',
  `business_hours`  VARCHAR(100) DEFAULT NULL COMMENT '营业时间',
  `logo`            VARCHAR(255) DEFAULT NULL COMMENT 'Logo',
  `sort_order`      INT(11)      DEFAULT 0 COMMENT '排序',
  `status`          TINYINT(1)   DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `open_date`       DATE         DEFAULT NULL COMMENT '开业日期',
  `create_by`       BIGINT(20)   DEFAULT NULL COMMENT '创建人',
  `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`       BIGINT(20)   DEFAULT NULL COMMENT '更新人',
  `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`          VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `del_flag`        TINYINT(1)   DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_clinic_code` (`clinic_code`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='诊所表';

-- ----------------------------
-- 2. 部门表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `clinic_id`       BIGINT(20)   NOT NULL COMMENT '所属诊所ID',
  `parent_id`       BIGINT(20)   DEFAULT 0 COMMENT '父部门ID',
  `dept_name`       VARCHAR(100) NOT NULL COMMENT '部门名称',
  `dept_code`       VARCHAR(50)  DEFAULT NULL COMMENT '部门编码',
  `leader`          VARCHAR(50)  DEFAULT NULL COMMENT '负责人',
  `phone`           VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
  `sort_order`      INT(11)      DEFAULT 0 COMMENT '排序',
  `status`          TINYINT(1)   DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `create_by`       BIGINT(20)   DEFAULT NULL,
  `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_by`       BIGINT(20)   DEFAULT NULL,
  `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remark`          VARCHAR(500) DEFAULT NULL,
  `del_flag`        TINYINT(1)   DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_clinic_id` (`clinic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ----------------------------
-- 3. 员工表
-- ----------------------------
DROP TABLE IF EXISTS `sys_employee`;
CREATE TABLE `sys_employee` (
  `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `name`            VARCHAR(50)  NOT NULL COMMENT '姓名',
  `emp_no`          VARCHAR(50)  NOT NULL COMMENT '工号',
  `username`        VARCHAR(50)  NOT NULL COMMENT '登录账号',
  `password`        VARCHAR(100) NOT NULL COMMENT '密码',
  `gender`          TINYINT(1)   DEFAULT 2 COMMENT '性别（0女 1男 2未知）',
  `birthday`        DATE         DEFAULT NULL COMMENT '生日',
  `mobile`          VARCHAR(20)  DEFAULT NULL COMMENT '手机号码',
  `email`           VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `clinic_id`       BIGINT(20)   DEFAULT NULL COMMENT '工作诊所ID',
  `dept_id`         BIGINT(20)   DEFAULT NULL COMMENT '所属部门ID',
  `position`        VARCHAR(50)  DEFAULT NULL COMMENT '岗位',
  `employ_status`   TINYINT(1)   DEFAULT 1 COMMENT '在职状态（1在职 0离职）',
  `mobile_link`     TINYINT(1)   DEFAULT 0 COMMENT '手机关联（1允许 0不允许）',
  `id_type`         VARCHAR(30)  DEFAULT NULL COMMENT '证件类型',
  `id_number`       VARCHAR(50)  DEFAULT NULL COMMENT '证件号码',
  `avatar`          VARCHAR(255) DEFAULT NULL COMMENT '头像',
  `entry_date`      DATE         DEFAULT NULL COMMENT '入职日期',
  `leave_date`      DATE         DEFAULT NULL COMMENT '离职日期',
  `sort_order`      INT(11)      DEFAULT 0 COMMENT '排序',
  `status`          TINYINT(1)   DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `create_by`       BIGINT(20)   DEFAULT NULL,
  `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_by`       BIGINT(20)   DEFAULT NULL,
  `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remark`          VARCHAR(500) DEFAULT NULL,
  `del_flag`        TINYINT(1)   DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_emp_no` (`emp_no`),
  KEY `idx_clinic_id` (`clinic_id`),
  KEY `idx_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- ----------------------------
-- 4. 角色表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name`       VARCHAR(50)  NOT NULL COMMENT '角色名称',
  `role_key`        VARCHAR(50)  NOT NULL COMMENT '角色编码',
  `sort_order`      INT(11)      DEFAULT 0 COMMENT '排序',
  `data_scope`      TINYINT(1)   DEFAULT 1 COMMENT '数据范围（1全部 2本诊所 3本部门 4仅本人 5自定义）',
  `status`          TINYINT(1)   DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `create_by`       BIGINT(20)   DEFAULT NULL,
  `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_by`       BIGINT(20)   DEFAULT NULL,
  `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remark`          VARCHAR(500) DEFAULT NULL,
  `del_flag`        TINYINT(1)   DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- 5. 菜单/权限表
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name`       VARCHAR(50)  NOT NULL COMMENT '菜单名称',
  `parent_id`       BIGINT(20)   DEFAULT 0 COMMENT '父菜单ID',
  `sort_order`      INT(11)      DEFAULT 0 COMMENT '排序',
  `path`            VARCHAR(200) DEFAULT NULL COMMENT '路由地址',
  `component`       VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
  `perms`           VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
  `menu_type`       CHAR(1)      DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `platform`        VARCHAR(20)  DEFAULT 'web' COMMENT '平台（web/mobile）',
  `icon`            VARCHAR(100) DEFAULT NULL COMMENT '菜单图标',
  `visible`         TINYINT(1)   DEFAULT 0 COMMENT '是否显示（0显示 1隐藏）',
  `status`          TINYINT(1)   DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `create_by`       BIGINT(20)   DEFAULT NULL,
  `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_by`       BIGINT(20)   DEFAULT NULL,
  `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remark`          VARCHAR(500) DEFAULT NULL,
  `del_flag`        TINYINT(1)   DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_platform` (`platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- ----------------------------
-- 6. 员工角色关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_employee_role`;
CREATE TABLE `sys_employee_role` (
  `id`              BIGINT(20) NOT NULL AUTO_INCREMENT,
  `employee_id`     BIGINT(20) NOT NULL COMMENT '员工ID',
  `role_id`         BIGINT(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工角色关联表';

-- ----------------------------
-- 7. 角色菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id`              BIGINT(20) NOT NULL AUTO_INCREMENT,
  `role_id`         BIGINT(20) NOT NULL COMMENT '角色ID',
  `menu_id`         BIGINT(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';
