package com.yujian.admin.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 更新预约状态入参
 *
 * @author Zhangyk
 * @date 2026-08-18 17:20
 */
@Data
@ApiModel("预约改状态请求")
public class AppointmentStatusRequest {

    /** 预约ID */
    @ApiModelProperty(value = "预约ID", required = true, example = "1")
    private Long id;

    /** 预约状态：1已预约 2已确认 3已到达 4治疗中 5已离开 6已过期 7已流失 8预约未到 */
    @ApiModelProperty(value = "状态 1已预约~8预约未到", required = true, example = "2")
    private Integer status;

    /** 操作备注（可选） */
    @ApiModelProperty(value = "备注", example = "患者已到店")
    private String remark;
}
