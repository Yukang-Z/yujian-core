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
 * 员工管理接口。
 * <p>
 * 提供员工的增删改查、密码重置、启停及同诊所内排序；员工与诊所为一对多关联。
 * 列表与写操作按 Session 中当前所选诊所隔离；需已登录且已选定诊所。
 * 前端按钮权限分别对应 system:employee:query/add/edit/remove/resetPwd。
 * </p>
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
     * 分页查询当前诊所下的员工列表。
     *
     * @param keyword      关键字，匹配姓名/手机号/工号
     * @param clinicId     诊所ID（忽略，始终以 Session 当前诊所为准）
     * @param employStatus 在职状态：1 在职，0 离职
     * @param pageNum      页码，默认 1
     * @param pageSize     每页条数，默认 20
     * @return 统一响应；data 为 {@link PageResult}{@code <SysEmployee>}，records 含 roleIds、clinicIds
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
     * 根据主键查询员工详情。
     *
     * @param id 员工ID
     * @return 统一响应；data 为 {@link SysEmployee}，含 roleIds、clinicIds，不含密码
     */
    @ApiOperation("员工详情")
    @GetMapping("/{id}")
    public R<SysEmployee> getInfo(@ApiParam(value = "员工ID", required = true) @PathVariable Long id) {
        return R.ok(employeeService.selectEmployeeById(id));
    }

    /**
     * 新增员工并关联角色与诊所。
     * <p>
     * 可传 roleIds、clinicIds；未传 clinicIds 时默认绑定当前诊所。
     * </p>
     *
     * @param employee 员工信息，含 name、username、password、roleIds、clinicIds 等字段
     * @return 统一响应；data 为空，code=200 表示新增成功
     */
    @ApiOperation("新增员工")
    @PostMapping
    public R<?> add(@Validated @RequestBody SysEmployee employee) {
        return employeeService.insertEmployee(employee) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改员工基础信息及关联关系。
     * <p>
     * roleIds/clinicIds 为 null 时不修改对应关联；密码修改请走 {@code /resetPwd}。
     * </p>
     *
     * @param employee 员工信息，id 必填，其余为待更新字段
     * @return 统一响应；data 为空，code=200 表示修改成功
     */
    @ApiOperation("修改员工")
    @PostMapping("/edit")
    public R<?> edit(@Validated @RequestBody SysEmployee employee) {
        return employeeService.updateEmployee(employee) > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除员工及其角色、诊所关联数据。
     *
     * @param id 员工ID
     * @return 统一响应；data 为空，code=200 表示删除成功
     */
    @ApiOperation("删除员工")
    @PostMapping("/remove/{id}")
    public R<?> remove(@ApiParam(value = "员工ID", required = true) @PathVariable Long id) {
        return employeeService.deleteEmployeeById(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 重置指定员工的登录密码。
     *
     * @param params 含 id（员工ID，必填）、password（新密码，明文）
     * @return 统一响应；data 为空，code=200 表示重置成功
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
     * 启用或停用员工账号（不影响在职状态 employStatus）。
     *
     * @param params 含 id（员工ID，必填）、status（0 正常，1 停用）
     * @return 统一响应；data 为空，code=200 表示更新成功
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
     * 在当前诊所内调整员工显示排序（上移或下移）。
     *
     * @param id        员工ID
     * @param direction 移动方向：up 上移，down 下移
     * @return 统一响应；data 为空，code=200 表示排序成功；已到边界时返回失败提示
     */
    @ApiOperation("员工排序上移或下移")
    @PostMapping("/sort/{id}/{direction}")
    public R<?> sort(@ApiParam(value = "员工ID", required = true) @PathVariable Long id,
                     @ApiParam(value = "up或down", required = true) @PathVariable String direction) {
        return employeeService.updateSortOrder(id, direction) > 0 ? R.ok() : R.fail("已到边界，无法移动");
    }
}
