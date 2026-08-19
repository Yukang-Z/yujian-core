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
 * 诊所实体，映射表 t_clinic；
 * 作为诊所管理相关接口（列表/详情/新增/修改）的请求与回参字段说明。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_clinic")
public class SysClinic extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 诊所ID（主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父诊所ID，关联 t_clinic.id（0 表示总部/顶级） */
    private Long parentId;

    /** 诊所全称 */
    private String clinicName;

    /** 诊所编码（唯一，业务标识） */
    private String clinicCode;

    /** 诊所简称 */
    private String shortName;

    /** 联系人姓名 */
    private String contactName;

    /** 联系人电话 */
    private String contactPhone;

    /** 所在省份 */
    private String province;

    /** 所在城市 */
    private String city;

    /** 所在区县 */
    private String district;

    /** 详细地址 */
    private String address;

    /** 营业时间描述（如 09:00-18:00） */
    private String businessHours;

    /** 诊所 Logo 图片地址（URL） */
    private String logo;

    /** 排序号，数值越小越靠前 */
    private Integer sortOrder;

    /** 诊所状态：0 正常，1 停用 */
    private Integer status;

    /** 开业日期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date openDate;
}
