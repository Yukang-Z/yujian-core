package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yujian.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 诊疗/预约项目实体，对应表 t_treatment_item。
 * 用于业务接口请求/响应数据传输（项目维护、预约/日历选用等场景）。
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

    /** 诊所ID，关联 t_clinic.id */
    private Long clinicId;

    /** 项目名称 */
    private String itemName;

    /** 项目编码（诊所内唯一标识） */
    private String itemCode;

    /** 默认时长（分钟） */
    private Integer duration;

    /** 日历展示色（十六进制色值） */
    private String itemColor;

    /** 排序号，数值越小越靠前 */
    private Integer sortOrder;

    /** 状态：0停用 1启用 */
    private Integer status;
}
