package com.yujian.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yujian.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 员工实体，对应表 sys_staff
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_staff")
public class SysStaff extends BaseEntity {

    /**
     * 员工姓名
     */
    private String staffName;

    /**
     * 工号
     */
    private String jobNo;

    /**
     * 性别：0未知 1男 2女，参见 GenderEnum
     */
    private Integer gender;

    /**
     * 生日
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 工作诊所ID，关联 sys_clinic.id
     */
    private Long clinicId;

    /**
     * 所属部门ID，关联 sys_department.id
     */
    private Long deptId;

    /**
     * 岗位名称
     */
    private String positionName;

    /**
     * 在职状态：1在职 0离职，参见 WorkStatusEnum
     */
    private Integer workStatus;

    /**
     * 账号上线状态：1上线 0下线
     */
    private Integer onlineStatus;

    /**
     * 手机关联：1允许 0不允许
     */
    private Integer mobileLink;

    /**
     * 证件类型，如：身份证
     */
    private String idType;

    /**
     * 证件号码
     */
    private String idNo;

    /**
     * 登录账号
     */
    private String loginName;

    /**
     * 登录密码（BCrypt加密存储）
     */
    private String password;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 备注
     */
    private String remark;
}
