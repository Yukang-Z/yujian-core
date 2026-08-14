package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yujian.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 预约
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_appointment")
public class BizAppointment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 状态常量 */
    public static final int STATUS_BOOKED = 1;      // 已预约
    public static final int STATUS_CONFIRMED = 2;   // 已确认
    public static final int STATUS_ARRIVED = 3;     // 已到达
    public static final int STATUS_TREATING = 4;    // 治疗中
    public static final int STATUS_LEFT = 5;        // 已离开
    public static final int STATUS_EXPIRED = 6;     // 已过期
    public static final int STATUS_LOST = 7;        // 已流失/取消
    public static final int STATUS_MISSED = 8;      // 预约未到

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long clinicId;
    private Long patientId;

    /** 预约医生 */
    private Long doctorId;
    /** 护士 */
    private Long nurseId;
    /** 咨询师 */
    private Long consultantId;

    /** 预约开始/结束 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /** 就诊类型 1初诊 2复诊 */
    private Integer visitType;

    /** 预约状态 */
    private Integer status;

    /** 预约项目ID */
    private Long itemId;

    /** 预约项目名称（冗余） */
    private String itemName;

    /** 是否已分诊 */
    private Integer triaged;

    /** 是否已挂号 */
    private Integer registered;

    /** 预约类型 normal/walkin/online */
    private String appointType;

    /** 预约来源 clinic/online/wechat */
    private String appointSource;

    /** 取消/删除原因 */
    private String cancelReason;

    /** 项目颜色（日历块） */
    private String itemColor;

    /** 预约人姓名 */
    private String creatorName;

    @TableField(exist = false)
    private String patientName;

    @TableField(exist = false)
    private String medicalRecordNo;

    @TableField(exist = false)
    private String mobile;

    @TableField(exist = false)
    private Integer gender;

    @TableField(exist = false)
    private Integer age;

    @TableField(exist = false)
    private String doctorName;

    @TableField(exist = false)
    private String nurseName;

    @TableField(exist = false)
    private String consultantName;
}
