package com.yujian.common.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yujian.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 诊所实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_clinic")
public class SysClinic extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父诊所ID（0为总部） */
    private Long parentId;

    /** 诊所名称 */
    private String clinicName;

    /** 诊所编码 */
    private String clinicCode;

    /** 诊所简称 */
    private String shortName;

    /** 联系人 */
    private String contactName;

    /** 联系电话 */
    private String contactPhone;

    /** 省 */
    private String province;

    /** 市 */
    private String city;

    /** 区 */
    private String district;

    /** 详细地址 */
    private String address;

    /** 营业时间 */
    private String businessHours;

    /** Logo */
    private String logo;

    /** 排序 */
    private Integer sortOrder;

    /** 状态（0正常 1停用） */
    private Integer status;

    /** 开业日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date openDate;
}
