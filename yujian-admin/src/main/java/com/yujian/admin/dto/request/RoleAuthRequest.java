package com.yujian.admin.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 角色分配菜单权限入参
 *
 * @author Zhangyk
 * @date 2026-08-18 17:20
 */
@Data
@ApiModel("角色授权请求")
public class RoleAuthRequest {

    /** 角色ID */
    @ApiModelProperty(value = "角色ID", required = true, example = "1")
    private Long roleId;

    /** 菜单ID列表，空列表表示清空权限 */
    @ApiModelProperty(value = "菜单ID列表", example = "[1,2,3]")
    private List<Long> menuIds;
}
