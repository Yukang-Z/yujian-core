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
 * 菜单/权限管理接口。
 * <p>
 * 提供系统菜单的增删改查、树形结构查询，以及按员工查询菜单树与权限标识。
 * 菜单为全局维度，不按当前诊所隔离；需已登录。
 * 前端按钮权限分别对应 system:menu:query/add/edit/remove。
 * </p>
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
     * 按条件查询菜单扁平列表。
     *
     * @param menu 筛选条件，支持 menuName（菜单名称）、menuType（M目录/C菜单/F按钮）、
     *             platform（web/mobile）、status（0正常 1停用）等字段
     * @return 统一响应；data 为 {@link SysMenu} 列表
     */
    @ApiOperation("菜单列表")
    @GetMapping("/list")
    public R<List<SysMenu>> list(SysMenu menu) {
        return R.ok(menuService.selectMenuList(menu));
    }

    /**
     * 查询指定平台的菜单树（服务端已组装 children）。
     *
     * @param platform 适用平台：web 网页端，mobile 移动端，默认 web
     * @return 统一响应；data 为 {@link SysMenu} 树形列表，根节点含嵌套 children
     */
    @ApiOperation("菜单树")
    @GetMapping("/tree")
    public R<List<SysMenu>> tree(@ApiParam("平台 web/mobile") @RequestParam(defaultValue = "web") String platform) {
        return R.ok(menuService.selectMenuTree(platform));
    }

    /**
     * 根据主键查询菜单详情。
     *
     * @param id 菜单ID
     * @return 统一响应；data 为 {@link SysMenu} 实体，含菜单完整信息
     */
    @ApiOperation("菜单详情")
    @GetMapping("/{id}")
    public R<SysMenu> getInfo(@ApiParam(value = "菜单ID", required = true) @PathVariable Long id) {
        return R.ok(menuService.selectMenuById(id));
    }

    /**
     * 新增菜单或按钮权限节点。
     *
     * @param menu 菜单信息，含 menuName、parentId、menuType、path、perms 等字段
     * @return 统一响应；data 为空，code=200 表示新增成功
     */
    @ApiOperation("新增菜单")
    @PostMapping
    public R<?> add(@Validated @RequestBody SysMenu menu) {
        return menuService.insertMenu(menu) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改菜单或按钮权限节点。
     *
     * @param menu 菜单信息，id 必填，其余为待更新字段
     * @return 统一响应；data 为空，code=200 表示修改成功
     */
    @ApiOperation("修改菜单")
    @PostMapping("/edit")
    public R<?> edit(@Validated @RequestBody SysMenu menu) {
        return menuService.updateMenu(menu) > 0 ? R.ok() : R.fail();
    }

    /**
     * 根据主键删除菜单（存在子菜单时不允许删除）。
     *
     * @param id 菜单ID
     * @return 统一响应；data 为空，code=200 表示删除成功
     */
    @ApiOperation("删除菜单")
    @PostMapping("/remove/{id}")
    public R<?> remove(@ApiParam(value = "菜单ID", required = true) @PathVariable Long id) {
        return menuService.deleteMenuById(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 查询指定员工在指定平台下可见的菜单树。
     * <p>
     * 按员工角色聚合菜单，常用于角色授权预览或员工权限核查。
     * </p>
     *
     * @param employeeId 员工ID
     * @param platform   适用平台：web / mobile，默认 web
     * @return 统一响应；data 为 {@link SysMenu} 树形列表，仅含该员工有权限的菜单
     */
    @ApiOperation("员工菜单树")
    @GetMapping("/employee/{employeeId}")
    public R<List<SysMenu>> employeeMenus(
            @ApiParam(value = "员工ID", required = true) @PathVariable Long employeeId,
            @ApiParam("平台 web/mobile") @RequestParam(defaultValue = "web") String platform) {
        return R.ok(menuService.selectMenusByEmployeeId(employeeId, platform));
    }

    /**
     * 查询指定员工的权限标识列表。
     * <p>
     * 返回该员工所有角色关联菜单的 perms 字段集合，用于按钮级鉴权。
     * </p>
     *
     * @param employeeId 员工ID
     * @return 统一响应；data 为 String 类型的权限标识列表，如 system:employee:list
     */
    @ApiOperation("员工权限标识")
    @GetMapping("/employee/{employeeId}/perms")
    public R<List<String>> employeePerms(
            @ApiParam(value = "员工ID", required = true) @PathVariable Long employeeId) {
        return R.ok(menuService.selectPermsByEmployeeId(employeeId));
    }
}
