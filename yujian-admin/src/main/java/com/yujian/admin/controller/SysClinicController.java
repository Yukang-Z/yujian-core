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
 * 诊所管理接口。
 * <p>
 * 提供诊所的增删改查及树形数据查询，为全局维度的诊所维护，不按当前所选诊所隔离。
 * 需已登录；前端按钮权限分别对应 system:clinic:query/add/edit/remove。
 * </p>
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
     * 按条件查询诊所扁平列表。
     *
     * @param clinic 筛选条件，支持 clinicName（名称）、clinicCode（编码）、status（0正常 1停用）等字段
     * @return 统一响应；data 为 {@link SysClinic} 列表，按 sortOrder 排序
     */
    @ApiOperation("诊所列表")
    @GetMapping("/list")
    public R<List<SysClinic>> list(SysClinic clinic) {
        return R.ok(clinicService.selectClinicList(clinic));
    }

    /**
     * 查询诊所树形数据（扁平返回，含 parentId，由前端组装树）。
     *
     * @param clinic 筛选条件，支持 clinicName、clinicCode、status 等字段
     * @return 统一响应；data 为 {@link SysClinic} 列表，含 parentId 供前端构建树
     */
    @ApiOperation("诊所树数据")
    @GetMapping("/tree")
    public R<List<SysClinic>> tree(SysClinic clinic) {
        return R.ok(clinicService.selectClinicList(clinic));
    }

    /**
     * 根据主键查询诊所详情。
     *
     * @param id 诊所ID
     * @return 统一响应；data 为 {@link SysClinic} 实体，含诊所完整信息
     */
    @ApiOperation("诊所详情")
    @GetMapping("/{id}")
    public R<SysClinic> getInfo(@ApiParam(value = "诊所ID", required = true) @PathVariable Long id) {
        return R.ok(clinicService.getById(id));
    }

    /**
     * 新增诊所。
     *
     * @param clinic 诊所信息，含 clinicName、clinicCode、parentId 等必填/选填字段
     * @return 统一响应；data 为空，code=200 表示新增成功
     */
    @ApiOperation("新增诊所")
    @PostMapping
    public R<?> add(@Validated @RequestBody SysClinic clinic) {
        return clinicService.insertClinic(clinic) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改诊所信息。
     *
     * @param clinic 诊所信息，id 必填，其余为待更新字段
     * @return 统一响应；data 为空，code=200 表示修改成功
     */
    @ApiOperation("修改诊所")
    @PostMapping("/edit")
    public R<?> edit(@Validated @RequestBody SysClinic clinic) {
        return clinicService.updateClinic(clinic) > 0 ? R.ok() : R.fail();
    }

    /**
     * 根据主键删除诊所。
     *
     * @param id 诊所ID
     * @return 统一响应；data 为空，code=200 表示删除成功
     */
    @ApiOperation("删除诊所")
    @PostMapping("/remove/{id}")
    public R<?> remove(@ApiParam(value = "诊所ID", required = true) @PathVariable Long id) {
        return clinicService.deleteClinicById(id) > 0 ? R.ok() : R.fail();
    }
}
