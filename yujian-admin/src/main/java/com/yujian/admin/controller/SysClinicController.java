package com.yujian.admin.controller;

import com.yujian.admin.service.ISysClinicService;
import com.yujian.common.core.domain.R;
import com.yujian.common.system.domain.SysClinic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 诊所管理
 */
@RestController
@RequestMapping("/system/clinic")
public class SysClinicController {

    @Autowired
    private ISysClinicService clinicService;

    /** 诊所列表 */
    @GetMapping("/list")
    public R<List<SysClinic>> list(SysClinic clinic) {
        return R.ok(clinicService.selectClinicList(clinic));
    }

    /** 诊所树（含 parentId，前端可组装树） */
    @GetMapping("/tree")
    public R<List<SysClinic>> tree(SysClinic clinic) {
        return R.ok(clinicService.selectClinicList(clinic));
    }

    /** 诊所详情 */
    @GetMapping("/{id}")
    public R<SysClinic> getInfo(@PathVariable Long id) {
        return R.ok(clinicService.getById(id));
    }

    /** 新增诊所 */
    @PostMapping
    public R<?> add(@Validated @RequestBody SysClinic clinic) {
        return clinicService.insertClinic(clinic) > 0 ? R.ok() : R.fail();
    }

    /** 修改诊所 */
    @PutMapping
    public R<?> edit(@Validated @RequestBody SysClinic clinic) {
        return clinicService.updateClinic(clinic) > 0 ? R.ok() : R.fail();
    }

    /** 删除诊所 */
    @DeleteMapping("/{id}")
    public R<?> remove(@PathVariable Long id) {
        return clinicService.deleteClinicById(id) > 0 ? R.ok() : R.fail();
    }
}
