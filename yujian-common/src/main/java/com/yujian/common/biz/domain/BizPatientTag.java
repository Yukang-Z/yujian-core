package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yujian.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 患者标签实体，对应表 t_patient_tag。
 * 用于业务接口请求/响应数据传输（标签维护、患者筛选等场景）。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_patient_tag")
public class BizPatientTag extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 诊所ID，关联 t_clinic.id */
    private Long clinicId;

    /** 标签名称 */
    private String tagName;

    /** 标签颜色（十六进制色值，用于前端展示） */
    private String tagColor;

    /** 排序号，数值越小越靠前 */
    private Integer sortOrder;

    /** 状态：0正常 1停用 */
    private Integer status;
}
