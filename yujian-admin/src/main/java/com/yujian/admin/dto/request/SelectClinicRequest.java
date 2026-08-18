package com.yujian.admin.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 登录后选择诊所入参
 *
 * @author Zhangyk
 * @date 2026-08-18 17:20
 */
@Data
@ApiModel("选择诊所请求")
public class SelectClinicRequest {

    /** 要进入的诊所ID，须为当前员工已关联诊所 */
    @ApiModelProperty(value = "诊所ID", required = true, example = "1")
    private Long clinicId;
}
