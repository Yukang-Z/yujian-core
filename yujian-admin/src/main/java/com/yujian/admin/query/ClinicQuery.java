package com.yujian.admin.query;

import com.yujian.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 诊所分页查询参数
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClinicQuery extends PageQuery {

    /**
     * 诊所名称（模糊匹配）
     */
    private String clinicName;

    /**
     * 诊所编码（精确或模糊匹配）
     */
    private String clinicCode;

    /**
     * 状态：1启用 0停用
     */
    private Integer status;

    /**
     * 父诊所ID，0表示顶级
     */
    private Long parentId;
}
