package com.yujian.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限树形展示对象
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Data
public class PermissionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
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

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新人ID
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 子权限列表，用于树形结构展示
     */
    private List<PermissionVO> children;

    /**
     * 是否已选中（角色分配权限时使用，可选）
     */
    private Boolean checked;
}
