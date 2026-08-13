package com.yujian.admin.controller;

import com.yujian.admin.service.ISysDeptService;
import com.yujian.common.core.domain.R;
import com.yujian.common.system.domain.SysDept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理
 */
@RestController
@RequestMapping("/system/dept")
public class SysDeptController {

    @Autowired
    private ISysDeptService deptService;

    @GetMapping("/list")
    public R<List<SysDept>> list(SysDept dept) {
        return R.ok(deptService.selectDeptList(dept));
    }

    @GetMapping("/{id}")
    public R<SysDept> getInfo(@PathVariable Long id) {
        return R.ok(deptService.getById(id));
    }

    @PostMapping
    public R<?> add(@Validated @RequestBody SysDept dept) {
        return deptService.insertDept(dept) > 0 ? R.ok() : R.fail();
    }

    @PutMapping
    public R<?> edit(@Validated @RequestBody SysDept dept) {
        return deptService.updateDept(dept) > 0 ? R.ok() : R.fail();
    }

    @DeleteMapping("/{id}")
    public R<?> remove(@PathVariable Long id) {
        return deptService.deleteDeptById(id) > 0 ? R.ok() : R.fail();
    }
}
