package com.yujian.admin.controller;

import com.yujian.admin.service.ISysEmployeeService;
import com.yujian.common.core.domain.PageResult;
import com.yujian.common.core.domain.R;
import com.yujian.common.exception.BusinessException;
import com.yujian.common.system.domain.SysEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 员工管理接口
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@RestController
@RequestMapping("/system/employee")
public class SysEmployeeController {

    @Autowired
    private ISysEmployeeService employeeService;

    /**
     * 员工分页列表（含 roleIds）
     *
     * @param keyword      姓名/手机号/工号
     * @param clinicId     诊所ID
     * @param deptId       部门ID
     * @param employStatus 在职状态
     * @param pageNum      页码
     * @param pageSize     每页条数
     * @return 分页数据
     */
    @GetMapping("/list")
    public R<PageResult<SysEmployee>> list(@RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) Long clinicId,
                                           @RequestParam(required = false) Long deptId,
                                           @RequestParam(required = false) Integer employStatus,
                                           @RequestParam(defaultValue = "1") long pageNum,
                                           @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(employeeService.selectEmployeePage(keyword, clinicId, deptId, employStatus, pageNum, pageSize));
    }

    /**
     * 员工详情
     *
     * @param id 员工ID
     * @return 员工信息
     */
    @GetMapping("/{id}")
    public R<SysEmployee> getInfo(@PathVariable Long id) {
        return R.ok(employeeService.selectEmployeeById(id));
    }

    /**
     * 新增员工
     *
     * @param employee 员工（可含 roleIds）
     * @return 操作结果
     */
    @PostMapping
    public R<?> add(@Validated @RequestBody SysEmployee employee) {
        return employeeService.insertEmployee(employee) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改员工基础信息；不传 roleIds 则保留原角色；密码请走 resetPwd
     *
     * @param employee 员工（id 必填）
     * @return 操作结果
     */
    @PutMapping
    public R<?> edit(@Validated @RequestBody SysEmployee employee) {
        return employeeService.updateEmployee(employee) > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除员工
     *
     * @param id 员工ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public R<?> remove(@PathVariable Long id) {
        return employeeService.deleteEmployeeById(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 重置密码（仅改密码）
     *
     * @param params id、password
     * @return 操作结果
     */
    @PutMapping("/resetPwd")
    public R<?> resetPwd(@RequestBody Map<String, Object> params) {
        if (params.get("id") == null) {
            throw new BusinessException("员工ID不能为空");
        }
        Long id = Long.valueOf(String.valueOf(params.get("id")));
        String password = params.get("password") == null ? null : String.valueOf(params.get("password"));
        return employeeService.resetPassword(id, password) > 0 ? R.ok() : R.fail();
    }

    /**
     * 仅启用/停用账号，不改其它字段与角色
     *
     * @param params id、status（0正常 1停用）
     * @return 操作结果
     */
    @PutMapping("/status")
    public R<?> changeStatus(@RequestBody Map<String, Object> params) {
        if (params.get("id") == null) {
            throw new BusinessException("员工ID不能为空");
        }
        if (params.get("status") == null) {
            throw new BusinessException("状态不能为空");
        }
        Long id = Long.valueOf(String.valueOf(params.get("id")));
        Integer status = Integer.valueOf(String.valueOf(params.get("status")));
        return employeeService.updateStatus(id, status) > 0 ? R.ok() : R.fail();
    }

    /**
     * 调整排序 up/down
     *
     * @param id        员工ID
     * @param direction up / down
     * @return 操作结果
     */
    @PutMapping("/sort/{id}/{direction}")
    public R<?> sort(@PathVariable Long id, @PathVariable String direction) {
        return employeeService.updateSortOrder(id, direction) > 0 ? R.ok() : R.fail("已到边界，无法移动");
    }
}
