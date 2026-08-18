package com.yujian.admin.controller;

import com.yujian.admin.dto.request.IdPasswordRequest;
import com.yujian.admin.dto.request.IdStatusRequest;
import com.yujian.admin.service.ISysEmployeeService;
import com.yujian.common.core.domain.PageResult;
import com.yujian.common.core.domain.R;
import com.yujian.common.exception.BusinessException;
import com.yujian.common.system.domain.SysEmployee;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 员工管理接口（按当前所选诊所隔离，一对多关联诊所）
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Api(tags = "员工管理")
@RestController
@RequestMapping("/system/employee")
public class SysEmployeeController {

    @Autowired
    private ISysEmployeeService employeeService;

    /**
     * 当前诊所员工分页（含 roleIds、clinicIds）
     *
     * @param keyword      姓名/手机号/工号
     * @param clinicId     忽略，以当前所选诊所为准
     * @param employStatus 在职状态 1在职 0离职
     * @param pageNum      页码
     * @param pageSize     每页条数
     * @return 分页数据
     */
    @ApiOperation("员工分页列表")
    @GetMapping("/list")
    public R<PageResult<SysEmployee>> list(
            @ApiParam("姓名/手机/工号") @RequestParam(required = false) String keyword,
            @ApiParam("诊所ID（忽略）") @RequestParam(required = false) Long clinicId,
            @ApiParam("在职状态 1在职 0离职") @RequestParam(required = false) Integer employStatus,
            @ApiParam("页码") @RequestParam(defaultValue = "1") long pageNum,
            @ApiParam("每页条数") @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(employeeService.selectEmployeePage(keyword, clinicId, employStatus, pageNum, pageSize));
    }

    /**
     * 员工详情（含角色、关联诊所，不含密码）
     *
     * @param id 员工ID
     * @return 员工信息
     */
    @ApiOperation("员工详情")
    @GetMapping("/{id}")
    public R<SysEmployee> getInfo(@ApiParam(value = "员工ID", required = true) @PathVariable Long id) {
        return R.ok(employeeService.selectEmployeeById(id));
    }

    /**
     * 新增员工。可传 roleIds、clinicIds；未传诊所则绑定当前诊所。
     *
     * @param employee 员工信息
     * @return 操作结果
     */
    @ApiOperation("新增员工")
    @PostMapping
    public R<?> add(@Validated @RequestBody SysEmployee employee) {
        return employeeService.insertEmployee(employee) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改员工。roleIds/clinicIds 为 null 不改关联；密码请走 resetPwd。
     *
     * @param employee 员工信息（id 必填）
     * @return 操作结果
     */
    @ApiOperation("修改员工")
    @PostMapping("/edit")
    public R<?> edit(@Validated @RequestBody SysEmployee employee) {
        return employeeService.updateEmployee(employee) > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除员工及其角色、诊所关联
     *
     * @param id 员工ID
     * @return 操作结果
     */
    @ApiOperation("删除员工")
    @PostMapping("/remove/{id}")
    public R<?> remove(@ApiParam(value = "员工ID", required = true) @PathVariable Long id) {
        return employeeService.deleteEmployeeById(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 仅重置密码
     *
     * @param params 员工ID与新密码
     * @return 操作结果
     */
    @ApiOperation("重置密码")
    @PostMapping("/resetPwd")
    public R<?> resetPwd(@RequestBody IdPasswordRequest params) {
        if (params == null || params.getId() == null) {
            throw new BusinessException("员工ID不能为空");
        }
        return employeeService.resetPassword(params.getId(), params.getPassword()) > 0 ? R.ok() : R.fail();
    }

    /**
     * 仅启用/停用账号
     *
     * @param params id、status（0正常 1停用）
     * @return 操作结果
     */
    @ApiOperation("启用或停用员工")
    @PostMapping("/status")
    public R<?> changeStatus(@RequestBody IdStatusRequest params) {
        if (params == null || params.getId() == null) {
            throw new BusinessException("员工ID不能为空");
        }
        if (params.getStatus() == null) {
            throw new BusinessException("状态不能为空");
        }
        return employeeService.updateStatus(params.getId(), params.getStatus()) > 0 ? R.ok() : R.fail();
    }

    /**
     * 同诊所内调整排序
     *
     * @param id        员工ID
     * @param direction up 上移 / down 下移
     * @return 操作结果
     */
    @ApiOperation("员工排序上移或下移")
    @PostMapping("/sort/{id}/{direction}")
    public R<?> sort(@ApiParam(value = "员工ID", required = true) @PathVariable Long id,
                     @ApiParam(value = "up或down", required = true) @PathVariable String direction) {
        return employeeService.updateSortOrder(id, direction) > 0 ? R.ok() : R.fail("已到边界，无法移动");
    }
}
