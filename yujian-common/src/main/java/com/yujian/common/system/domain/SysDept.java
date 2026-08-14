package com.yujian.common.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yujian.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门实体，对应表 t_dept
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_dept")
public class SysDept extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 部门ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属诊所ID，关联 t_clinic.id */
    private Long clinicId;

    /** 父部门ID（0 为顶级） */
    private Long parentId;

    /** 部门名称 */
    private String deptName;

    /** 部门编码 */
    private String deptCode;

    /** 负责人 */
    private String leader;

    /** 联系电话 */
    private String phone;

    /** 排序号，越小越靠前 */
    private Integer sortOrder;

    /** 状态：0正常 1停用 */
    private Integer status;
}
