package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yujian.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 诊疗/预约项目
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_treatment_item")
public class BizTreatItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long clinicId;
    private String itemName;
    private String itemCode;
    /** 默认时长（分钟） */
    private Integer duration;

    /** 日历展示色 */
    private String itemColor;

    private Integer sortOrder;
    private Integer status;
}
