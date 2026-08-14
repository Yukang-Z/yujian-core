package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yujian.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据字典项实体，对应表 t_dict_data
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_dict_data")
public class BizDictData extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 字典类型编码，关联 t_dict_type.dict_type */
    private String dictType;

    /** 字典标签（展示名） */
    private String dictLabel;

    /** 字典键值 */
    private String dictValue;

    /** 前端样式类名 */
    private String cssClass;

    /** 排序号 */
    private Integer sortOrder;

    /** 状态：0停用 1启用 */
    private Integer status;
}
