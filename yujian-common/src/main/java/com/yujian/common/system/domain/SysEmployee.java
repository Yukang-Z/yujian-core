package com.yujian.common.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yujian.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 员工实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_employee")
public class SysEmployee extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 姓名 */
    private String name;

    /** 工号 */
    private String empNo;

    /** 登录账号 */
    private String username;

    /** 密码 */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /** 性别（0女 1男 2未知） */
    private Integer gender;

    /** 生日 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;

    /** 手机号码 */
    private String mobile;

    /** 邮箱 */
    private String email;

    /** 工作诊所ID */
    private Long clinicId;

    /** 所属部门ID */
    private Long deptId;

    /** 岗位 */
    private String position;

    /** 在职状态（1在职 0离职） */
    private Integer employStatus;

    /** 手机关联（1允许 0不允许） */
    private Integer mobileLink;

    /** 证件类型 */
    private String idType;

    /** 证件号码 */
    private String idNumber;

    /** 头像 */
    private String avatar;

    /** 入职日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date entryDate;

    /** 离职日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date leaveDate;

    /** 排序 */
    private Integer sortOrder;

    /** 状态（0正常 1停用） */
    private Integer status;

    /** 诊所名称（非表字段） */
    @TableField(exist = false)
    private String clinicName;

    /** 部门名称（非表字段） */
    @TableField(exist = false)
    private String deptName;

    /** 角色ID列表 */
    @TableField(exist = false)
    private List<Long> roleIds;

    /** 角色名称（逗号分隔） */
    @TableField(exist = false)
    private String roleNames;
}
