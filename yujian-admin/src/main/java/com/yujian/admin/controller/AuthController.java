package com.yujian.admin.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.yujian.admin.mapper.SysEmployeeMapper;
import com.yujian.admin.service.ISysMenuService;
import com.yujian.admin.service.SysAuthQueryService;
import com.yujian.common.constant.Constants;
import com.yujian.common.core.domain.LoginUser;
import com.yujian.common.core.domain.R;
import com.yujian.common.exception.BusinessException;
import com.yujian.common.system.domain.SysEmployee;
import com.yujian.common.system.domain.SysMenu;
import com.yujian.common.system.domain.SysRole;
import com.yujian.common.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 登录鉴权接口（基于 Sa-Token）
 * <p>
 * 提供登录、当前用户信息、退出；Token 由 Sa-Token 签发并持久化到 Redis。
 * </p>
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private SysEmployeeMapper employeeMapper;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysAuthQueryService authQueryService;

    /**
     * 账号密码登录
     *
     * @param body 请求体，需含 username、password
     * @return token 与用户基础信息
     */
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        log.info("【登录】开始登录, username={}", username);
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new BusinessException("用户名或密码不能为空");
        }

        // 1. 按账号查询员工
        SysEmployee employee = employeeMapper.selectByUsername(username);
        if (employee == null) {
            log.warn("【登录】用户不存在, username={}", username);
            throw new BusinessException("用户不存在");
        }
        if (employee.getStatus() != null && employee.getStatus() == 1) {
            throw new BusinessException("账号已停用");
        }
        if (employee.getEmployStatus() != null && employee.getEmployStatus() == 0) {
            throw new BusinessException("员工已离职");
        }
        if (!SecurityUtils.matchesPassword(password, employee.getPassword())) {
            log.warn("【登录】密码错误, username={}, employeeId={}", username, employee.getId());
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 加载角色与权限，组装登录上下文
        List<SysRole> roles = authQueryService.selectRolesByEmployeeId(employee.getId());
        List<String> perms = menuService.selectPermsByEmployeeId(employee.getId());
        LoginUser loginUser = buildLoginUser(employee, roles, perms);

        // 3. Sa-Token 登录：同账号互踢，Session 存 LoginUser
        StpUtil.login(employee.getId());
        loginUser.setToken(StpUtil.getTokenValue());
        StpUtil.getSession().set(Constants.LOGIN_USER_SESSION_KEY, loginUser);

        employee.setPassword(null);
        Map<String, Object> result = new HashMap<String, Object>(8);
        result.put("token", StpUtil.getTokenValue());
        result.put("user", employee);
        log.info("【登录】登录成功, employeeId={}, clinicId={}", employee.getId(), employee.getClinicId());
        return R.ok(result);
    }

    /**
     * 获取当前登录用户信息（含角色、菜单、权限）
     *
     * @return user / roles / menus / permissions
     */
    @GetMapping("/info")
    public R<Map<String, Object>> info() {
        long employeeId = StpUtil.getLoginIdAsLong();
        log.info("【登录】查询当前用户信息, employeeId={}", employeeId);
        return R.ok(buildInfoResult(employeeId));
    }

    /**
     * 退出登录，注销当前 Token
     *
     * @return 空成功结果
     */
    @PostMapping("/logout")
    public R<?> logout() {
        long employeeId = StpUtil.getLoginIdAsLong();
        log.info("【登录】退出登录, employeeId={}", employeeId);
        StpUtil.logout();
        return R.ok();
    }

    /**
     * 组装 LoginUser
     *
     * @param employee 员工
     * @param roles    角色列表
     * @param perms    权限标识列表
     * @return 登录用户
     */
    private LoginUser buildLoginUser(SysEmployee employee, List<SysRole> roles, List<String> perms) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(employee.getId());
        loginUser.setUsername(employee.getUsername());
        loginUser.setName(employee.getName());
        loginUser.setClinicId(employee.getClinicId());
        loginUser.setDeptId(employee.getDeptId());
        loginUser.setPermissions(new HashSet<String>(perms == null ? new ArrayList<String>() : perms));
        List<String> roleKeys = new ArrayList<String>();
        if (roles != null) {
            for (SysRole role : roles) {
                roleKeys.add(role.getRoleKey());
            }
        }
        loginUser.setRoles(roleKeys);
        return loginUser;
    }

    /**
     * 构建 /auth/info 返回结构
     *
     * @param employeeId 员工ID
     * @return 用户信息 Map
     */
    private Map<String, Object> buildInfoResult(long employeeId) {
        SysEmployee employee = employeeMapper.selectById(employeeId);
        if (employee == null) {
            throw new BusinessException("用户不存在");
        }
        employee.setPassword(null);
        List<SysRole> roles = authQueryService.selectRolesByEmployeeId(employeeId);
        List<SysMenu> menus = menuService.selectMenusByEmployeeId(employeeId, Constants.PLATFORM_WEB);
        List<String> perms = menuService.selectPermsByEmployeeId(employeeId);

        Map<String, Object> result = new HashMap<String, Object>(8);
        result.put("user", employee);
        result.put("roles", roles);
        result.put("menus", menus);
        result.put("permissions", perms);
        return result;
    }
}
