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
 * 电子病历实体，对应表 t_medical_record。
 * 用于业务接口请求/响应数据传输（病历书写、查询、打印等场景）。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_medical_record")
public class BizMedicalRecord extends BaseEntity {
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

    /** 就诊类型：1初诊 2复诊 */
    private Integer visitType;

    /** 就诊时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date visitTime;

    /** 主诉 */
    private String chiefComplaint;

    /** 处置/治疗说明 */
    private String treatment;

    /** 医嘱 */
    private String advice;

    /** 医生姓名（非表字段，列表/详情回显） */
    @TableField(exist = false)
    private String doctorName;
}
