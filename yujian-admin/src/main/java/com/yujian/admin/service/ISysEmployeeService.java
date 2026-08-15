package com.yujian.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yujian.common.core.domain.PageResult;
import com.yujian.common.system.domain.SysEmployee;

/**
 * 员工管理服务
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
public interface ISysEmployeeService extends IService<SysEmployee> {

    /**
     * 员工分页列表（含诊所/部门名称、角色名与 roleIds）
     *
     * @param keyword      姓名/手机/工号关键字
     * @param clinicId     诊所ID
     * @param deptId       部门ID
     * @param employStatus 在职状态
     * @param pageNum      页码
     * @param pageSize     每页条数
     * @return 分页结果
     */
    PageResult<SysEmployee> selectEmployeePage(String keyword, Long clinicId, Long deptId,
                                               Integer employStatus, long pageNum, long pageSize);

    /**
     * 员工详情（含 roleIds，不含密码）
     *
     * @param id 员工ID
     * @return 员工信息
     */
    SysEmployee selectEmployeeById(Long id);

    /**
     * 新增员工并写入角色关联
     *
     * @param employee 员工（可含 roleIds）
     * @return 影响行数
     */
    int insertEmployee(SysEmployee employee);

    /**
     * 修改员工基础信息；roleIds 为 null 时不改角色，非 null 时全量同步关联
     *
     * @param employee 员工（id 必填；勿传 password）
     * @return 影响行数
     */
    int updateEmployee(SysEmployee employee);

    /**
     * 删除员工及其角色关联
     *
     * @param id 员工ID
     * @return 影响行数
     */
    int deleteEmployeeById(Long id);

    /**
     * 重置密码（仅改密码字段）
     *
     * @param id       员工ID
     * @param password 新明文密码，空则用默认密码
     * @return 影响行数
     */
    int resetPassword(Long id, String password);

    /**
     * 仅更新账号启用/停用状态，不触碰角色与其它字段
     *
     * @param id     员工ID
     * @param status 0正常 1停用
     * @return 影响行数
     */
    int updateStatus(Long id, Integer status);

    /**
     * 同诊所内调整排序（与相邻记录交换 sortOrder）
     *
     * @param id        员工ID
     * @param direction up / down
     * @return 1成功 0已到边界
     */
    int updateSortOrder(Long id, String direction);

    /**
     * 校验登录账号唯一（排除自身）
     *
     * @param employee 含 username、id
     * @return true=唯一
     */
    boolean checkUsernameUnique(SysEmployee employee);

    /**
     * 校验工号唯一（排除自身）
     *
     * @param employee 含 empNo、id
     * @return true=唯一
     */
    boolean checkEmpNoUnique(SysEmployee employee);
}
