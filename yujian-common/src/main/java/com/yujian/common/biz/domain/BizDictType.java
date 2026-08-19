package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yujian.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 数据字典类型实体，对应表 t_dict_type。
 * 用于业务接口请求/响应数据传输（字典类型维护、下拉配置等场景）。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_dict_type")
public class BizDictType extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 字典类型名称（展示用） */
    private String dictName;

    /** 字典类型编码（唯一，关联 t_dict_data.dict_type） */
    private String dictType;

    /** 状态：0正常 1停用 */
    private Integer status;
}
