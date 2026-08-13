package com.yujian.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yujian.admin.mapper.SysEmployeeMapper;
import com.yujian.admin.service.ISysMenuService;
import com.yujian.admin.service.SysAuthQueryService;
import com.yujian.common.constant.Constants;
import com.yujian.common.core.context.SecurityContextHolder;
import com.yujian.common.core.domain.LoginUser;
import com.yujian.common.core.domain.R;
import com.yujian.common.exception.BusinessException;
import com.yujian.common.system.domain.SysEmployee;
import com.yujian.common.system.domain.SysMenu;
import com.yujian.common.system.domain.SysRole;
import com.yujian.common.utils.JwtUtils;
import com.yujian.common.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录鉴权
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SysEmployeeMapper employeeMapper;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysAuthQueryService authQueryService;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new BusinessException("用户名或密码不能为空");
        }
        SysEmployee employee = employeeMapper.selectOne(new LambdaQueryWrapper<SysEmployee>()
                .eq(SysEmployee::getUsername, username)
                .last("LIMIT 1"));
        if (employee == null) {
            throw new BusinessException("用户不存在");
        }
        if (employee.getStatus() != null && employee.getStatus() == 1) {
            throw new BusinessException("账号已停用");
        }
        if (employee.getEmployStatus() != null && employee.getEmployStatus() == 0) {
            throw new BusinessException("员工已离职");
        }
        if (!SecurityUtils.matchesPassword(password, employee.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        String token = JwtUtils.createToken(employee.getId(), employee.getUsername());
        List<SysRole> roles = authQueryService.selectRolesByEmployeeId(employee.getId());
        List<String> perms = menuService.selectPermsByEmployeeId(employee.getId());

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(employee.getId());
        loginUser.setUsername(employee.getUsername());
        loginUser.setName(employee.getName());
        loginUser.setClinicId(employee.getClinicId());
        loginUser.setDeptId(employee.getDeptId());
        loginUser.setToken(token);
        loginUser.setPermissions(new HashSet<String>(perms == null ? new ArrayList<String>() : perms));
        List<String> roleKeys = new ArrayList<String>();
        if (roles != null) {
            for (SysRole role : roles) {
                roleKeys.add(role.getRoleKey());
            }
        }
        loginUser.setRoles(roleKeys);

        if (redisTemplate != null) {
            redisTemplate.opsForValue().set(Constants.REDIS_TOKEN_KEY + employee.getId(), token,
                    Constants.TOKEN_EXPIRE, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(Constants.REDIS_LOGIN_USER_KEY + employee.getId(), loginUser,
                    Constants.TOKEN_EXPIRE, TimeUnit.SECONDS);
        }

        Map<String, Object> result = new HashMap<String, Object>(8);
        result.put("token", token);
        employee.setPassword(null);
        result.put("user", employee);
        return R.ok(result);
    }

    /** 当前登录用户信息 */
    @GetMapping("/info")
    public R<Map<String, Object>> info() {
        Long employeeId = SecurityContextHolder.getUserId();
        if (employeeId == null) {
            throw new BusinessException(401, "未登录");
        }
        return infoById(employeeId);
    }

    @GetMapping("/info/{employeeId}")
    public R<Map<String, Object>> infoById(@PathVariable Long employeeId) {
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
        return R.ok(result);
    }

    @PostMapping("/logout")
    public R<?> logout() {
        Long employeeId = SecurityContextHolder.getUserId();
        if (employeeId != null && redisTemplate != null) {
            redisTemplate.delete(Constants.REDIS_TOKEN_KEY + employeeId);
            redisTemplate.delete(Constants.REDIS_LOGIN_USER_KEY + employeeId);
        }
        return R.ok();
    }
}
