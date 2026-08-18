package com.yujian.admin.controller;

import com.yujian.admin.service.ISysMenuService;
import com.yujian.common.core.domain.R;
import com.yujian.common.system.domain.SysMenu;
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

import java.util.List;

/**
 * 菜单/权限管理接口
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Api(tags = "菜单权限")
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    @Autowired
    private ISysMenuService menuService;

    /**
     * 菜单扁平列表
     *
     * @param menu 筛选条件
     * @return 菜单列表
     */
    @ApiOperation("菜单列表")
    @GetMapping("/list")
    public R<List<SysMenu>> list(SysMenu menu) {
        return R.ok(menuService.selectMenuList(menu));
    }

    /**
     * 菜单树
     *
     * @param platform web 或 mobile，默认 web
     * @return 菜单树
     */
    @ApiOperation("菜单树")
    @GetMapping("/tree")
    public R<List<SysMenu>> tree(@ApiParam("平台 web/mobile") @RequestParam(defaultValue = "web") String platform) {
        return R.ok(menuService.selectMenuTree(platform));
    }

    /**
     * 菜单详情
     *
     * @param id 菜单ID
     * @return 菜单
     */
    @ApiOperation("菜单详情")
    @GetMapping("/{id}")
    public R<SysMenu> getInfo(@ApiParam(value = "菜单ID", required = true) @PathVariable Long id) {
        return R.ok(menuService.selectMenuById(id));
    }

    /**
     * 新增菜单
     *
     * @param menu 菜单信息
     * @return 操作结果
     */
    @ApiOperation("新增菜单")
    @PostMapping
    public R<?> add(@Validated @RequestBody SysMenu menu) {
        return menuService.insertMenu(menu) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改菜单
     *
     * @param menu 菜单信息（须含 id）
     * @return 操作结果
     */
    @ApiOperation("修改菜单")
    @PostMapping("/edit")
    public R<?> edit(@Validated @RequestBody SysMenu menu) {
        return menuService.updateMenu(menu) > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 操作结果
     */
    @ApiOperation("删除菜单")
    @PostMapping("/remove/{id}")
    public R<?> remove(@ApiParam(value = "菜单ID", required = true) @PathVariable Long id) {
        return menuService.deleteMenuById(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 查询员工菜单树
     *
     * @param employeeId 员工ID
     * @param platform   web / mobile
     * @return 菜单树
     */
    @ApiOperation("员工菜单树")
    @GetMapping("/employee/{employeeId}")
    public R<List<SysMenu>> employeeMenus(
            @ApiParam(value = "员工ID", required = true) @PathVariable Long employeeId,
            @ApiParam("平台 web/mobile") @RequestParam(defaultValue = "web") String platform) {
        return R.ok(menuService.selectMenusByEmployeeId(employeeId, platform));
    }

    /**
     * 查询员工权限标识
     *
     * @param employeeId 员工ID
     * @return 权限标识列表
     */
    @ApiOperation("员工权限标识")
    @GetMapping("/employee/{employeeId}/perms")
    public R<List<String>> employeePerms(
            @ApiParam(value = "员工ID", required = true) @PathVariable Long employeeId) {
        return R.ok(menuService.selectPermsByEmployeeId(employeeId));
    }
}
