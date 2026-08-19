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
 * 员工实体，映射表 t_employee；
 * 作为员工管理相关接口（列表/详情/新增/修改/重置密码）的请求与回参字段说明。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_employee")
public class SysEmployee extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 员工ID（主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 员工姓名 */
    private String name;

    /** 工号（唯一，业务标识） */
    private String empNo;

    /** 登录账号（唯一） */
    private String username;

    /** 登录密码（BCrypt 密文；仅写入时序列化，查询回参不返回） */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /** 性别：0 女，1 男，2 未知 */
    private Integer gender;

    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;

    /** 手机号码 */
    private String mobile;

    /** 电子邮箱 */
    private String email;

    /** 默认/主诊所ID（冗余字段；实际关联诊所以 clinicIds / t_employee_clinic 为准），关联 t_clinic.id */
    private Long clinicId;

    /** 岗位名称 */
    private String position;

    /** 在职状态：1 在职，0 离职 */
    private Integer employStatus;

    /** 证件类型（如身份证、护照等） */
    private String idType;

    /** 证件号码 */
    private String idNumber;

    /** 头像图片地址（URL） */
    private String avatar;

    /** 入职日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date entryDate;

    /** 离职日期（在职时为空） */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date leaveDate;

    /** 排序号，数值越小越靠前 */
    private Integer sortOrder;

    /** 账号状态：0 正常，1 停用 */
    private Integer status;

    /** 默认诊所名称（非表字段；列表联查 t_clinic 回显） */
    @TableField(exist = false)
    private String clinicName;

    /** 关联角色ID列表（非表字段；列表/详情回参；更新时 null 表示不修改，非 null 全量同步 t_employee_role） */
    @TableField(exist = false)
    private List<Long> roleIds;

    /** 关联诊所ID列表（非表字段；一对多；更新时 null 表示不修改，非 null 全量同步 t_employee_clinic） */
    @TableField(exist = false)
    private List<Long> clinicIds;

    /** 关联角色名称，逗号分隔（非表字段；列表联查 t_role 回显） */
    @TableField(exist = false)
    private String roleNames;

    /** 关联诊所名称，逗号分隔（非表字段；列表联查 t_clinic 回显） */
    @TableField(exist = false)
    private String clinicNames;
}
