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
 * 预约实体，对应表 t_appointment。
 * 用于业务接口请求/响应数据传输（预约创建/改期、日历展示、到诊分诊等场景）。
 * <p>
 * 预约状态取值：1已预约 2已确认 3已到达 4治疗中 5已离开 6已过期 7已流失 8预约未到
 * </p>
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_appointment")
public class BizAppointment extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 状态常量：1已预约 */
    public static final int STATUS_BOOKED = 1;

    /** 状态常量：2已确认 */
    public static final int STATUS_CONFIRMED = 2;

    /** 状态常量：3已到达 */
    public static final int STATUS_ARRIVED = 3;

    /** 状态常量：4治疗中 */
    public static final int STATUS_TREATING = 4;

    /** 状态常量：5已离开 */
    public static final int STATUS_LEFT = 5;

    /** 状态常量：6已过期 */
    public static final int STATUS_EXPIRED = 6;

    /** 状态常量：7已流失/取消 */
    public static final int STATUS_LOST = 7;

    /** 状态常量：8预约未到 */
    public static final int STATUS_MISSED = 8;

    /** 预约ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 诊所ID，关联 t_clinic.id */
    private Long clinicId;

    /** 患者ID，关联 t_patient.id */
    private Long patientId;

    /** 预约医生ID，关联 t_employee.id */
    private Long doctorId;

    /** 护士ID，关联 t_employee.id */
    private Long nurseId;

    /** 咨询师ID，关联 t_employee.id */
    private Long consultantId;

    /** 预约开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /** 预约结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /** 就诊类型：1初诊 2复诊 */
    private Integer visitType;

    /** 预约状态：1已预约 2已确认 3已到达 4治疗中 5已离开 6已过期 7已流失 8预约未到 */
    private Integer status;

    /** 预约项目ID，关联 t_treatment_item.id */
    private Long itemId;

    /** 预约项目名称（冗余展示） */
    private String itemName;

    /** 是否已分诊：0否 1是 */
    private Integer triaged;

    /** 是否已挂号：0否 1是 */
    private Integer registered;

    /** 预约类型：normal普通 / walkin散客 / online网络 */
    private String appointType;

    /** 预约来源：clinic院内 / online网络 / wechat微信 */
    private String appointSource;

    /** 取消/删除原因 */
    private String cancelReason;

    /** 项目颜色（日历块展示，十六进制色值） */
    private String itemColor;

    /** 预约人姓名（冗余展示） */
    private String creatorName;

    /** 患者姓名（非表字段，列表/日历回显） */
    @TableField(exist = false)
    private String patientName;

    /** 病历号（非表字段，列表/日历回显） */
    @TableField(exist = false)
    private String medicalRecordNo;

    /** 患者手机号（非表字段，列表/日历回显） */
    @TableField(exist = false)
    private String mobile;

    /** 患者性别（非表字段，列表/日历回显）：0女 1男 2未知 */
    @TableField(exist = false)
    private Integer gender;

    /** 患者年龄（非表字段，列表/日历回显） */
    @TableField(exist = false)
    private Integer age;

    /** 医生姓名（非表字段，列表/日历回显） */
    @TableField(exist = false)
    private String doctorName;

    /** 护士姓名（非表字段，列表/日历回显） */
    @TableField(exist = false)
    private String nurseName;

    /** 咨询师姓名（非表字段，列表/日历回显） */
    @TableField(exist = false)
    private String consultantName;
}
