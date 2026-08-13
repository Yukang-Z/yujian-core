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
 * 角色实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色名称 */
    private String roleName;

    /** 角色编码 */
    private String roleKey;

    /** 排序 */
    private Integer sortOrder;

    /** 数据范围（1全部 2本诊所 3本部门 4仅本人 5自定义） */
    private Integer dataScope;

    /** 状态（0正常 1停用） */
    private Integer status;

    /** 菜单ID列表 */
    @TableField(exist = false)
    private List<Long> menuIds;
}
