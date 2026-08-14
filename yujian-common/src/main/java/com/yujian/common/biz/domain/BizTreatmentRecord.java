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
 * 治疗记录实体，对应表 t_treatment_record
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

    /** 诊所ID */
    private Long clinicId;

    /** 患者ID */
    private Long patientId;

    /** 关联就诊ID */
    private Long visitId;

    /** 医生ID */
    private Long doctorId;

    /** 护士ID */
    private Long nurseId;

    /** 诊疗项目ID */
    private Long itemId;

    /** 诊疗项目名称 */
    private String itemName;

    /** 牙位信息 */
    private String toothPositions;

    /** 就诊类型：1初诊 2复诊 */
    private Integer visitType;

    /** 治疗金额 */
    private BigDecimal amount;

    /** 治疗时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date treatTime;

    /** 医生姓名（非表字段） */
    @TableField(exist = false)
    private String doctorName;
}
