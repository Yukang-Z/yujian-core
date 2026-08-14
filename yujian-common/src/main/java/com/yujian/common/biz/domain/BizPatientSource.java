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
 * 患者来源实体（树形），对应表 t_patient_source
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

    /** 诊所ID */
    private Long clinicId;

    /** 父级来源ID，顶级为0或空 */
    private Long parentId;

    /** 来源名称 */
    private String sourceName;

    /** 排序号 */
    private Integer sortOrder;

    /** 状态：0停用 1启用 */
    private Integer status;

    /** 子级来源列表（非表字段） */
    @TableField(exist = false)
    private List<BizPatientSource> children = new ArrayList<BizPatientSource>();
}
