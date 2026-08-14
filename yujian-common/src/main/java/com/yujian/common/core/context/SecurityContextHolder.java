package com.yujian.common.core.context;

import com.yujian.common.core.domain.LoginUser;

/**
 * 当前登录用户 ThreadLocal 上下文
 * <p>
 * 由 Sa-Token 鉴权拦截器在请求进入时写入，请求结束时清理，
 * 业务层通过本类获取 userId / clinicId，避免直接依赖 Sa-Token API。
 * </p>
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
public final class SecurityContextHolder {

    private static final ThreadLocal<LoginUser> CONTEXT = new ThreadLocal<LoginUser>();

    private SecurityContextHolder() {
    }

    /**
     * 写入当前登录用户
     *
     * @param loginUser 登录用户
     */
    public static void setLoginUser(LoginUser loginUser) {
        CONTEXT.set(loginUser);
    }

    /**
     * 获取当前登录用户
     *
     * @return 登录用户，未登录时为 null
     */
    public static LoginUser getLoginUser() {
        return CONTEXT.get();
    }

    /**
     * 获取当前员工ID
     *
     * @return 员工ID，未登录时为 null
     */
    public static Long getUserId() {
        LoginUser user = CONTEXT.get();
        return user == null ? null : user.getUserId();
    }

    /**
     * 获取当前诊所ID
     *
     * @return 诊所ID，未登录时为 null
     */
    public static Long getClinicId() {
        LoginUser user = CONTEXT.get();
        return user == null ? null : user.getClinicId();
    }

    /**
     * 清理 ThreadLocal，防止线程复用导致用户串号
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
