package com.yujian.common.core.context;

import com.yujian.common.core.domain.LoginUser;

/**
 * 当前登录用户 ThreadLocal
 */
public final class SecurityContextHolder {

    private static final ThreadLocal<LoginUser> CONTEXT = new ThreadLocal<LoginUser>();

    private SecurityContextHolder() {
    }

    public static void setLoginUser(LoginUser loginUser) {
        CONTEXT.set(loginUser);
    }

    public static LoginUser getLoginUser() {
        return CONTEXT.get();
    }

    public static Long getUserId() {
        LoginUser user = CONTEXT.get();
        return user == null ? null : user.getUserId();
    }

    public static Long getClinicId() {
        LoginUser user = CONTEXT.get();
        return user == null ? null : user.getClinicId();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
