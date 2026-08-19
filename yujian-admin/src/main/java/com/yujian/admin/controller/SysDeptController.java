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
 * 部门管理接口。
 * <p>
 * 提供当前诊所下部门的增删改查；数据按 Session 中当前所选诊所（clinicId）隔离。
 * 需已登录且已选定诊所，否则拦截器返回「请先选择诊所后再操作」。
 * </p>
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
     * 查询当前诊所下的部门列表。
     *
     * @param dept 筛选条件，支持 deptName（部门名称）、status（0正常 1停用）
     * @return 统一响应；data 为 {@link SysDept} 列表，仅含当前诊所数据
     */
    @ApiOperation("部门列表")
    @GetMapping("/list")
    public R<List<SysDept>> list(SysDept dept) {
        return R.ok(deptService.selectDeptList(dept));
    }

    /**
     * 根据主键查询部门详情。
     *
     * @param id 部门ID
     * @return 统一响应；data 为 {@link SysDept} 实体，含部门完整信息
     */
    @ApiOperation("部门详情")
    @GetMapping("/{id}")
    public R<SysDept> getInfo(@ApiParam(value = "部门ID", required = true) @PathVariable Long id) {
        return R.ok(deptService.getById(id));
    }

    /**
     * 在当前诊所下新增部门。
     * <p>
     * 请求体未传 clinicId 时，自动写入 Session 中的当前诊所ID。
     * </p>
     *
     * @param dept 部门信息，含 deptName、parentId 等字段
     * @return 统一响应；data 为空，code=200 表示新增成功
     */
    @ApiOperation("新增部门")
    @PostMapping
    public R<?> add(@Validated @RequestBody SysDept dept) {
        return deptService.insertDept(dept) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改部门信息。
     *
     * @param dept 部门信息，id 必填，其余为待更新字段
     * @return 统一响应；data 为空，code=200 表示修改成功
     */
    @ApiOperation("修改部门")
    @PostMapping("/edit")
    public R<?> edit(@Validated @RequestBody SysDept dept) {
        return deptService.updateDept(dept) > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除部门（存在下级部门时不允许删除）。
     *
     * @param id 部门ID
     * @return 统一响应；data 为空，code=200 表示删除成功
     */
    @ApiOperation("删除部门")
    @PostMapping("/remove/{id}")
    public R<?> remove(@ApiParam(value = "部门ID", required = true) @PathVariable Long id) {
        return deptService.deleteDeptById(id) > 0 ? R.ok() : R.fail();
    }
}
