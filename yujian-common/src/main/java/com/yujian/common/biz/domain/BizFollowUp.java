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
 * 随访/回访实体，对应表 t_follow_up
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_follow_up")
public class BizFollowUp extends BaseEntity {
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

    /** 计划回访时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date planTime;

    /** 实际回访时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date actualTime;

    /** 回访类型 */
    private String followType;

    /** 回访状态：0待回访 1已完成 2已取消 */
    private Integer followStatus;

    /** 回访内容 */
    private String content;

    /** 回访结果 */
    private String result;

    /** 负责人ID */
    private Long ownerId;

    /** 患者姓名（非表字段） */
    @TableField(exist = false)
    private String patientName;

    /** 负责人姓名（非表字段） */
    @TableField(exist = false)
    private String ownerName;
}
