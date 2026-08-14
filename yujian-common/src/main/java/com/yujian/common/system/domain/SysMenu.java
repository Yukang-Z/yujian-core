package com.yujian.common.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yujian.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单/权限实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_menu")
public class SysMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 菜单名称 */
    private String menuName;

    /** 父菜单ID */
    private Long parentId;

    /** 排序 */
    private Integer sortOrder;

    /** 路由地址 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 权限标识 */
    private String perms;

    /** 菜单类型（M目录 C菜单 F按钮） */
    private String menuType;

    /** 平台（web/mobile） */
    private String platform;

    /** 菜单图标 */
    private String icon;

    /** 是否显示（0显示 1隐藏） */
    private Integer visible;

    /** 状态（0正常 1停用） */
    private Integer status;

    /** 子菜单 */
    @TableField(exist = false)
    private List<SysMenu> children = new ArrayList<SysMenu>();
}
