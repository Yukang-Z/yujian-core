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

/**
 * 治疗/处置记录实体，对应表 t_treatment_record。
 * 用于业务接口请求/响应数据传输（处置录入、历史查询、统计等场景）。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_treatment_record")
public class BizTreatmentRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 诊所ID，关联 t_clinic.id */
    private Long clinicId;

    /** 患者ID，关联 t_patient.id */
    private Long patientId;

    /** 关联就诊ID，关联 t_visit.id */
    private Long visitId;

    /** 医生ID，关联 t_employee.id */
    private Long doctorId;

    /** 护士ID，关联 t_employee.id */
    private Long nurseId;

    /** 诊疗项目ID，关联 t_treatment_item.id */
    private Long itemId;

    /** 诊疗项目名称（冗余展示） */
    private String itemName;

    /** 牙位信息，逗号分隔 */
    private String toothPositions;

    /** 就诊类型：1初诊 2复诊 */
    private Integer visitType;

    /** 治疗/处置金额（元） */
    private BigDecimal amount;

    /** 治疗/处置时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date treatTime;

    /** 医生姓名（非表字段，列表/详情回显） */
    @TableField(exist = false)
    private String doctorName;
}
