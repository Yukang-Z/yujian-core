package com.yujian.common.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 员工-诊所关联，对应表 t_employee_clinic（一对多）
 *
 * @author Zhangyk
 * @date 2026-08-18 14:45
 */
@Data
@TableName("t_employee_clinic")
public class SysEmployeeClinic implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 员工ID，关联 t_employee.id */
    private Long employeeId;

    /** 诊所ID，关联 t_clinic.id */
    private Long clinicId;
}
