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
 * 诊所实体，对应表 t_clinic
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_clinic")
public class SysClinic extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 诊所ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父诊所ID（0 为总部） */
    private Long parentId;

    /** 诊所名称 */
    private String clinicName;

    /** 诊所编码（唯一） */
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

    /** Logo 地址 */
    private String logo;

    /** 排序号，越小越靠前 */
    private Integer sortOrder;

    /** 状态：0正常 1停用 */
    private Integer status;

    /** 开业日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date openDate;
}
