package com.yujian.admin.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 按主键改状态入参（员工启停等）
 *
 * @author Zhangyk
 * @date 2026-08-18 17:20
 */
@Data
@ApiModel("主键+状态请求")
public class IdStatusRequest {

    /** 业务主键ID */
    @ApiModelProperty(value = "主键ID", required = true, example = "1")
    private Long id;

    /** 状态值：员工账号 0正常 1停用 */
    @ApiModelProperty(value = "状态：0正常 1停用", required = true, example = "0")
    private Integer status;
}
