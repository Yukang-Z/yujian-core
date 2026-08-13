package com.yujian.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 员工详情展示对象（不含密码）
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Data
public class StaffVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
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
     * 工作诊所名称
     */
    private String clinicName;

    /**
     * 所属部门ID，关联 sys_department.id
     */
    private Long deptId;

    /**
     * 所属部门名称
     */
    private String deptName;

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

    /**
     * 角色名称，多个以逗号分隔
     */
    private String roleNames;

    /**
     * 角色ID列表
     */
    private List<Long> roleIds;

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新人ID
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
