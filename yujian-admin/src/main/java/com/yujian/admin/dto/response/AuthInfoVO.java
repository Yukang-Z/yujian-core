package com.yujian.admin.dto.response;

import com.yujian.common.system.domain.SysClinic;
import com.yujian.common.system.domain.SysEmployee;
import com.yujian.common.system.domain.SysMenu;
import com.yujian.common.system.domain.SysRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 当前登录用户信息返回
 *
 * @author Zhangyk
 * @date 2026-08-18 17:20
 */
@Data
@ApiModel("当前用户信息")
public class AuthInfoVO {

    /** 员工信息（不含密码，含 clinicIds） */
    @ApiModelProperty("员工")
    private SysEmployee user;

    /** 角色列表 */
    @ApiModelProperty("角色列表")
    private List<SysRole> roles;

    /** 网页端菜单树 */
    @ApiModelProperty("菜单树")
    private List<SysMenu> menus;

    /** 权限标识列表，如 system:employee:list */
    @ApiModelProperty("权限标识")
    private List<String> permissions;

    /** 可进入诊所 */
    @ApiModelProperty("可进入诊所")
    private List<SysClinic> clinics;

    /** Session 中当前诊所ID */
    @ApiModelProperty("当前诊所ID")
    private Long currentClinicId;

    /** Session 中当前诊所名称 */
    @ApiModelProperty("当前诊所名称")
    private String currentClinicName;
}
