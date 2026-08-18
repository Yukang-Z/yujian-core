package com.yujian.admin.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 登录请求入参
 *
 * @author Zhangyk
 * @date 2026-08-18 17:20
 */
@Data
@ApiModel("登录请求")
public class LoginRequest {

    /** 登录账号 */
    @ApiModelProperty(value = "登录账号", required = true, example = "admin")
    private String username;

    /** 登录密码（明文） */
    @ApiModelProperty(value = "登录密码", required = true, example = "123456")
    private String password;
}
