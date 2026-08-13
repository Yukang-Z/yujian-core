package com.yujian.admin.query;

import com.yujian.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 员工分页查询参数
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StaffQuery extends PageQuery {

    /**
     * 关键字，匹配姓名或手机号
     */
    private String keyword;

    /**
     * 工作诊所ID，关联 sys_clinic.id
     */
    private Long clinicId;

    /**
     * 所属部门ID，关联 sys_department.id
     */
    private Long deptId;

    /**
     * 在职状态：1在职 0离职，参见 WorkStatusEnum
     */
    private Integer workStatus;

    /**
     * 账号上线状态：1上线 0下线
     */
    private Integer onlineStatus;
}
