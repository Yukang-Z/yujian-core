package com.yujian.admin.config;

import cn.dev33.satoken.stp.StpInterface;
import com.yujian.common.constant.Constants;
import com.yujian.common.core.domain.LoginUser;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 权限与角色数据源（从登录 Session 中的 LoginUser 读取）
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    /**
     * 返回当前账号拥有的权限码集合
     *
     * @param loginId   登录ID（员工ID）
     * @param loginType 账号体系
     * @return 权限标识列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        LoginUser user = resolveLoginUser(loginId);
        if (user == null || user.getPermissions() == null) {
            return Collections.emptyList();
        }
        return new ArrayList<String>(user.getPermissions());
    }

    /**
     * 返回当前账号拥有的角色标识集合
     *
     * @param loginId   登录ID（员工ID）
     * @param loginType 账号体系
     * @return 角色编码列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        LoginUser user = resolveLoginUser(loginId);
        if (user == null || user.getRoles() == null) {
            return Collections.emptyList();
        }
        return new ArrayList<String>(user.getRoles());
    }

    /**
     * 从指定 loginId 的 Session 取出 LoginUser
     *
     * @param loginId 登录ID
     * @return 登录用户，不存在时为 null
     */
    private LoginUser resolveLoginUser(Object loginId) {
        Object cached = StpUtil.getSessionByLoginId(loginId).get(Constants.LOGIN_USER_SESSION_KEY);
        return cached instanceof LoginUser ? (LoginUser) cached : null;
    }
}
