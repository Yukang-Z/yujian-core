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
 * 菜单/权限实体，映射表 t_menu；
 * 作为菜单管理、路由构建及按钮鉴权相关接口的请求与回参字段说明。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_menu")
public class SysMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 菜单ID（主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 菜单名称（展示用） */
    private String menuName;

    /** 父菜单ID，关联 t_menu.id（0 表示顶级菜单） */
    private Long parentId;

    /** 排序号，数值越小越靠前 */
    private Integer sortOrder;

    /** 前端路由地址 */
    private String path;

    /** 前端组件路径（Vue 组件） */
    private String component;

    /** 权限标识（按钮/接口鉴权用，如 system:clinic:list） */
    private String perms;

    /** 菜单类型：M 目录，C 菜单，F 按钮 */
    private String menuType;

    /** 适用平台：web 网页端，mobile 移动端 */
    private String platform;

    /** 菜单图标标识 */
    private String icon;

    /** 是否在菜单栏显示：0 显示，1 隐藏 */
    private Integer visible;

    /** 菜单状态：0 正常，1 停用 */
    private Integer status;

    /** 子菜单列表（非表字段；树形结构接口回参） */
    @TableField(exist = false)
    private List<SysMenu> children = new ArrayList<SysMenu>();
}
