package com.yujian.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * 员工新增/编辑请求参数
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Data
public class StaffSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，编辑时必填
     */
    private Long id;

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
     * 角色ID列表，关联 sys_role.id
     */
    private List<Long> roleIds;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 备注
     */
    private String remark;
}
