package com.yujian.admin.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 角色分配权限请求参数
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Data
public class RolePermissionAssignDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID，关联 sys_role.id
     */
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    /**
     * 权限ID列表，关联 sys_permission.id
     */
    private List<Long> permissionIds;
}
