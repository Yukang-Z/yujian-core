package com.yujian.admin.query;

import com.yujian.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色分页查询参数
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleQuery extends PageQuery {

    /**
     * 角色名称（模糊匹配）
     */
    private String roleName;

    /**
     * 角色编码（精确或模糊匹配）
     */
    private String roleCode;

    /**
     * 状态：1启用 0停用
     */
    private Integer status;
}
