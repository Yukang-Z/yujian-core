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
 * 收费记录实体，对应表 t_charge_record
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

    /** 诊所ID */
    private Long clinicId;

    /** 患者ID */
    private Long patientId;

    /** 关联就诊ID */
    private Long visitId;

    /** 收费单号 */
    private String chargeNo;

    /** 应收总金额 */
    private BigDecimal totalAmount;

    /** 已收金额 */
    private BigDecimal paidAmount;

    /** 欠费金额 */
    private BigDecimal oweAmount;

    /** 支付方式 */
    private String payMethod;

    /** 收费状态 */
    private Integer chargeStatus;

    /** 收费时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date chargeTime;

    /** 收银员ID */
    private Long cashierId;
}
