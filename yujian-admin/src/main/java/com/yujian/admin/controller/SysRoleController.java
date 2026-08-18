package com.yujian.admin.controller;

import com.yujian.admin.dto.request.RoleAuthRequest;
import com.yujian.admin.service.ISysRoleService;
import com.yujian.common.core.domain.R;
import com.yujian.common.system.domain.SysRole;
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
 * 角色设置接口
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Api(tags = "角色设置")
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    @Autowired
    private ISysRoleService roleService;

    /**
     * 角色列表
     *
     * @param role 筛选条件（roleName、roleKey、status）
     * @return 角色列表
     */
    @ApiOperation("角色列表")
    @GetMapping("/list")
    public R<List<SysRole>> list(SysRole role) {
        return R.ok(roleService.selectRoleList(role));
    }

    /**
     * 角色详情（含 menuIds）
     *
     * @param id 角色ID
     * @return 角色
     */
    @ApiOperation("角色详情")
    @GetMapping("/{id}")
    public R<SysRole> getInfo(@ApiParam(value = "角色ID", required = true) @PathVariable Long id) {
        return R.ok(roleService.selectRoleById(id));
    }

    /**
     * 新增角色，可同时带 menuIds
     *
     * @param role 角色信息
     * @return 操作结果
     */
    @ApiOperation("新增角色")
    @PostMapping
    public R<?> add(@Validated @RequestBody SysRole role) {
        return roleService.insertRole(role) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改角色
     *
     * @param role 角色信息（须含 id）
     * @return 操作结果
     */
    @ApiOperation("修改角色")
    @PostMapping("/edit")
    public R<?> edit(@Validated @RequestBody SysRole role) {
        return roleService.updateRole(role) > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 操作结果
     */
    @ApiOperation("删除角色")
    @PostMapping("/remove/{id}")
    public R<?> remove(@ApiParam(value = "角色ID", required = true) @PathVariable Long id) {
        return roleService.deleteRoleById(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 保存角色菜单权限
     *
     * @param body 角色ID与菜单ID列表
     * @return 操作结果
     */
    @ApiOperation("保存角色菜单权限")
    @PostMapping("/auth")
    public R<?> auth(@RequestBody RoleAuthRequest body) {
        return roleService.saveRoleMenus(body.getRoleId(), body.getMenuIds()) > 0 ? R.ok() : R.fail();
    }

    /**
     * 查询角色已选菜单ID
     *
     * @param id 角色ID
     * @return 菜单ID列表
     */
    @ApiOperation("角色已选菜单")
    @GetMapping("/{id}/menus")
    public R<List<Long>> menus(@ApiParam(value = "角色ID", required = true) @PathVariable Long id) {
        return R.ok(roleService.selectMenuIdsByRoleId(id));
    }

    /**
     * 角色排序上移/下移
     *
     * @param id        角色ID
     * @param direction up / down
     * @return 操作结果
     */
    @ApiOperation("角色排序上移或下移")
    @PostMapping("/move/{id}/{direction}")
    public R<?> move(@ApiParam(value = "角色ID", required = true) @PathVariable Long id,
                     @ApiParam(value = "up或down", required = true) @PathVariable String direction) {
        return roleService.moveRole(id, direction) > 0 ? R.ok() : R.fail("已到边界，无法移动");
    }
}
