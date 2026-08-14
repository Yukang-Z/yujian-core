package com.yujian.common.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yujian.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 角色实体，对应表 t_role
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_role")
public class SysRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 角色ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色名称 */
    private String roleName;

    /** 角色编码（唯一，如 admin） */
    private String roleKey;

    /** 排序号，越小越靠前 */
    private Integer sortOrder;

    /**
     * 数据范围：1全部 2本诊所 3本部门 4仅本人 5自定义
     */
    private Integer dataScope;

    /** 状态：0正常 1停用 */
    private Integer status;

    /** 菜单ID列表（非表字段，分配权限时使用） */
    @TableField(exist = false)
    private List<Long> menuIds;
}
