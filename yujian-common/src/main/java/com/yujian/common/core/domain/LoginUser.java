package com.yujian.common.core.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 登录用户上下文（写入 Sa-Token Session，并同步到 ThreadLocal 供业务读取）
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 员工ID，对应 t_employee.id */
    private Long userId;

    /** 登录账号 */
    private String username;

    /** 员工姓名 */
    private String name;

    /** 当前工作诊所ID，对应 t_clinic.id */
    private Long clinicId;

    /** 所属部门ID，对应 t_dept.id */
    private Long deptId;

    /** 当前请求 Token 值 */
    private String token;

    /** 权限标识集合（菜单 perms） */
    private Set<String> permissions;

    /** 角色编码列表（role_key） */
    private List<String> roles;
}
