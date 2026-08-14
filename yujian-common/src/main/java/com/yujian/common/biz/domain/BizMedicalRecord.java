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
 * 病历实体，对应表 t_medical_record
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

    /** 诊所ID */
    private Long clinicId;

    /** 患者ID */
    private Long patientId;

    /** 关联就诊ID */
    private Long visitId;

    /** 医生ID */
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

    /** 医生姓名（非表字段） */
    @TableField(exist = false)
    private String doctorName;
}
