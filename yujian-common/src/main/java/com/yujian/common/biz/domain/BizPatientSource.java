package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yujian.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 患者来源实体（树形），对应表 t_patient_source。
 * 用于业务接口请求/响应数据传输（来源维护、树形下拉、患者建档选来源等场景）。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_patient_source")
public class BizPatientSource extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 诊所ID，关联 t_clinic.id */
    private Long clinicId;

    /** 父级来源ID，关联 t_patient_source.id，顶级为0 */
    private Long parentId;

    /** 来源名称 */
    private String sourceName;

    /** 排序号，数值越小越靠前 */
    private Integer sortOrder;

    /** 状态：0停用 1启用 */
    private Integer status;

    /** 子级来源列表（非表字段，树形结构回显） */
    @TableField(exist = false)
    private List<BizPatientSource> children = new ArrayList<BizPatientSource>();
}
