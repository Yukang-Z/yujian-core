package com.yujian.common.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yujian.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门实体，映射表 t_dept；
 * 作为部门管理相关接口（列表/详情/新增/修改）的请求与回参字段说明。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_dept")
public class SysDept extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 部门ID（主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属诊所ID，关联 t_clinic.id */
    private Long clinicId;

    /** 父部门ID，关联 t_dept.id（0 表示顶级部门） */
    private Long parentId;

    /** 部门名称 */
    private String deptName;

    /** 部门编码（业务标识） */
    private String deptCode;

    /** 部门负责人姓名 */
    private String leader;

    /** 部门联系电话 */
    private String phone;

    /** 排序号，数值越小越靠前 */
    private Integer sortOrder;

    /** 部门状态：0 正常，1 停用 */
    private Integer status;
}
