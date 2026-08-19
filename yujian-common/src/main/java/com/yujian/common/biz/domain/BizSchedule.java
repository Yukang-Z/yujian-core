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
 * 员工日程实体（可不绑患者），对应表 t_schedule。
 * 用于业务接口请求/响应数据传输（日历展示、日程新增/修改/取消等场景）。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_schedule")
public class BizSchedule extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 诊所ID，关联 t_clinic.id */
    private Long clinicId;

    /** 医生/员工ID，关联 t_employee.id */
    private Long doctorId;

    /** 日程标题 */
    private String title;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /** 日历展示颜色（十六进制色值） */
    private String color;

    /** 日程状态：0正常 1取消 */
    private Integer status;

    /** 医生/员工姓名（非表字段，列表/详情回显） */
    @TableField(exist = false)
    private String doctorName;
}
