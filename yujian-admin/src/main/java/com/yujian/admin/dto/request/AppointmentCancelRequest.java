package com.yujian.admin.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 取消预约入参
 *
 * @author Zhangyk
 * @date 2026-08-18 17:20
 */
@Data
@ApiModel("取消预约请求")
public class AppointmentCancelRequest {

    /** 预约ID */
    @ApiModelProperty(value = "预约ID", required = true, example = "1")
    private Long id;

    /** 取消原因 */
    @ApiModelProperty(value = "取消原因", example = "患者改期")
    private String cancelReason;
}
