package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 预约操作日志实体，对应表 t_appointment_log。
 * 用于业务接口请求/响应数据传输（预约状态变更审计、操作轨迹查询等场景）。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@TableName("t_appointment_log")
public class BizAppointmentLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 预约ID，关联 t_appointment.id */
    private Long appointmentId;

    /** 诊所ID，关联 t_clinic.id */
    private Long clinicId;

    /** 操作动作（create/update/confirm/cancel/arrive/seat/left/delete/restore 等） */
    private String action;

    /** 变更前预约状态（同 t_appointment.status：1已预约 2已确认 3已到达 4治疗中 5已离开 6已过期 7已流失 8预约未到） */
    private Integer beforeStatus;

    /** 变更后预约状态（同 t_appointment.status：1已预约 2已确认 3已到达 4治疗中 5已离开 6已过期 7已流失 8预约未到） */
    private Integer afterStatus;

    /** 操作内容描述 */
    private String content;

    /** 操作人ID，关联 t_employee.id */
    private Long operatorId;

    /** 操作人姓名（冗余展示） */
    private String operatorName;

    /** 创建时间（操作发生时间） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
