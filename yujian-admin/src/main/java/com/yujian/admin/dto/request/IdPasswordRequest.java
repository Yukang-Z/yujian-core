package com.yujian.admin.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 重置密码入参
 *
 * @author Zhangyk
 * @date 2026-08-18 17:20
 */
@Data
@ApiModel("重置密码请求")
public class IdPasswordRequest {

    /** 员工ID */
    @ApiModelProperty(value = "员工ID", required = true, example = "1")
    private Long id;

    /** 新明文密码，空则重置为默认密码 123456 */
    @ApiModelProperty(value = "新密码，空则用默认密码123456", example = "123456")
    private String password;
}
