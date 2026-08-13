package com.yujian.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 诊所树形展示对象
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Data
public class ClinicVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

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

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新人ID
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 子诊所列表，用于树形结构展示
     */
    private List<ClinicVO> children;
}
