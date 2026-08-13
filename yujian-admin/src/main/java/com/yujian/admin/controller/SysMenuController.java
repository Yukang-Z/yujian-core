package com.yujian.admin.controller;

import com.yujian.admin.service.ISysMenuService;
import com.yujian.common.core.domain.R;
import com.yujian.common.system.domain.SysMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限/菜单管理
 */
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    @Autowired
    private ISysMenuService menuService;

    /** 菜单列表（扁平） */
    @GetMapping("/list")
    public R<List<SysMenu>> list(SysMenu menu) {
        return R.ok(menuService.selectMenuList(menu));
    }

    /** 菜单树（按平台：web / mobile） */
    @GetMapping("/tree")
    public R<List<SysMenu>> tree(@RequestParam(defaultValue = "web") String platform) {
        return R.ok(menuService.selectMenuTree(platform));
    }

    /** 菜单详情 */
    @GetMapping("/{id}")
    public R<SysMenu> getInfo(@PathVariable Long id) {
        return R.ok(menuService.selectMenuById(id));
    }

    /** 新增菜单 */
    @PostMapping
    public R<?> add(@Validated @RequestBody SysMenu menu) {
        return menuService.insertMenu(menu) > 0 ? R.ok() : R.fail();
    }

    /** 修改菜单 */
    @PutMapping
    public R<?> edit(@Validated @RequestBody SysMenu menu) {
        return menuService.updateMenu(menu) > 0 ? R.ok() : R.fail();
    }

    /** 删除菜单 */
    @DeleteMapping("/{id}")
    public R<?> remove(@PathVariable Long id) {
        return menuService.deleteMenuById(id) > 0 ? R.ok() : R.fail();
    }

    /** 查询员工菜单树 */
    @GetMapping("/employee/{employeeId}")
    public R<List<SysMenu>> employeeMenus(@PathVariable Long employeeId,
                                          @RequestParam(defaultValue = "web") String platform) {
        return R.ok(menuService.selectMenusByEmployeeId(employeeId, platform));
    }

    /** 查询员工权限标识 */
    @GetMapping("/employee/{employeeId}/perms")
    public R<List<String>> employeePerms(@PathVariable Long employeeId) {
        return R.ok(menuService.selectPermsByEmployeeId(employeeId));
    }
}
