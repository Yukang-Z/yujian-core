package com.yujian.admin.controller;

import com.yujian.admin.service.ISysClinicService;
import com.yujian.common.core.domain.R;
import com.yujian.common.system.domain.SysClinic;
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
 * 诊所管理接口
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Api(tags = "诊所管理")
@RestController
@RequestMapping("/system/clinic")
public class SysClinicController {

    @Autowired
    private ISysClinicService clinicService;

    /**
     * 诊所列表（支持名称/编码/状态筛选）
     *
     * @param clinic 筛选条件
     * @return 诊所列表
     */
    @ApiOperation("诊所列表")
    @GetMapping("/list")
    public R<List<SysClinic>> list(SysClinic clinic) {
        return R.ok(clinicService.selectClinicList(clinic));
    }

    /**
     * 诊所树数据（含 parentId，前端组装树）
     *
     * @param clinic 筛选条件
     * @return 诊所列表
     */
    @ApiOperation("诊所树数据")
    @GetMapping("/tree")
    public R<List<SysClinic>> tree(SysClinic clinic) {
        return R.ok(clinicService.selectClinicList(clinic));
    }

    /**
     * 诊所详情
     *
     * @param id 诊所ID
     * @return 诊所
     */
    @ApiOperation("诊所详情")
    @GetMapping("/{id}")
    public R<SysClinic> getInfo(@ApiParam(value = "诊所ID", required = true) @PathVariable Long id) {
        return R.ok(clinicService.getById(id));
    }

    /**
     * 新增诊所
     *
     * @param clinic 诊所信息
     * @return 操作结果
     */
    @ApiOperation("新增诊所")
    @PostMapping
    public R<?> add(@Validated @RequestBody SysClinic clinic) {
        return clinicService.insertClinic(clinic) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改诊所
     *
     * @param clinic 诊所信息（须含 id）
     * @return 操作结果
     */
    @ApiOperation("修改诊所")
    @PostMapping("/edit")
    public R<?> edit(@Validated @RequestBody SysClinic clinic) {
        return clinicService.updateClinic(clinic) > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除诊所
     *
     * @param id 诊所ID
     * @return 操作结果
     */
    @ApiOperation("删除诊所")
    @PostMapping("/remove/{id}")
    public R<?> remove(@ApiParam(value = "诊所ID", required = true) @PathVariable Long id) {
        return clinicService.deleteClinicById(id) > 0 ? R.ok() : R.fail();
    }
}
