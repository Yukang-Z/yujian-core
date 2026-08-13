package com.yujian.admin.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 员工重置密码请求参数
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Data
public class StaffPasswordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 员工ID，关联 sys_staff.id
     */
    @NotNull(message = "员工ID不能为空")
    private Long staffId;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
