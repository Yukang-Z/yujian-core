package com.yujian.common.core.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 登录用户上下文对象，写入 Sa-Token Session 并同步 ThreadLocal 供业务层读取；
 * 承载当前登录员工身份、工作诊所及权限信息，非直接对外接口回参。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 员工ID，关联 t_employee.id */
    private Long userId;

    /** 登录账号，对应 t_employee.username */
    private String username;

    /** 员工姓名，对应 t_employee.name */
    private String name;

    /** 当前工作诊所ID，关联 t_clinic.id（登录后选择诊所写入 Session） */
    private Long clinicId;

    /** 当前工作诊所名称，关联 t_clinic.clinic_name */
    private String clinicName;

    /** 员工可进入的诊所ID列表，来源于 t_employee_clinic.clinic_id 集合 */
    private List<Long> clinicIds;

    /** 当前请求 Token 值（Sa-Token 会话标识） */
    private String token;

    /** 权限标识集合，来源于 t_menu.perms（按钮/接口鉴权） */
    private Set<String> permissions;

    /** 角色编码列表，来源于 t_role.role_key */
    private List<String> roles;
}
