package com.yujian.admin.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 诊所新增/编辑请求参数
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Data
public class ClinicSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID，编辑时必填
     */
    private Long id;

    /**
     * 诊所编码
     */
    @NotBlank(message = "诊所编码不能为空")
    private String clinicCode;

    /**
     * 诊所名称
     */
    @NotBlank(message = "诊所名称不能为空")
    private String clinicName;

    /**
     * 父诊所ID，0表示顶级
     */
    private Long parentId;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 状态：1启用 0停用
     */
    private Integer status;

    /**
     * 排序号，越小越靠前
     */
    private Integer sortNo;

    /**
     * 备注
     */
    private String remark;
}
