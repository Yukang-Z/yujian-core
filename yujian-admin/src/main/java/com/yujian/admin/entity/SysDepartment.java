package com.yujian.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yujian.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门实体，对应表 sys_department
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_department")
public class SysDepartment extends BaseEntity {

    /**
     * 所属诊所ID，关联 sys_clinic.id
     */
    private Long clinicId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 父部门ID，0表示顶级
     */
    private Long parentId;

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
