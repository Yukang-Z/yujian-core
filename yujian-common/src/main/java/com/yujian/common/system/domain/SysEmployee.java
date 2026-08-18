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
 * 员工实体，对应表 t_employee
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_employee")
public class SysEmployee extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 员工ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 姓名 */
    private String name;

    /** 工号（唯一） */
    private String empNo;

    /** 登录账号（唯一） */
    private String username;

    /** 登录密码（BCrypt 密文，仅写入时序列化） */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /** 性别：0女 1男 2未知 */
    private Integer gender;

    /** 生日 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;

    /** 手机号码 */
    private String mobile;

    /** 邮箱 */
    private String email;

    /** 默认/主诊所ID（冗余字段；关联诊所以 clinicIds / t_employee_clinic 为准） */
    private Long clinicId;

    /** 岗位名称 */
    private String position;

    /** 在职状态：1在职 0离职 */
    private Integer employStatus;

    /** 证件类型，如身份证 */
    private String idType;

    /** 证件号码 */
    private String idNumber;

    /** 头像地址 */
    private String avatar;

    /** 入职日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date entryDate;

    /** 离职日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date leaveDate;

    /** 排序号，越小越靠前 */
    private Integer sortOrder;

    /** 账号状态：0正常 1停用 */
    private Integer status;

    /** 诊所名称（非表字段，列表联查） */
    @TableField(exist = false)
    private String clinicName;

    /** 角色ID列表（非表字段；列表/详情返回；更新时 null=不改，非 null=全量同步） */
    @TableField(exist = false)
    private List<Long> roleIds;

    /** 关联诊所ID列表（非表字段；一对多；更新时 null=不改，非 null=全量同步） */
    @TableField(exist = false)
    private List<Long> clinicIds;

    /** 角色名称，逗号分隔（非表字段） */
    @TableField(exist = false)
    private String roleNames;

    /** 关联诊所名称，逗号分隔（非表字段） */
    @TableField(exist = false)
    private String clinicNames;
}
