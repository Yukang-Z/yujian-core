package com.yujian.admin.dto.response;

import com.yujian.common.system.domain.SysClinic;
import com.yujian.common.system.domain.SysEmployee;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 登录成功返回
 *
 * @author Zhangyk
 * @date 2026-08-18 17:20
 */
@Data
@ApiModel("登录结果")
public class LoginVO {

    /** Sa-Token，后续请求放 Header：Authorization: Bearer {token} */
    @ApiModelProperty("登录 Token")
    private String token;

    /** 员工基础信息（不含密码） */
    @ApiModelProperty("当前员工")
    private SysEmployee user;

    /** 该员工可进入的诊所列表 */
    @ApiModelProperty("可进入诊所列表")
    private List<SysClinic> clinics;

    /** true 表示关联多个诊所，必须再调 /auth/selectClinic */
    @ApiModelProperty("是否需要选择诊所")
    private Boolean needSelectClinic;

    /** 已自动选中的诊所ID；多诊所未选时为 null */
    @ApiModelProperty("当前诊所ID")
    private Long currentClinicId;
}
