-- ============================================================
-- 宇健口腔医疗系统 - 完整库表结构（统一入口）
-- Database: yujian
-- Charset: utf8mb4
-- 规范：t_ 表前缀 / is_delete / TINYINT(4)
-- ============================================================

CREATE DATABASE IF NOT EXISTS `yujian` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `yujian`;

-- ----------------------------
-- 关联 / 日志表先删（无依赖顺序）
-- ----------------------------
DROP TABLE IF EXISTS `t_employee_role`;
DROP TABLE IF EXISTS `t_employee_clinic`;
DROP TABLE IF EXISTS `t_role_menu`;
DROP TABLE IF EXISTS `t_patient_tag_rel`;
DROP TABLE IF EXISTS `t_appointment_log`;
DROP TABLE IF EXISTS `t_patient_log`;
DROP TABLE IF EXISTS `t_patient_relation`;
DROP TABLE IF EXISTS `t_consult_record`;
DROP TABLE IF EXISTS `t_treatment_plan`;
DROP TABLE IF EXISTS `t_patient_file`;
DROP TABLE IF EXISTS `t_follow_up`;
DROP TABLE IF EXISTS `t_charge_record`;
DROP TABLE IF EXISTS `t_treatment_record`;
DROP TABLE IF EXISTS `t_medical_record`;
DROP TABLE IF EXISTS `t_visit`;
DROP TABLE IF EXISTS `t_schedule`;
DROP TABLE IF EXISTS `t_appointment`;
DROP TABLE IF EXISTS `t_patient`;
DROP TABLE IF EXISTS `t_treatment_item`;
DROP TABLE IF EXISTS `t_patient_source`;
DROP TABLE IF EXISTS `t_patient_tag`;
DROP TABLE IF EXISTS `t_dict_data`;
DROP TABLE IF EXISTS `t_dict_type`;
DROP TABLE IF EXISTS `t_menu`;
DROP TABLE IF EXISTS `t_role`;
DROP TABLE IF EXISTS `t_employee`;
DROP TABLE IF EXISTS `t_dept`;
DROP TABLE IF EXISTS `t_clinic`;

