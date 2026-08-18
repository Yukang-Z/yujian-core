package com.yujian.admin.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 选择诊所成功返回
 *
 * @author Zhangyk
 * @date 2026-08-18 17:20
 */
@Data
@ApiModel("选择诊所结果")
public class SelectClinicVO {

    /** 当前进入的诊所ID */
    @ApiModelProperty("诊所ID")
    private Long clinicId;

    /** 当前进入的诊所名称 */
    @ApiModelProperty("诊所名称")
    private String clinicName;
}
