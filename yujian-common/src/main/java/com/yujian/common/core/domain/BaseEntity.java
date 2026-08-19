package com.yujian.common.core.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 实体公共基类，统一审计与逻辑删除字段；
 * 继承本类的系统域实体可作为增删改查接口请求/回参的公共字段来源。
 * <p>
 * 统一字段：create_by / create_time / update_by / update_time / remark / is_delete
 * </p>
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 创建人ID，关联 t_employee.id（新增时自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /** 创建时间（新增时自动填充） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /** 更新人ID，关联 t_employee.id（新增/修改时自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /** 更新时间（新增/修改时自动填充） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /** 备注说明 */
    private String remark;

    /** 逻辑删除标识：0 未删除，1 已删除 */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;
}