-- ----------------------------
-- 1. 诊所表
-- ----------------------------
CREATE TABLE `t_clinic` (
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
  `status`          TINYINT(4)   DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `open_date`       DATE         DEFAULT NULL COMMENT '开业日期',
  `create_by`       BIGINT(20)   DEFAULT NULL COMMENT '创建人',
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`       BIGINT(20)   DEFAULT NULL COMMENT '更新人',
  `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`          VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `is_delete`       TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_clinic_code` (`clinic_code`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='诊所表';

-- ----------------------------
-- 2. 部门表
-- ----------------------------
CREATE TABLE `t_dept` (
  `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `clinic_id`       BIGINT(20)   NOT NULL COMMENT '所属诊所ID',
  `parent_id`       BIGINT(20)   DEFAULT 0 COMMENT '父部门ID',
  `dept_name`       VARCHAR(100) NOT NULL COMMENT '部门名称',
  `dept_code`       VARCHAR(50)  DEFAULT NULL COMMENT '部门编码',
  `leader`          VARCHAR(50)  DEFAULT NULL COMMENT '负责人',
  `phone`           VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
  `sort_order`      INT(11)      DEFAULT 0 COMMENT '排序',
  `status`          TINYINT(4)   DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `create_by`       BIGINT(20)   DEFAULT NULL COMMENT '创建人',
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`       BIGINT(20)   DEFAULT NULL COMMENT '更新人',
  `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`          VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `is_delete`       TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_clinic_id` (`clinic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ----------------------------
-- 3. 员工表
-- ----------------------------
CREATE TABLE `t_employee` (
  `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `name`            VARCHAR(50)  NOT NULL COMMENT '姓名',
  `emp_no`          VARCHAR(50)  NOT NULL COMMENT '工号',
  `username`        VARCHAR(50)  NOT NULL COMMENT '登录账号',
  `password`        VARCHAR(100) NOT NULL COMMENT '密码',
  `gender`          TINYINT(4)   DEFAULT 2 COMMENT '性别（0女 1男 2未知）',
  `birthday`        DATE         DEFAULT NULL COMMENT '生日',
  `mobile`          VARCHAR(20)  DEFAULT NULL COMMENT '手机号码',
  `email`           VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `clinic_id`       BIGINT(20)   DEFAULT NULL COMMENT '默认/主诊所ID（冗余，关联以 t_employee_clinic 为准）',
  `position`        VARCHAR(50)  DEFAULT NULL COMMENT '岗位',
  `employ_status`   TINYINT(4)   DEFAULT 1 COMMENT '在职状态（1在职 0离职）',
  `id_type`         VARCHAR(30)  DEFAULT NULL COMMENT '证件类型',
  `id_number`       VARCHAR(50)  DEFAULT NULL COMMENT '证件号码',
  `avatar`          VARCHAR(255) DEFAULT NULL COMMENT '头像',
  `entry_date`      DATE         DEFAULT NULL COMMENT '入职日期',
  `leave_date`      DATE         DEFAULT NULL COMMENT '离职日期',
  `sort_order`      INT(11)      DEFAULT 0 COMMENT '排序',
  `status`          TINYINT(4)   DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `create_by`       BIGINT(20)   DEFAULT NULL COMMENT '创建人',
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`       BIGINT(20)   DEFAULT NULL COMMENT '更新人',
  `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`          VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `is_delete`       TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_emp_no` (`emp_no`),
  KEY `idx_clinic_id` (`clinic_id`),
  KEY `idx_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- ----------------------------
-- 4. 角色表
-- ----------------------------
CREATE TABLE `t_role` (
  `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name`       VARCHAR(50)  NOT NULL COMMENT '角色名称',
  `role_key`        VARCHAR(50)  NOT NULL COMMENT '角色编码',
  `sort_order`      INT(11)      DEFAULT 0 COMMENT '排序',
  `data_scope`      TINYINT(4)   DEFAULT 1 COMMENT '数据范围（1全部 2本诊所 3本部门 4仅本人 5自定义）',
  `status`          TINYINT(4)   DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `create_by`       BIGINT(20)   DEFAULT NULL COMMENT '创建人',
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`       BIGINT(20)   DEFAULT NULL COMMENT '更新人',
  `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`          VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `is_delete`       TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- 5. 菜单/权限表
-- ----------------------------
CREATE TABLE `t_menu` (
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
  `visible`         TINYINT(4)   DEFAULT 0 COMMENT '是否显示（0显示 1隐藏）',
  `status`          TINYINT(4)   DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `create_by`       BIGINT(20)   DEFAULT NULL COMMENT '创建人',
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`       BIGINT(20)   DEFAULT NULL COMMENT '更新人',
  `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`          VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `is_delete`       TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_platform` (`platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- ----------------------------
-- 6. 员工角色关联表
-- ----------------------------
CREATE TABLE `t_employee_role` (
  `id`              BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `employee_id`     BIGINT(20) NOT NULL COMMENT '员工ID',
  `role_id`         BIGINT(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工角色关联表';

-- ----------------------------
-- 6.1 员工诊所关联表（一对多）
-- ----------------------------
CREATE TABLE `t_employee_clinic` (
  `id`              BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `employee_id`     BIGINT(20) NOT NULL COMMENT '员工ID',
  `clinic_id`       BIGINT(20) NOT NULL COMMENT '诊所ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_employee_clinic` (`employee_id`, `clinic_id`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_clinic_id` (`clinic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工诊所关联表';

-- ----------------------------
-- 7. 角色菜单关联表
-- ----------------------------
CREATE TABLE `t_role_menu` (
  `id`              BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id`         BIGINT(20) NOT NULL COMMENT '角色ID',
  `menu_id`         BIGINT(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- ----------------------------
-- 8. 患者表
-- ----------------------------
CREATE TABLE `t_patient` (
  `id`                 BIGINT(20)     NOT NULL AUTO_INCREMENT COMMENT '患者ID',
  `clinic_id`          BIGINT(20)     NOT NULL COMMENT '诊所ID',
  `medical_record_no`  VARCHAR(50)    NOT NULL COMMENT '病历号',
  `name`               VARCHAR(50)    NOT NULL COMMENT '姓名',
  `name_pinyin`        VARCHAR(100)   DEFAULT NULL COMMENT '拼音首字母',
  `gender`             TINYINT(4)     DEFAULT 2 COMMENT '0女 1男 2未知',
  `star_level`         TINYINT(4)     DEFAULT 0 COMMENT '星级1-5',
  `birthday`           DATE           DEFAULT NULL COMMENT '生日',
  `age`                INT(11)        DEFAULT NULL COMMENT '年龄',
  `mobile`             VARCHAR(20)    NOT NULL COMMENT '手机号',
  `mobile_relation`    VARCHAR(20)    DEFAULT '本人' COMMENT '手机关系',
  `phone`              VARCHAR(20)    DEFAULT NULL COMMENT '其他电话',
  `phone_relation`     VARCHAR(20)    DEFAULT NULL COMMENT '其他电话关系',
  `id_number`          VARCHAR(30)    DEFAULT NULL COMMENT '证件号码',
  `medicare_card_no`   VARCHAR(50)    DEFAULT NULL COMMENT '医保卡号',
  `medicare_balance`   DECIMAL(12,2)  DEFAULT 0.00 COMMENT '医保余额',
  `province`           VARCHAR(50)    DEFAULT NULL COMMENT '省',
  `city`               VARCHAR(50)    DEFAULT NULL COMMENT '市',
  `district`           VARCHAR(50)    DEFAULT NULL COMMENT '区',
  `address`            VARCHAR(255)   DEFAULT NULL COMMENT '详细地址',
  `residence`          VARCHAR(100)   DEFAULT NULL COMMENT '居住区域',
  `avatar`             VARCHAR(255)   DEFAULT NULL COMMENT '头像',
  `patient_type`       TINYINT(4)     DEFAULT 1 COMMENT '1普通 2临时',
  `patient_category`   VARCHAR(50)    DEFAULT NULL COMMENT '患者分类',
  `source_id`          BIGINT(20)     DEFAULT NULL COMMENT '患者来源',
  `introducer_type`    VARCHAR(30)    DEFAULT NULL COMMENT '介绍人类型',
  `introducer_id`      BIGINT(20)     DEFAULT NULL COMMENT '介绍人ID',
  `introducer_name`    VARCHAR(50)    DEFAULT NULL COMMENT '介绍人姓名',
  `doctor_id`          BIGINT(20)     DEFAULT NULL COMMENT '主治医生',
  `first_doctor_id`    BIGINT(20)     DEFAULT NULL COMMENT '初诊医生',
  `first_visit_time`   DATETIME       DEFAULT NULL COMMENT '初诊时间',
  `next_visit_time`    DATETIME       DEFAULT NULL COMMENT '下次就诊时间',
  `last_doctor_id`     BIGINT(20)     DEFAULT NULL COMMENT '最近就诊医生',
  `last_visit_time`    DATETIME       DEFAULT NULL COMMENT '最近就诊时间',
  `owe_amount`         DECIMAL(12,2)  DEFAULT 0.00 COMMENT '欠费金额',
  `paid_amount`        DECIMAL(12,2)  DEFAULT 0.00 COMMENT '已付金额',
  `prepay_amount`      DECIMAL(12,2)  DEFAULT 0.00 COMMENT '预交款余额',
  `total_amount`       DECIMAL(12,2)  DEFAULT 0.00 COMMENT '消费总额',
  `avg_amount`         DECIMAL(12,2)  DEFAULT 0.00 COMMENT '客单价',
  `referral_count`     INT(11)        DEFAULT 0 COMMENT '转介绍人数',
  `creator_name`       VARCHAR(50)    DEFAULT NULL COMMENT '创建人姓名',
  `status`             TINYINT(4)     DEFAULT 0 COMMENT '0正常 1归档',
  `create_by`          BIGINT(20)     DEFAULT NULL COMMENT '创建人',
  `create_time`        DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`          BIGINT(20)     DEFAULT NULL COMMENT '更新人',
  `update_time`        DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`             VARCHAR(500)   DEFAULT NULL COMMENT '备注',
  `is_delete`          TINYINT(4)     NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_clinic_mrn` (`clinic_id`, `medical_record_no`),
  KEY `idx_mobile` (`mobile`),
  KEY `idx_name` (`name`),
  KEY `idx_doctor` (`doctor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者表';

-- ----------------------------
-- 9. 预约表
-- ----------------------------
CREATE TABLE `t_appointment` (
  `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `clinic_id`       BIGINT(20)   NOT NULL COMMENT '诊所ID',
  `patient_id`      BIGINT(20)   NOT NULL COMMENT '患者ID',
  `doctor_id`       BIGINT(20)   DEFAULT NULL COMMENT '预约医生',
  `nurse_id`        BIGINT(20)   DEFAULT NULL COMMENT '护士',
  `consultant_id`   BIGINT(20)   DEFAULT NULL COMMENT '咨询师',
  `start_time`      DATETIME     NOT NULL COMMENT '开始时间',
  `end_time`        DATETIME     NOT NULL COMMENT '结束时间',
  `visit_type`      TINYINT(4)   DEFAULT 2 COMMENT '1初诊 2复诊',
  `status`          TINYINT(4)   DEFAULT 1 COMMENT '1已预约 2已确认 3已到达 4治疗中 5已离开 6已过期 7已流失 8预约未到',
  `item_id`         BIGINT(20)   DEFAULT NULL COMMENT '项目ID',
  `item_name`       VARCHAR(100) DEFAULT NULL COMMENT '项目名称',
  `triaged`         TINYINT(4)   DEFAULT 0 COMMENT '是否分诊',
  `registered`      TINYINT(4)   DEFAULT 0 COMMENT '是否挂号',
  `appoint_type`    VARCHAR(30)  DEFAULT 'normal' COMMENT '预约类型 normal普通 walkin散客 online网络',
  `appoint_source`  VARCHAR(30)  DEFAULT 'clinic' COMMENT '预约来源 clinic院内 online网络 wechat微信',
  `cancel_reason`   VARCHAR(200) DEFAULT NULL COMMENT '取消/删除原因',
  `item_color`      VARCHAR(20)  DEFAULT NULL COMMENT '项目颜色（日历块）',
  `creator_name`    VARCHAR(50)  DEFAULT NULL COMMENT '预约人姓名冗余',
  `create_by`       BIGINT(20)   DEFAULT NULL COMMENT '创建人',
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`       BIGINT(20)   DEFAULT NULL COMMENT '更新人',
  `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`          VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `is_delete`       TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_clinic_time` (`clinic_id`, `start_time`),
  KEY `idx_doctor_time` (`doctor_id`, `start_time`),
  KEY `idx_patient` (`patient_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- ----------------------------
-- 10. 字典类型
-- ----------------------------
CREATE TABLE `t_dict_type` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dict_name`   VARCHAR(100) NOT NULL COMMENT '字典名称',
  `dict_type`   VARCHAR(100) NOT NULL COMMENT '字典类型',
  `status`      TINYINT(4)   DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `create_by`   BIGINT(20)   DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`   BIGINT(20)   DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `is_delete`   TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型';

-- ----------------------------
-- 11. 字典数据
-- ----------------------------
CREATE TABLE `t_dict_data` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dict_type`   VARCHAR(100) NOT NULL COMMENT '字典类型',
  `dict_label`  VARCHAR(100) NOT NULL COMMENT '字典标签',
  `dict_value`  VARCHAR(100) NOT NULL COMMENT '字典键值',
  `css_class`   VARCHAR(50)  DEFAULT NULL COMMENT '样式',
  `sort_order`  INT(11)      DEFAULT 0 COMMENT '排序',
  `status`      TINYINT(4)   DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `create_by`   BIGINT(20)   DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`   BIGINT(20)   DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `is_delete`   TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据';

-- ----------------------------
-- 12. 患者标签
-- ----------------------------
CREATE TABLE `t_patient_tag` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `clinic_id`   BIGINT(20)   NOT NULL COMMENT '诊所ID',
  `tag_name`    VARCHAR(50)  NOT NULL COMMENT '标签名',
  `tag_color`   VARCHAR(20)  DEFAULT NULL COMMENT '颜色',
  `sort_order`  INT(11)      DEFAULT 0 COMMENT '排序',
  `status`      TINYINT(4)   DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `create_by`   BIGINT(20)   DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`   BIGINT(20)   DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `is_delete`   TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者标签';

-- ----------------------------
-- 13. 患者标签关联
-- ----------------------------
CREATE TABLE `t_patient_tag_rel` (
  `id`         BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `patient_id` BIGINT(20) NOT NULL COMMENT '患者ID',
  `tag_id`     BIGINT(20) NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`),
  KEY `idx_patient` (`patient_id`),
  KEY `idx_tag` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者标签关联';

-- ----------------------------
-- 14. 患者来源（树）
-- ----------------------------
CREATE TABLE `t_patient_source` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `clinic_id`   BIGINT(20)   NOT NULL COMMENT '诊所ID',
  `parent_id`   BIGINT(20)   DEFAULT 0 COMMENT '父来源ID',
  `source_name` VARCHAR(100) NOT NULL COMMENT '来源名称',
  `sort_order`  INT(11)      DEFAULT 0 COMMENT '排序',
  `status`      TINYINT(4)   DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `create_by`   BIGINT(20)   DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`   BIGINT(20)   DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `is_delete`   TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者来源';

-- ----------------------------
-- 15. 诊疗/预约项目
-- ----------------------------
CREATE TABLE `t_treatment_item` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `clinic_id`   BIGINT(20)   NOT NULL COMMENT '诊所ID',
  `item_name`   VARCHAR(100) NOT NULL COMMENT '项目名称',
  `item_code`   VARCHAR(50)  DEFAULT NULL COMMENT '项目编码',
  `duration`    INT(11)      DEFAULT 30 COMMENT '默认时长分钟',
  `item_color`  VARCHAR(20)  DEFAULT '#409EFF' COMMENT '日历展示色',
  `sort_order`  INT(11)      DEFAULT 0 COMMENT '排序',
  `status`      TINYINT(4)   DEFAULT 0 COMMENT '状态（0正常 1停用）',
  `create_by`   BIGINT(20)   DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`   BIGINT(20)   DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `is_delete`   TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='诊疗预约项目';

-- ----------------------------
-- 16. 预约操作日志
-- ----------------------------
CREATE TABLE `t_appointment_log` (
  `id`             BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `appointment_id` BIGINT(20)   NOT NULL COMMENT '预约ID',
  `clinic_id`      BIGINT(20)   DEFAULT NULL COMMENT '诊所ID',
  `action`         VARCHAR(50)  NOT NULL COMMENT 'create/update/confirm/cancel/arrive/seat/left/delete/restore',
  `before_status`  TINYINT(4)   DEFAULT NULL COMMENT '变更前状态',
  `after_status`   TINYINT(4)   DEFAULT NULL COMMENT '变更后状态',
  `content`        VARCHAR(500) DEFAULT NULL COMMENT '内容',
  `operator_id`    BIGINT(20)   DEFAULT NULL COMMENT '操作人ID',
  `operator_name`  VARCHAR(50)  DEFAULT NULL COMMENT '操作人姓名',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_appointment_id` (`appointment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约操作日志';

-- ----------------------------
-- 17. 员工日程
-- ----------------------------
CREATE TABLE `t_schedule` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `clinic_id`   BIGINT(20)   NOT NULL COMMENT '诊所ID',
  `doctor_id`   BIGINT(20)   DEFAULT NULL COMMENT '医生ID',
  `title`       VARCHAR(100) NOT NULL COMMENT '日程标题',
  `start_time`  DATETIME     NOT NULL COMMENT '开始时间',
  `end_time`    DATETIME     NOT NULL COMMENT '结束时间',
  `color`       VARCHAR(20)  DEFAULT '#909399' COMMENT '颜色',
  `status`      TINYINT(4)   DEFAULT 0 COMMENT '0正常 1取消',
  `create_by`   BIGINT(20)   DEFAULT NULL COMMENT '创建人',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`   BIGINT(20)   DEFAULT NULL COMMENT '更新人',
  `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `is_delete`   TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_clinic_time` (`clinic_id`, `start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工日程';

-- ----------------------------
-- 18. 患者亲友关系
-- ----------------------------
CREATE TABLE `t_patient_relation` (
  `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `clinic_id`       BIGINT(20)   NOT NULL COMMENT '诊所ID',
  `patient_id`      BIGINT(20)   NOT NULL COMMENT '当前患者',
  `related_id`      BIGINT(20)   NOT NULL COMMENT '关联患者',
  `relation_type`   VARCHAR(30)  NOT NULL COMMENT '父子/夫妻/兄弟等',
  `create_by`       BIGINT(20)   DEFAULT NULL COMMENT '创建人',
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`          VARCHAR(200) DEFAULT NULL COMMENT '备注',
  `is_delete`       TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_patient` (`patient_id`),
  KEY `idx_related` (`related_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者亲友关系';

-- ----------------------------
-- 19. 患者操作日志
-- ----------------------------
CREATE TABLE `t_patient_log` (
  `id`             BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `patient_id`     BIGINT(20)   NOT NULL COMMENT '患者ID',
  `clinic_id`      BIGINT(20)   DEFAULT NULL COMMENT '诊所ID',
  `action`         VARCHAR(50)  NOT NULL COMMENT '动作',
  `content`        VARCHAR(500) DEFAULT NULL COMMENT '内容',
  `operator_id`    BIGINT(20)   DEFAULT NULL COMMENT '操作人ID',
  `operator_name`  VARCHAR(50)  DEFAULT NULL COMMENT '操作人姓名',
  `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_patient` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者操作日志';

-- ----------------------------
-- 20. 就诊记录
-- ----------------------------
CREATE TABLE `t_visit` (
  `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `clinic_id`       BIGINT(20)   NOT NULL COMMENT '诊所ID',
  `patient_id`      BIGINT(20)   NOT NULL COMMENT '患者ID',
  `appointment_id`  BIGINT(20)   DEFAULT NULL COMMENT '预约ID',
  `doctor_id`       BIGINT(20)   DEFAULT NULL COMMENT '医生ID',
  `nurse_id`        BIGINT(20)   DEFAULT NULL COMMENT '护士ID',
  `consultant_id`   BIGINT(20)   DEFAULT NULL COMMENT '咨询师ID',
  `visit_type`      TINYINT(4)   DEFAULT 1 COMMENT '1初诊 2复诊',
  `visit_status`    TINYINT(4)   DEFAULT 1 COMMENT '1待分诊 2咨询中 3治疗中 4待结算 5已完成 6已离开',
  `item_name`       VARCHAR(100) DEFAULT NULL COMMENT '项目名称',
  `start_time`      DATETIME     DEFAULT NULL COMMENT '开始时间',
  `end_time`        DATETIME     DEFAULT NULL COMMENT '结束时间',
  `create_by`       BIGINT(20)   DEFAULT NULL COMMENT '创建人',
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`       BIGINT(20)   DEFAULT NULL COMMENT '更新人',
  `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`          VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `is_delete`       TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_patient` (`patient_id`),
  KEY `idx_clinic_time` (`clinic_id`, `start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='就诊记录';

-- ----------------------------
-- 21. 电子病历
-- ----------------------------
CREATE TABLE `t_medical_record` (
  `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `clinic_id`       BIGINT(20)   NOT NULL COMMENT '诊所ID',
  `patient_id`      BIGINT(20)   NOT NULL COMMENT '患者ID',
  `visit_id`        BIGINT(20)   DEFAULT NULL COMMENT '就诊ID',
  `doctor_id`       BIGINT(20)   DEFAULT NULL COMMENT '医生ID',
  `visit_type`      TINYINT(4)   DEFAULT 1 COMMENT '1初诊 2复诊',
  `visit_time`      DATETIME     DEFAULT NULL COMMENT '就诊时间',
  `chief_complaint` VARCHAR(500) DEFAULT NULL COMMENT '主诉',
  `treatment`       TEXT         COMMENT '治疗/处置内容',
  `advice`          TEXT         COMMENT '医嘱',
  `create_by`       BIGINT(20)   DEFAULT NULL COMMENT '创建人',
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`       BIGINT(20)   DEFAULT NULL COMMENT '更新人',
  `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`          VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `is_delete`       TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_patient` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电子病历';

-- ----------------------------
-- 22. 处置记录
-- ----------------------------
CREATE TABLE `t_treatment_record` (
  `id`              BIGINT(20)    NOT NULL AUTO_INCREMENT COMMENT '主键',
  `clinic_id`       BIGINT(20)    NOT NULL COMMENT '诊所ID',
  `patient_id`      BIGINT(20)    NOT NULL COMMENT '患者ID',
  `visit_id`        BIGINT(20)    DEFAULT NULL COMMENT '就诊ID',
  `doctor_id`       BIGINT(20)    DEFAULT NULL COMMENT '医生ID',
  `nurse_id`        BIGINT(20)    DEFAULT NULL COMMENT '护士ID',
  `item_id`         BIGINT(20)    DEFAULT NULL COMMENT '项目ID',
  `item_name`       VARCHAR(100)  DEFAULT NULL COMMENT '项目名称',
  `tooth_positions` VARCHAR(200)  DEFAULT NULL COMMENT '牙位，逗号分隔',
  `visit_type`      TINYINT(4)    DEFAULT 2 COMMENT '1初诊 2复诊',
  `amount`          DECIMAL(12,2) DEFAULT 0.00 COMMENT '金额',
  `treat_time`      DATETIME      DEFAULT NULL COMMENT '处置时间',
  `create_by`       BIGINT(20)    DEFAULT NULL COMMENT '创建人',
  `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`       BIGINT(20)    DEFAULT NULL COMMENT '更新人',
  `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`          VARCHAR(500)  DEFAULT NULL COMMENT '备注',
  `is_delete`       TINYINT(4)    NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_patient` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处置记录';

-- ----------------------------
-- 23. 收费记录
-- ----------------------------
CREATE TABLE `t_charge_record` (
  `id`              BIGINT(20)    NOT NULL AUTO_INCREMENT COMMENT '主键',
  `clinic_id`       BIGINT(20)    NOT NULL COMMENT '诊所ID',
  `patient_id`      BIGINT(20)    NOT NULL COMMENT '患者ID',
  `visit_id`        BIGINT(20)    DEFAULT NULL COMMENT '就诊ID',
  `charge_no`       VARCHAR(50)   DEFAULT NULL COMMENT '收费单号',
  `total_amount`    DECIMAL(12,2) DEFAULT 0.00 COMMENT '应收金额',
  `paid_amount`     DECIMAL(12,2) DEFAULT 0.00 COMMENT '实收金额',
  `owe_amount`      DECIMAL(12,2) DEFAULT 0.00 COMMENT '欠费金额',
  `pay_method`      VARCHAR(30)   DEFAULT NULL COMMENT 'cash/wechat/alipay/card',
  `charge_status`   TINYINT(4)    DEFAULT 0 COMMENT '0待收 1部分 2已结清',
  `charge_time`     DATETIME      DEFAULT NULL COMMENT '收费时间',
  `cashier_id`      BIGINT(20)    DEFAULT NULL COMMENT '收银员ID',
  `create_by`       BIGINT(20)    DEFAULT NULL COMMENT '创建人',
  `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`       BIGINT(20)    DEFAULT NULL COMMENT '更新人',
  `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`          VARCHAR(500)  DEFAULT NULL COMMENT '备注',
  `is_delete`       TINYINT(4)    NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_patient` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收费记录';

-- ----------------------------
-- 24. 回访记录
-- ----------------------------
CREATE TABLE `t_follow_up` (
  `id`              BIGINT(20)    NOT NULL AUTO_INCREMENT COMMENT '主键',
  `clinic_id`       BIGINT(20)    NOT NULL COMMENT '诊所ID',
  `patient_id`      BIGINT(20)    NOT NULL COMMENT '患者ID',
  `visit_id`        BIGINT(20)    DEFAULT NULL COMMENT '就诊ID',
  `plan_time`       DATETIME      DEFAULT NULL COMMENT '计划回访时间',
  `actual_time`     DATETIME      DEFAULT NULL COMMENT '实际回访时间',
  `follow_type`     VARCHAR(30)   DEFAULT NULL COMMENT '电话/微信/到店',
  `follow_status`   TINYINT(4)    DEFAULT 0 COMMENT '0待回访 1已完成 2已取消',
  `content`         VARCHAR(1000) DEFAULT NULL COMMENT '回访内容',
  `result`          VARCHAR(500)  DEFAULT NULL COMMENT '回访结果',
  `owner_id`        BIGINT(20)    DEFAULT NULL COMMENT '回访人',
  `create_by`       BIGINT(20)    DEFAULT NULL COMMENT '创建人',
  `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`       BIGINT(20)    DEFAULT NULL COMMENT '更新人',
  `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`          VARCHAR(500)  DEFAULT NULL COMMENT '备注',
  `is_delete`       TINYINT(4)    NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_patient` (`patient_id`),
  KEY `idx_plan_time` (`plan_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回访记录';

-- ----------------------------
-- 25. 患者附件（影像/文档/协议）
-- ----------------------------
CREATE TABLE `t_patient_file` (
  `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `clinic_id`       BIGINT(20)   NOT NULL COMMENT '诊所ID',
  `patient_id`      BIGINT(20)   NOT NULL COMMENT '患者ID',
  `visit_id`        BIGINT(20)   DEFAULT NULL COMMENT '就诊ID',
  `file_category`   VARCHAR(30)  NOT NULL COMMENT 'image影像 document文档 agreement协议',
  `file_type`       VARCHAR(50)  DEFAULT NULL COMMENT 'xray/photo/scan/pdf...',
  `file_name`       VARCHAR(200) DEFAULT NULL COMMENT '文件名',
  `file_url`        VARCHAR(500) NOT NULL COMMENT '文件地址',
  `file_size`       BIGINT(20)   DEFAULT NULL COMMENT '文件大小',
  `upload_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `create_by`       BIGINT(20)   DEFAULT NULL COMMENT '创建人',
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`          VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `is_delete`       TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_patient_cat` (`patient_id`, `file_category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者附件（影像/文档/协议）';

-- ----------------------------
-- 26. 治疗计划
-- ----------------------------
CREATE TABLE `t_treatment_plan` (
  `id`              BIGINT(20)    NOT NULL AUTO_INCREMENT COMMENT '主键',
  `clinic_id`       BIGINT(20)    NOT NULL COMMENT '诊所ID',
  `patient_id`      BIGINT(20)    NOT NULL COMMENT '患者ID',
  `doctor_id`       BIGINT(20)    DEFAULT NULL COMMENT '医生ID',
  `plan_name`       VARCHAR(100)  DEFAULT NULL COMMENT '计划名称',
  `plan_content`    TEXT          COMMENT '计划内容',
  `estimate_amount` DECIMAL(12,2) DEFAULT 0.00 COMMENT '预估金额',
  `plan_status`     TINYINT(4)    DEFAULT 0 COMMENT '0草稿 1已确认 2执行中 3已完成 4已取消',
  `create_by`       BIGINT(20)    DEFAULT NULL COMMENT '创建人',
  `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`       BIGINT(20)    DEFAULT NULL COMMENT '更新人',
  `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`          VARCHAR(500)  DEFAULT NULL COMMENT '备注',
  `is_delete`       TINYINT(4)    NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_patient` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='治疗计划';

-- ----------------------------
-- 27. 咨询沟通
-- ----------------------------
CREATE TABLE `t_consult_record` (
  `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `clinic_id`       BIGINT(20)   NOT NULL COMMENT '诊所ID',
  `patient_id`      BIGINT(20)   NOT NULL COMMENT '患者ID',
  `consultant_id`   BIGINT(20)   DEFAULT NULL COMMENT '咨询师ID',
  `consult_time`    DATETIME     DEFAULT NULL COMMENT '咨询时间',
  `content`         TEXT         COMMENT '沟通内容',
  `intention`       VARCHAR(50)  DEFAULT NULL COMMENT '意向度',
  `create_by`       BIGINT(20)   DEFAULT NULL COMMENT '创建人',
  `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`       BIGINT(20)   DEFAULT NULL COMMENT '更新人',
  `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark`          VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `is_delete`       TINYINT(4)   NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_patient` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='咨询沟通';
