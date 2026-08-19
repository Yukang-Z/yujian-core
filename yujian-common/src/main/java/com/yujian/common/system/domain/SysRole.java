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
 * 角色实体，映射表 t_role；
 * 作为角色管理相关接口（列表/详情/新增/修改/授权）的请求与回参字段说明。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_role")
public class SysRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 角色ID（主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色名称（展示用） */
    private String roleName;

    /** 角色编码（唯一标识，如 admin，用于权限与 LoginUser.roles） */
    private String roleKey;

    /** 排序号，数值越小越靠前 */
    private Integer sortOrder;

    /** 数据权限范围：1 全部，2 本诊所，3 本部门，4 仅本人，5 自定义 */
    private Integer dataScope;

    /** 角色状态：0 正常，1 停用 */
    private Integer status;

    /** 菜单ID列表（非表字段；分配角色菜单权限时作为请求/回参） */
    @TableField(exist = false)
    private List<Long> menuIds;
}
