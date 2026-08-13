-- ============================================================
-- 宇健口腔 - 患者 / 预约 / 基础数据 表结构
-- ============================================================
USE `yujian`;

-- ----------------------------
-- 患者表
-- ----------------------------
DROP TABLE IF EXISTS `biz_patient`;
CREATE TABLE `biz_patient` (
  `id`                 BIGINT(20)     NOT NULL AUTO_INCREMENT,
  `clinic_id`          BIGINT(20)     NOT NULL COMMENT '诊所ID',
  `medical_record_no`  VARCHAR(50)    NOT NULL COMMENT '病历号',
  `name`               VARCHAR(50)    NOT NULL COMMENT '姓名',
  `name_pinyin`        VARCHAR(100)   DEFAULT NULL COMMENT '拼音首字母',
  `gender`             TINYINT(1)     DEFAULT 2 COMMENT '0女 1男 2未知',
  `star_level`         TINYINT(1)     DEFAULT 0 COMMENT '星级1-5',
  `birthday`           DATE           DEFAULT NULL,
  `age`                INT(11)        DEFAULT NULL,
  `mobile`             VARCHAR(20)    NOT NULL COMMENT '手机号',
  `mobile_relation`    VARCHAR(20)    DEFAULT '本人',
  `phone`              VARCHAR(20)    DEFAULT NULL,
  `phone_relation`     VARCHAR(20)    DEFAULT NULL,
  `id_number`          VARCHAR(30)    DEFAULT NULL,
  `province`           VARCHAR(50)    DEFAULT NULL,
  `city`               VARCHAR(50)    DEFAULT NULL,
  `district`           VARCHAR(50)    DEFAULT NULL,
  `address`            VARCHAR(255)   DEFAULT NULL,
  `avatar`             VARCHAR(255)   DEFAULT NULL,
  `patient_type`       TINYINT(1)     DEFAULT 1 COMMENT '1普通 2临时',
  `source_id`          BIGINT(20)     DEFAULT NULL COMMENT '患者来源',
  `introducer_type`    VARCHAR(30)    DEFAULT NULL,
  `introducer_id`      BIGINT(20)     DEFAULT NULL,
  `introducer_name`    VARCHAR(50)    DEFAULT NULL,
  `doctor_id`          BIGINT(20)     DEFAULT NULL COMMENT '主治医生',
  `first_doctor_id`    BIGINT(20)     DEFAULT NULL COMMENT '初诊医生',
  `first_visit_time`   DATETIME       DEFAULT NULL,
  `next_visit_time`    DATETIME       DEFAULT NULL,
  `owe_amount`         DECIMAL(12,2)  DEFAULT 0.00,
  `paid_amount`        DECIMAL(12,2)  DEFAULT 0.00,
  `status`             TINYINT(1)     DEFAULT 0 COMMENT '0正常 1归档',
  `create_by`          BIGINT(20)     DEFAULT NULL,
  `create_time`        DATETIME       DEFAULT CURRENT_TIMESTAMP,
  `update_by`          BIGINT(20)     DEFAULT NULL,
  `update_time`        DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remark`             VARCHAR(500)   DEFAULT NULL,
  `del_flag`           TINYINT(1)     DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_clinic_mrn` (`clinic_id`, `medical_record_no`),
  KEY `idx_mobile` (`mobile`),
  KEY `idx_name` (`name`),
  KEY `idx_doctor` (`doctor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者表';

-- ----------------------------
-- 预约表
-- ----------------------------
DROP TABLE IF EXISTS `biz_appointment`;
CREATE TABLE `biz_appointment` (
  `id`              BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `clinic_id`       BIGINT(20)   NOT NULL,
  `patient_id`      BIGINT(20)   NOT NULL,
  `doctor_id`       BIGINT(20)   DEFAULT NULL COMMENT '预约医生',
  `nurse_id`        BIGINT(20)   DEFAULT NULL COMMENT '护士',
  `consultant_id`   BIGINT(20)   DEFAULT NULL COMMENT '咨询师',
  `start_time`      DATETIME     NOT NULL,
  `end_time`        DATETIME     NOT NULL,
  `visit_type`      TINYINT(1)   DEFAULT 2 COMMENT '1初诊 2复诊',
  `status`          TINYINT(1)   DEFAULT 1 COMMENT '1已预约 2已确认 3已到达 4治疗中 5已离开 6已过期 7已流失 8预约未到',
  `item_id`         BIGINT(20)   DEFAULT NULL,
  `item_name`       VARCHAR(100) DEFAULT NULL,
  `triaged`         TINYINT(1)   DEFAULT 0 COMMENT '是否分诊',
  `registered`      TINYINT(1)   DEFAULT 0 COMMENT '是否挂号',
  `create_by`       BIGINT(20)   DEFAULT NULL,
  `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_by`       BIGINT(20)   DEFAULT NULL,
  `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remark`          VARCHAR(500) DEFAULT NULL,
  `del_flag`        TINYINT(1)   DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_clinic_time` (`clinic_id`, `start_time`),
  KEY `idx_doctor_time` (`doctor_id`, `start_time`),
  KEY `idx_patient` (`patient_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- ----------------------------
-- 字典类型 / 字典数据
-- ----------------------------
DROP TABLE IF EXISTS `biz_dict_type`;
CREATE TABLE `biz_dict_type` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `dict_name`   VARCHAR(100) NOT NULL,
  `dict_type`   VARCHAR(100) NOT NULL,
  `status`      TINYINT(1)   DEFAULT 0,
  `create_by`   BIGINT(20)   DEFAULT NULL,
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_by`   BIGINT(20)   DEFAULT NULL,
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remark`      VARCHAR(500) DEFAULT NULL,
  `del_flag`    TINYINT(1)   DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型';

DROP TABLE IF EXISTS `biz_dict_data`;
CREATE TABLE `biz_dict_data` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `dict_type`   VARCHAR(100) NOT NULL,
  `dict_label`  VARCHAR(100) NOT NULL,
  `dict_value`  VARCHAR(100) NOT NULL,
  `css_class`   VARCHAR(50)  DEFAULT NULL,
  `sort_order`  INT(11)      DEFAULT 0,
  `status`      TINYINT(1)   DEFAULT 0,
  `create_by`   BIGINT(20)   DEFAULT NULL,
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_by`   BIGINT(20)   DEFAULT NULL,
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remark`      VARCHAR(500) DEFAULT NULL,
  `del_flag`    TINYINT(1)   DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据';

-- ----------------------------
-- 患者标签
-- ----------------------------
DROP TABLE IF EXISTS `biz_patient_tag`;
CREATE TABLE `biz_patient_tag` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `clinic_id`   BIGINT(20)   NOT NULL,
  `tag_name`    VARCHAR(50)  NOT NULL,
  `tag_color`   VARCHAR(20)  DEFAULT NULL,
  `sort_order`  INT(11)      DEFAULT 0,
  `status`      TINYINT(1)   DEFAULT 0,
  `create_by`   BIGINT(20)   DEFAULT NULL,
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_by`   BIGINT(20)   DEFAULT NULL,
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remark`      VARCHAR(500) DEFAULT NULL,
  `del_flag`    TINYINT(1)   DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者标签';

DROP TABLE IF EXISTS `biz_patient_tag_rel`;
CREATE TABLE `biz_patient_tag_rel` (
  `id`         BIGINT(20) NOT NULL AUTO_INCREMENT,
  `patient_id` BIGINT(20) NOT NULL,
  `tag_id`     BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_patient` (`patient_id`),
  KEY `idx_tag` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者标签关联';

-- ----------------------------
-- 患者来源（树）
-- ----------------------------
DROP TABLE IF EXISTS `biz_patient_source`;
CREATE TABLE `biz_patient_source` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `clinic_id`   BIGINT(20)   NOT NULL,
  `parent_id`   BIGINT(20)   DEFAULT 0,
  `source_name` VARCHAR(100) NOT NULL,
  `sort_order`  INT(11)      DEFAULT 0,
  `status`      TINYINT(1)   DEFAULT 0,
  `create_by`   BIGINT(20)   DEFAULT NULL,
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_by`   BIGINT(20)   DEFAULT NULL,
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remark`      VARCHAR(500) DEFAULT NULL,
  `del_flag`    TINYINT(1)   DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者来源';

-- ----------------------------
-- 诊疗/预约项目
-- ----------------------------
DROP TABLE IF EXISTS `biz_treat_item`;
CREATE TABLE `biz_treat_item` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `clinic_id`   BIGINT(20)   NOT NULL,
  `item_name`   VARCHAR(100) NOT NULL,
  `item_code`   VARCHAR(50)  DEFAULT NULL,
  `duration`    INT(11)      DEFAULT 30 COMMENT '默认时长分钟',
  `sort_order`  INT(11)      DEFAULT 0,
  `status`      TINYINT(1)   DEFAULT 0,
  `create_by`   BIGINT(20)   DEFAULT NULL,
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP,
  `update_by`   BIGINT(20)   DEFAULT NULL,
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remark`      VARCHAR(500) DEFAULT NULL,
  `del_flag`    TINYINT(1)   DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='诊疗预约项目';
