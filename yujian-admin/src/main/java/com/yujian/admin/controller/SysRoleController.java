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
 * 角色设置接口。
 * <p>
 * 提供角色的增删改查、菜单权限分配及排序；角色为全局维度，不按当前诊所隔离。
 * 需已登录；前端按钮权限分别对应 system:role:query/add/edit/remove/auth。
 * </p>
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
     * 按条件查询角色列表。
     *
     * @param role 筛选条件，支持 roleName（角色名称）、roleKey（角色编码）、status（0正常 1停用）
     * @return 统一响应；data 为 {@link SysRole} 列表，按 sortOrder 排序
     */
    @ApiOperation("角色列表")
    @GetMapping("/list")
    public R<List<SysRole>> list(SysRole role) {
        return R.ok(roleService.selectRoleList(role));
    }

    /**
     * 根据主键查询角色详情（含已分配菜单ID）。
     *
     * @param id 角色ID
     * @return 统一响应；data 为 {@link SysRole}，含 menuIds 字段
     */
    @ApiOperation("角色详情")
    @GetMapping("/{id}")
    public R<SysRole> getInfo(@ApiParam(value = "角色ID", required = true) @PathVariable Long id) {
        return R.ok(roleService.selectRoleById(id));
    }

    /**
     * 新增角色，可同时指定初始菜单权限。
     *
     * @param role 角色信息，含 roleName、roleKey 等字段，可选 menuIds
     * @return 统一响应；data 为空，code=200 表示新增成功
     */
    @ApiOperation("新增角色")
    @PostMapping
    public R<?> add(@Validated @RequestBody SysRole role) {
        return roleService.insertRole(role) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改角色基础信息。
     *
     * @param role 角色信息，id 必填，其余为待更新字段
     * @return 统一响应；data 为空，code=200 表示修改成功
     */
    @ApiOperation("修改角色")
    @PostMapping("/edit")
    public R<?> edit(@Validated @RequestBody SysRole role) {
        return roleService.updateRole(role) > 0 ? R.ok() : R.fail();
    }

    /**
     * 根据主键删除角色及其菜单关联。
     *
     * @param id 角色ID
     * @return 统一响应；data 为空，code=200 表示删除成功
     */
    @ApiOperation("删除角色")
    @PostMapping("/remove/{id}")
    public R<?> remove(@ApiParam(value = "角色ID", required = true) @PathVariable Long id) {
        return roleService.deleteRoleById(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 保存角色的菜单权限（全量覆盖）。
     *
     * @param body 含 roleId（角色ID，必填）、menuIds（菜单ID列表，空列表表示清空权限）
     * @return 统一响应；data 为空，code=200 表示授权成功
     */
    @ApiOperation("保存角色菜单权限")
    @PostMapping("/auth")
    public R<?> auth(@RequestBody RoleAuthRequest body) {
        return roleService.saveRoleMenus(body.getRoleId(), body.getMenuIds()) > 0 ? R.ok() : R.fail();
    }

    /**
     * 查询角色已勾选的菜单ID列表。
     *
     * @param id 角色ID
     * @return 统一响应；data 为 Long 类型的菜单ID列表
     */
    @ApiOperation("角色已选菜单")
    @GetMapping("/{id}/menus")
    public R<List<Long>> menus(@ApiParam(value = "角色ID", required = true) @PathVariable Long id) {
        return R.ok(roleService.selectMenuIdsByRoleId(id));
    }

    /**
     * 调整角色显示排序（上移或下移）。
     *
     * @param id        角色ID
     * @param direction 移动方向：up 上移，down 下移
     * @return 统一响应；data 为空，code=200 表示排序成功；已到边界时返回失败提示
     */
    @ApiOperation("角色排序上移或下移")
    @PostMapping("/move/{id}/{direction}")
    public R<?> move(@ApiParam(value = "角色ID", required = true) @PathVariable Long id,
                     @ApiParam(value = "up或down", required = true) @PathVariable String direction) {
        return roleService.moveRole(id, direction) > 0 ? R.ok() : R.fail("已到边界，无法移动");
    }
}
