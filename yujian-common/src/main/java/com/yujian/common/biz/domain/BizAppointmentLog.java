package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 预约操作日志实体，对应表 t_appointment_log
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

    /** 预约ID */
    private Long appointmentId;

    /** 诊所ID */
    private Long clinicId;

    /** 操作动作 */
    private String action;

    /** 变更前状态 */
    private Integer beforeStatus;

    /** 变更后状态 */
    private Integer afterStatus;

    /** 操作内容 */
    private String content;

    /** 操作人ID */
    private Long operatorId;

    /** 操作人姓名 */
    private String operatorName;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
