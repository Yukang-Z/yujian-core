package com.yujian.admin.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 新增患者并附带动作后的返回
 *
 * @author Zhangyk
 * @date 2026-08-18 17:20
 */
@Data
@ApiModel("患者保存动作结果")
public class PatientActionVO {

    /** 患者ID */
    @ApiModelProperty("患者ID")
    private Long patientId;

    /** 执行动作：save / arrive / appoint */
    @ApiModelProperty("动作 save|arrive|appoint")
    private String action;
}
