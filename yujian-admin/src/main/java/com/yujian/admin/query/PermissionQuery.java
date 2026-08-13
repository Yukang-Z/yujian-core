package com.yujian.admin.query;

import com.yujian.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限分页查询参数
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PermissionQuery extends PageQuery {

    /**
     * 权限名称（模糊匹配）
     */
    private String permName;

    /**
     * 平台：1网页版 2移动版 3数据权限，参见 PlatformEnum
     */
    private Integer platform;

    /**
     * 状态：1启用 0停用
     */
    private Integer status;

    /**
     * 父权限ID，0表示顶级
     */
    private Long parentId;
}
