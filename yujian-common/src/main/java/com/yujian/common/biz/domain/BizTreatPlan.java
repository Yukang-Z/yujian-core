package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yujian.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;


/**
 * 治疗计划实体，对应表 t_treatment_plan。
 * 用于业务接口请求/响应数据传输（计划制定、确认、执行跟踪等场景）。
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

    /** 诊所ID，关联 t_clinic.id */
    private Long clinicId;

    /** 患者ID，关联 t_patient.id */
    private Long patientId;

    /** 制定医生ID，关联 t_employee.id */
    private Long doctorId;

    /** 计划名称 */
    private String planName;

    /** 计划内容（治疗方案说明） */
    private String planContent;

    /** 预估金额（元） */
    private BigDecimal estimateAmount;

    /** 计划状态：0草稿 1已确认 2执行中 3已完成 4已取消 */
    private Integer planStatus;
}
