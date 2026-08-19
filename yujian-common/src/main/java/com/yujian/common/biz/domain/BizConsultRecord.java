package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yujian.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 咨询记录实体，对应表 t_consult_record。
 * 用于业务接口请求/响应数据传输（咨询登记、沟通历史查询等场景）。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_consult_record")
public class BizConsultRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 诊所ID，关联 t_clinic.id */
    private Long clinicId;

    /** 患者ID，关联 t_patient.id */
    private Long patientId;

    /** 咨询师ID，关联 t_employee.id */
    private Long consultantId;

    /** 咨询时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date consultTime;

    /** 咨询/沟通内容 */
    private String content;

    /** 意向说明（意向度或跟进意向描述） */
    private String intention;
}
