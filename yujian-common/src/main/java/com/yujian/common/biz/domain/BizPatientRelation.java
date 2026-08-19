package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 患者亲友关系实体，对应表 t_patient_relation。
 * 用于业务接口请求/响应数据传输（亲友绑定、关系列表查询等场景）。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@TableName("t_patient_relation")
public class BizPatientRelation implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 诊所ID，关联 t_clinic.id */
    private Long clinicId;

    /** 当前患者ID，关联 t_patient.id */
    private Long patientId;

    /** 关联患者ID，关联 t_patient.id */
    private Long relatedId;

    /** 关系类型（如父子、夫妻、兄弟、朋友等，字典或自由文本） */
    private String relationType;

    /** 创建人ID，关联 t_employee.id */
    private Long createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /** 备注 */
    private String remark;

    /** 逻辑删除标记：0正常 1已删除 */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;

    /** 关联患者姓名（非表字段，列表/详情回显） */
    @TableField(exist = false)
    private String relatedName;

    /** 关联患者手机号（非表字段，列表/详情回显） */
    @TableField(exist = false)
    private String relatedMobile;

    /** 关联患者病历号（非表字段，列表/详情回显） */
    @TableField(exist = false)
    private String relatedMedicalRecordNo;
}
