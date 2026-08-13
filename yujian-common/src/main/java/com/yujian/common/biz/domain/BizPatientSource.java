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
 * 患者来源（树形）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_patient_source")
public class BizPatientSource extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long clinicId;
    private Long parentId;
    private String sourceName;
    private Integer sortOrder;
    private Integer status;

    @TableField(exist = false)
    private List<BizPatientSource> children = new ArrayList<BizPatientSource>();
}
