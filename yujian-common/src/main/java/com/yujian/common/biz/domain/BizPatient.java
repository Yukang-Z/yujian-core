package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yujian.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 患者
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_patient")
public class BizPatient extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 诊所ID */
    private Long clinicId;

    /** 病历号 */
    private String medicalRecordNo;

    /** 姓名 */
    private String name;

    /** 姓名拼音 */
    private String namePinyin;

    /** 性别 0女 1男 2未知 */
    private Integer gender;

    /** 星级 1-5 */
    private Integer starLevel;

    /** 生日 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;

    /** 年龄（冗余） */
    private Integer age;

    /** 手机号 */
    private String mobile;

    /** 手机号关系（本人/父亲/母亲等） */
    private String mobileRelation;

    /** 备用电话 */
    private String phone;

    /** 备用电话关系 */
    private String phoneRelation;

    /** 身份证号 */
    private String idNumber;

    /** 省市区 */
    private String province;
    private String city;
    private String district;
    private String address;

    /** 头像 */
    private String avatar;

    /** 患者类型 1普通 2临时 */
    private Integer patientType;

    /** 患者来源ID */
    private Long sourceId;

    /** 介绍人类型 */
    private String introducerType;

    /** 介绍人ID（患者/员工） */
    private Long introducerId;

    /** 介绍人姓名 */
    private String introducerName;

    /** 主治医生 */
    private Long doctorId;

    /** 初诊医生 */
    private Long firstDoctorId;

    /** 初诊日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date firstVisitTime;

    /** 复诊日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date nextVisitTime;

    /** 欠费 */
    private BigDecimal oweAmount;

    /** 已收 */
    private BigDecimal paidAmount;

    /** 状态 0正常 1归档 */
    private Integer status;

    @TableField(exist = false)
    private String doctorName;

    @TableField(exist = false)
    private String firstDoctorName;

    @TableField(exist = false)
    private String sourceName;

    @TableField(exist = false)
    private List<Long> tagIds;

    @TableField(exist = false)
    private String tagNames;

    @TableField(exist = false)
    private List<Long> itemIds;
}
