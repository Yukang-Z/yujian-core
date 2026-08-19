package com.yujian.common.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 员工-角色关联实体，映射表 t_employee_role；
 * 作为员工分配角色、角色绑定员工等接口的内部关联数据载体。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@TableName("t_employee_role")
public class SysEmployeeRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 员工ID，关联 t_employee.id */
    private Long employeeId;

    /** 角色ID，关联 t_role.id */
    private Long roleId;
}
