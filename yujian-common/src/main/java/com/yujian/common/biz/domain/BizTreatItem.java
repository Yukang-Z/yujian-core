package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yujian.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 诊疗/预约项目实体，对应表 t_treatment_item
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_treatment_item")
public class BizTreatItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 诊所ID */
    private Long clinicId;

    /** 项目名称 */
    private String itemName;

    /** 项目编码 */
    private String itemCode;

    /** 默认时长（分钟） */
    private Integer duration;

    /** 日历展示色 */
    private String itemColor;

    /** 排序号 */
    private Integer sortOrder;

    /** 状态：0停用 1启用 */
    private Integer status;
}
