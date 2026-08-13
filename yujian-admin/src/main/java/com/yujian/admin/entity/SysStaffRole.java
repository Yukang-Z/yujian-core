package com.yujian.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yujian.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 员工角色关联实体，对应表 sys_staff_role
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_staff_role")
public class SysStaffRole extends BaseEntity {

    /**
     * 员工ID，关联 sys_staff.id
     */
    private Long staffId;

    /**
     * 角色ID，关联 sys_role.id
     */
    private Long roleId;
}
