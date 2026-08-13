package com.yujian.admin.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 权限新增/编辑请求参数
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Data
public class PermissionSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，编辑时必填
     */
    private Long id;

    /**
     * 父权限ID，0表示顶级
     */
    private Long parentId;

    /**
     * 权限名称
     */
    private String permName;

    /**
     * 权限编码（按钮级鉴权用）
     */
    private String permCode;

    /**
     * 权限类型：1目录 2菜单 3按钮，参见 PermissionTypeEnum
     */
    private Integer permType;

    /**
     * 平台：1网页版 2移动版 3数据权限，参见 PlatformEnum
     */
    private Integer platform;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 前端组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序号，越小越靠前
     */
    private Integer sortNo;

    /**
     * 状态：1启用 0停用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
