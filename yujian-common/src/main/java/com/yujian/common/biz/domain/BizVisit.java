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
 * 就诊实体，对应表 t_visit。
 * 用于业务接口请求/响应数据传输（分诊、就诊流转、列表查询、详情等场景）。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_visit")
public class BizVisit extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 诊所ID，关联 t_clinic.id */
    private Long clinicId;

    /** 患者ID，关联 t_patient.id */
    private Long patientId;

    /** 关联预约ID，关联 t_appointment.id */
    private Long appointmentId;

    /** 医生ID，关联 t_employee.id */
    private Long doctorId;

    /** 护士ID，关联 t_employee.id */
    private Long nurseId;

    /** 咨询师ID，关联 t_employee.id */
    private Long consultantId;

    /** 就诊类型：1初诊 2复诊 */
    private Integer visitType;

    /** 就诊状态：1待分诊 2咨询中 3治疗中 4待结算 5已完成 6已离开 */
    private Integer visitStatus;

    /** 诊疗项目名称（冗余展示） */
    private String itemName;

    /** 就诊开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /** 就诊结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /** 医生姓名（非表字段，列表/详情回显） */
    @TableField(exist = false)
    private String doctorName;

    /** 患者姓名（非表字段，列表/详情回显） */
    @TableField(exist = false)
    private String patientName;
}
