package com.yujian.admin.controller;

import com.yujian.admin.service.ISysRoleService;
import com.yujian.common.core.domain.R;
import com.yujian.common.system.domain.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 角色设置
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    @Autowired
    private ISysRoleService roleService;

    /** 角色列表 */
    @GetMapping("/list")
    public R<List<SysRole>> list(SysRole role) {
        return R.ok(roleService.selectRoleList(role));
    }

    /** 角色详情（含菜单权限） */
    @GetMapping("/{id}")
    public R<SysRole> getInfo(@PathVariable Long id) {
        return R.ok(roleService.selectRoleById(id));
    }

    /** 新增角色 */
    @PostMapping
    public R<?> add(@Validated @RequestBody SysRole role) {
        return roleService.insertRole(role) > 0 ? R.ok() : R.fail();
    }

    /** 修改角色 */
    @PutMapping
    public R<?> edit(@Validated @RequestBody SysRole role) {
        return roleService.updateRole(role) > 0 ? R.ok() : R.fail();
    }

    /** 删除角色 */
    @DeleteMapping("/{id}")
    public R<?> remove(@PathVariable Long id) {
        return roleService.deleteRoleById(id) > 0 ? R.ok() : R.fail();
    }

    /** 保存角色菜单权限 */
    @PutMapping("/auth")
    public R<?> auth(@RequestBody Map<String, Object> body) {
        Long roleId = Long.valueOf(String.valueOf(body.get("roleId")));
        @SuppressWarnings("unchecked")
        List<Integer> menuIdInts = (List<Integer>) body.get("menuIds");
        List<Long> menuIds = null;
        if (menuIdInts != null) {
            menuIds = new java.util.ArrayList<Long>();
            for (Integer mid : menuIdInts) {
                menuIds.add(mid.longValue());
            }
        }
        return roleService.saveRoleMenus(roleId, menuIds) > 0 ? R.ok() : R.fail();
    }

    /** 查询角色已选菜单ID */
    @GetMapping("/{id}/menus")
    public R<List<Long>> menus(@PathVariable Long id) {
        return R.ok(roleService.selectMenuIdsByRoleId(id));
    }

    /** 角色上移/下移 */
    @PutMapping("/move/{id}/{direction}")
    public R<?> move(@PathVariable Long id, @PathVariable String direction) {
        return roleService.moveRole(id, direction) > 0 ? R.ok() : R.fail("已到边界，无法移动");
    }
}
