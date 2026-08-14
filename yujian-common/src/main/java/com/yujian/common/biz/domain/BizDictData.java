package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yujian.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据字典项
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_dict_data")
public class BizDictData extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String dictType;
    private String dictLabel;
    private String dictValue;
    private String cssClass;
    private Integer sortOrder;
    private Integer status;
}
