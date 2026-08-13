package com.yujian.admin.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色新增/编辑请求参数
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Data
public class RoleSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，编辑时必填
     */
    private Long id;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

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
