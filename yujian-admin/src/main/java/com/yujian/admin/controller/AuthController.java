package com.yujian.admin.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.yujian.admin.dto.request.LoginRequest;
import com.yujian.admin.dto.request.SelectClinicRequest;
import com.yujian.admin.dto.response.AuthInfoVO;
import com.yujian.admin.dto.response.LoginVO;
import com.yujian.admin.dto.response.SelectClinicVO;
import com.yujian.admin.mapper.SysEmployeeClinicMapper;
import com.yujian.admin.mapper.SysEmployeeMapper;
import com.yujian.admin.service.ISysMenuService;
import com.yujian.admin.service.SysAuthQueryService;
import com.yujian.common.constant.Constants;
import com.yujian.common.core.domain.LoginUser;
import com.yujian.common.core.domain.R;
import com.yujian.common.exception.BusinessException;
import com.yujian.common.system.domain.SysClinic;
import com.yujian.common.system.domain.SysEmployee;
import com.yujian.common.system.domain.SysMenu;
import com.yujian.common.system.domain.SysRole;
import com.yujian.common.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import java.util.HashSet;
import java.util.List;

/**
 * 登录鉴权接口（基于 Sa-Token）
 * <p>
 * 登录成功后返回可进入诊所列表；需调用 {@code /auth/selectClinic} 选定诊所后，
 * 业务查询按当前诊所隔离。
 * </p>
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Api(tags = "登录鉴权")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private SysEmployeeMapper employeeMapper;

    @Autowired
    private SysEmployeeClinicMapper employeeClinicMapper;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysAuthQueryService authQueryService;

    /**
     * 账号密码登录
     * <p>
     * 返回 token、用户、可进入诊所列表；若仅 1 个诊所则自动选中。
     * </p>
     *
     * @param body 登录账号与密码
     * @return token、用户、可进入诊所、是否还需选诊所
     */
    @ApiOperation("账号密码登录")
    @PostMapping("/login")
    public R<LoginVO> login(@RequestBody LoginRequest body) {
        String username = body == null ? null : body.getUsername();
        String password = body == null ? null : body.getPassword();
        log.info("【登录】开始登录, username={}", username);
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new BusinessException("用户名或密码不能为空");
        }

        // 1. 校验账号密码与状态
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

        // 2. 加载关联诊所（一对多）
        List<SysClinic> clinics = employeeClinicMapper.selectClinicsByEmployeeId(employee.getId());
        if (clinics == null || clinics.isEmpty()) {
            log.warn("【登录】未关联任何诊所, employeeId={}", employee.getId());
            throw new BusinessException("账号未关联诊所，请联系管理员");
        }

        // 3. 组装 LoginUser（尚未选定诊所时 clinicId 为空，单诊所则自动选定）
        List<SysRole> roles = authQueryService.selectRolesByEmployeeId(employee.getId());
        List<String> perms = menuService.selectPermsByEmployeeId(employee.getId());
        LoginUser loginUser = buildLoginUser(employee, roles, perms, clinics);
        boolean needSelectClinic = clinics.size() > 1;
        if (clinics.size() == 1) {
            applyClinic(loginUser, clinics.get(0));
            needSelectClinic = false;
        }

        // 4. Sa-Token 登录并写入 Session
        StpUtil.login(employee.getId());
        loginUser.setToken(StpUtil.getTokenValue());
        StpUtil.getSession().set(Constants.LOGIN_USER_SESSION_KEY, loginUser);

        employee.setPassword(null);
        employee.setClinicIds(loginUser.getClinicIds());
        LoginVO result = new LoginVO();
        result.setToken(StpUtil.getTokenValue());
        result.setUser(employee);
        result.setClinics(clinics);
        result.setNeedSelectClinic(needSelectClinic);
        result.setCurrentClinicId(loginUser.getClinicId());
        log.info("【登录】登录成功, employeeId={}, clinicCount={}, currentClinicId={}",
                employee.getId(), clinics.size(), loginUser.getClinicId());
        return R.ok(result);
    }

    /**
     * 登录后选择进入的诊所（写入 Session）
     *
     * @param body 诊所ID
     * @return 当前诊所ID与名称
     */
    @ApiOperation("登录后选择进入的诊所")
    @PostMapping("/selectClinic")
    public R<SelectClinicVO> selectClinic(@RequestBody SelectClinicRequest body) {
        if (body == null || body.getClinicId() == null) {
            throw new BusinessException("诊所ID不能为空");
        }
        Long clinicId = body.getClinicId();
        long employeeId = StpUtil.getLoginIdAsLong();
        log.info("【登录】选择诊所, employeeId={}, clinicId={}", employeeId, clinicId);

        // 校验该员工是否关联目标诊所
        if (employeeClinicMapper.countByEmployeeAndClinic(employeeId, clinicId) <= 0) {
            throw new BusinessException("无权进入该诊所");
        }
        List<SysClinic> clinics = employeeClinicMapper.selectClinicsByEmployeeId(employeeId);
        SysClinic target = null;
        for (SysClinic clinic : clinics) {
            if (clinicId.equals(clinic.getId())) {
                target = clinic;
                break;
            }
        }
        if (target == null) {
            throw new BusinessException("诊所不存在或已停用");
        }

        // 更新 Session 中的当前诊所
        Object cached = StpUtil.getSession().get(Constants.LOGIN_USER_SESSION_KEY);
        if (!(cached instanceof LoginUser)) {
            throw new BusinessException("登录状态异常，请重新登录");
        }
        LoginUser loginUser = (LoginUser) cached;
        applyClinic(loginUser, target);
        StpUtil.getSession().set(Constants.LOGIN_USER_SESSION_KEY, loginUser);

        SelectClinicVO result = new SelectClinicVO();
        result.setClinicId(target.getId());
        result.setClinicName(target.getClinicName());
        log.info("【登录】选择诊所成功, employeeId={}, clinicId={}", employeeId, clinicId);
        return R.ok(result);
    }

    /**
     * 查询当前账号可进入的诊所列表
     *
     * @return 可进入诊所列表
     */
    @ApiOperation("查询当前账号可进入的诊所")
    @GetMapping("/clinics")
    public R<List<SysClinic>> clinics() {
        long employeeId = StpUtil.getLoginIdAsLong();
        return R.ok(employeeClinicMapper.selectClinicsByEmployeeId(employeeId));
    }

    /**
     * 获取当前登录用户信息（含角色、菜单、权限、当前诊所）
     *
     * @return 用户、角色、菜单、权限、诊所及当前诊所
     */
    @ApiOperation("当前登录用户信息")
    @GetMapping("/info")
    public R<AuthInfoVO> info() {
        long employeeId = StpUtil.getLoginIdAsLong();
        log.info("【登录】查询当前用户信息, employeeId={}", employeeId);
        return R.ok(buildInfoResult(employeeId));
    }

    /**
     * 退出登录，注销当前 Token
     *
     * @return 空成功结果
     */
    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public R<?> logout() {
        long employeeId = StpUtil.getLoginIdAsLong();
        log.info("【登录】退出登录, employeeId={}", employeeId);
        StpUtil.logout();
        return R.ok();
    }

    /**
     * 组装 LoginUser（诊所列表写入 clinicIds，当前诊所可为空）
     *
     * @param employee 员工
     * @param roles    角色列表
     * @param perms    权限标识列表
     * @param clinics  可进入诊所
     * @return 登录用户
     */
    private LoginUser buildLoginUser(SysEmployee employee, List<SysRole> roles,
                                     List<String> perms, List<SysClinic> clinics) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(employee.getId());
        loginUser.setUsername(employee.getUsername());
        loginUser.setName(employee.getName());
        loginUser.setPermissions(new HashSet<String>(perms == null ? new ArrayList<String>() : perms));
        List<String> roleKeys = new ArrayList<String>();
        if (roles != null) {
            for (SysRole role : roles) {
                roleKeys.add(role.getRoleKey());
            }
        }
        loginUser.setRoles(roleKeys);
        List<Long> clinicIds = new ArrayList<Long>();
        if (clinics != null) {
            for (SysClinic clinic : clinics) {
                clinicIds.add(clinic.getId());
            }
        }
        loginUser.setClinicIds(clinicIds);
        return loginUser;
    }

    /**
     * 将选定诊所写入 LoginUser
     *
     * @param loginUser 登录用户
     * @param clinic    诊所
     */
    private void applyClinic(LoginUser loginUser, SysClinic clinic) {
        loginUser.setClinicId(clinic.getId());
        loginUser.setClinicName(clinic.getClinicName());
    }

    /**
     * 构建 /auth/info 返回结构
     *
     * @param employeeId 员工ID
     * @return 用户信息
     */
    private AuthInfoVO buildInfoResult(long employeeId) {
        SysEmployee employee = employeeMapper.selectById(employeeId);
        if (employee == null) {
            throw new BusinessException("用户不存在");
        }
        employee.setPassword(null);
        List<Long> clinicIds = employeeClinicMapper.selectClinicIdsByEmployeeId(employeeId);
        employee.setClinicIds(clinicIds);
        List<SysClinic> clinics = employeeClinicMapper.selectClinicsByEmployeeId(employeeId);
        List<SysRole> roles = authQueryService.selectRolesByEmployeeId(employeeId);
        List<SysMenu> menus = menuService.selectMenusByEmployeeId(employeeId, Constants.PLATFORM_WEB);
        List<String> perms = menuService.selectPermsByEmployeeId(employeeId);

        Long currentClinicId = null;
        String currentClinicName = null;
        Object cached = StpUtil.getSession().get(Constants.LOGIN_USER_SESSION_KEY);
        if (cached instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) cached;
            currentClinicId = loginUser.getClinicId();
            currentClinicName = loginUser.getClinicName();
        }

        AuthInfoVO result = new AuthInfoVO();
        result.setUser(employee);
        result.setRoles(roles);
        result.setMenus(menus);
        result.setPermissions(perms);
        result.setClinics(clinics);
        result.setCurrentClinicId(currentClinicId);
        result.setCurrentClinicName(currentClinicName);
        return result;
    }
}
