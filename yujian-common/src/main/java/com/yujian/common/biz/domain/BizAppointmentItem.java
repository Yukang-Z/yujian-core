package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 预约项目明细实体，对应表 t_appointment_item。
 * 用于一次预约勾选多个诊疗项目；主表 t_appointment 仍冗余首项便于列表展示。
 *
 * @author Zhangyk
 * @date 2026-08-20 14:30
 */
@Data
@TableName("t_appointment_item")
public class BizAppointmentItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 预约ID，关联 t_appointment.id */
    private Long appointmentId;

    /** 项目ID，关联 t_treatment_item.id */
    private Long itemId;

    /** 项目名称（冗余展示） */
    private String itemName;

    /** 时长（分钟），默认 30 */
    private Integer duration;

    /** 排序号，数值越小越靠前 */
    private Integer sortOrder;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
