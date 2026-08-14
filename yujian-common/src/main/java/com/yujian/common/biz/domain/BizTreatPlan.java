package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yujian.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 治疗计划实体，对应表 t_treatment_plan
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_treatment_plan")
public class BizTreatPlan extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 诊所ID */
    private Long clinicId;

    /** 患者ID */
    private Long patientId;

    /** 制定医生ID */
    private Long doctorId;

    /** 计划名称 */
    private String planName;

    /** 计划内容 */
    private String planContent;

    /** 预估金额 */
    private BigDecimal estimateAmount;

    /** 计划状态 */
    private Integer planStatus;
}
