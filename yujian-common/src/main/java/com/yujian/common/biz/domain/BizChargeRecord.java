package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yujian.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 收费记录实体，对应表 t_charge_record。
 * 用于业务接口请求/响应数据传输（收银结算、欠费查询、收费明细等场景）。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_charge_record")
public class BizChargeRecord extends BaseEntity {
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

    /** 收费单号（业务流水号） */
    private String chargeNo;

    /** 应收总金额（元） */
    private BigDecimal totalAmount;

    /** 已收金额（元） */
    private BigDecimal paidAmount;

    /** 欠费金额（元） */
    private BigDecimal oweAmount;

    /** 支付方式：cash现金 / wechat微信 / alipay支付宝 / card刷卡 */
    private String payMethod;

    /** 收费状态：0待收 1部分收 2已结清 */
    private Integer chargeStatus;

    /** 收费时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date chargeTime;

    /** 收银员ID，关联 t_employee.id */
    private Long cashierId;
}
