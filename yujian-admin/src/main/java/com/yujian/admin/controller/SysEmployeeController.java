package com.yujian.admin.controller;

import com.yujian.admin.service.ISysEmployeeService;
import com.yujian.common.core.domain.PageResult;
import com.yujian.common.core.domain.R;
import com.yujian.common.system.domain.SysEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/system/employee")
public class SysEmployeeController {

    @Autowired
    private ISysEmployeeService employeeService;

    /**
     * 员工分页列表
     * keyword: 姓名/手机号/工号
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

    /** 员工详情 */
    @GetMapping("/{id}")
    public R<SysEmployee> getInfo(@PathVariable Long id) {
        return R.ok(employeeService.selectEmployeeById(id));
    }

    /** 新增员工 */
    @PostMapping
    public R<?> add(@Validated @RequestBody SysEmployee employee) {
        return employeeService.insertEmployee(employee) > 0 ? R.ok() : R.fail();
    }

    /** 修改员工 */
    @PutMapping
    public R<?> edit(@Validated @RequestBody SysEmployee employee) {
        return employeeService.updateEmployee(employee) > 0 ? R.ok() : R.fail();
    }

    /** 删除员工 */
    @DeleteMapping("/{id}")
    public R<?> remove(@PathVariable Long id) {
        return employeeService.deleteEmployeeById(id) > 0 ? R.ok() : R.fail();
    }

    /** 重置密码 */
    @PutMapping("/resetPwd")
    public R<?> resetPwd(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(String.valueOf(params.get("id")));
        String password = params.get("password") == null ? null : String.valueOf(params.get("password"));
        return employeeService.resetPassword(id, password) > 0 ? R.ok() : R.fail();
    }

    /** 调整排序 up/down */
    @PutMapping("/sort/{id}/{direction}")
    public R<?> sort(@PathVariable Long id, @PathVariable String direction) {
        return employeeService.updateSortOrder(id, direction) > 0 ? R.ok() : R.fail("已到边界，无法移动");
    }
}
