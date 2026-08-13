package com.yujian.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yujian.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色权限关联实体，对应表 sys_role_permission
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_permission")
public class SysRolePermission extends BaseEntity {

    /**
     * 角色ID，关联 sys_role.id
     */
    private Long roleId;

    /**
     * 权限ID，关联 sys_permission.id
     */
    private Long permissionId;
}
