package com.yujian.admin.controller;

import com.yujian.admin.service.ISysDeptService;
import com.yujian.common.core.domain.R;
import com.yujian.common.system.domain.SysDept;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 部门管理接口（按当前所选诊所隔离）
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Api(tags = "部门管理")
@RestController
@RequestMapping("/system/dept")
public class SysDeptController {

    @Autowired
    private ISysDeptService deptService;

    /**
     * 当前诊所部门列表
     *
     * @param dept 筛选条件（deptName、status）
     * @return 部门列表
     */
    @ApiOperation("部门列表")
    @GetMapping("/list")
    public R<List<SysDept>> list(SysDept dept) {
        return R.ok(deptService.selectDeptList(dept));
    }

    /**
     * 部门详情
     *
     * @param id 部门ID
     * @return 部门
     */
    @ApiOperation("部门详情")
    @GetMapping("/{id}")
    public R<SysDept> getInfo(@ApiParam(value = "部门ID", required = true) @PathVariable Long id) {
        return R.ok(deptService.getById(id));
    }

    /**
     * 新增部门（未传 clinicId 时写入当前诊所）
     *
     * @param dept 部门信息
     * @return 操作结果
     */
    @ApiOperation("新增部门")
    @PostMapping
    public R<?> add(@Validated @RequestBody SysDept dept) {
        return deptService.insertDept(dept) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改部门
     *
     * @param dept 部门信息（须含 id）
     * @return 操作结果
     */
    @ApiOperation("修改部门")
    @PostMapping("/edit")
    public R<?> edit(@Validated @RequestBody SysDept dept) {
        return deptService.updateDept(dept) > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除部门（存在下级则不允许删）
     *
     * @param id 部门ID
     * @return 操作结果
     */
    @ApiOperation("删除部门")
    @PostMapping("/remove/{id}")
    public R<?> remove(@ApiParam(value = "部门ID", required = true) @PathVariable Long id) {
        return deptService.deleteDeptById(id) > 0 ? R.ok() : R.fail();
    }
}
