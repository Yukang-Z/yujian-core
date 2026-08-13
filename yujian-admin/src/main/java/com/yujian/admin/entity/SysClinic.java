package com.yujian.admin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yujian.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 诊所实体，对应表 sys_clinic
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_clinic")
public class SysClinic extends BaseEntity {

    /**
     * 诊所编码
     */
    private String clinicCode;

    /**
     * 诊所名称
     */
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
