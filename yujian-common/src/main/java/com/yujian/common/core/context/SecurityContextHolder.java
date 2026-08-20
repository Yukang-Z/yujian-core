package com.yujian.common.core.context;

import com.yujian.common.core.domain.LoginUser;
import com.yujian.common.exception.BusinessException;

import java.util.List;

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
     * 获取当前已选择的诊所ID（可能未选诊所，返回 null）
     *
     * @return 诊所ID，未登录或未选诊所时为 null
     */
    public static Long getClinicId() {
        LoginUser user = CONTEXT.get();
        return user == null ? null : user.getClinicId();
    }

    /**
     * 写操作诊所：强制使用登录后选定的当前诊所（忽略请求入参，避免误写到未切换诊所）
     *
     * @param requestClinicId 请求传入的诊所ID（可空，写路径忽略）
     * @return 当前会话诊所ID
     */
    public static Long requireClinicId(Long requestClinicId) {
        Long clinicId = getClinicId();
        if (clinicId == null) {
            throw new BusinessException("请先选择诊所后再操作");
        }
        return clinicId;
    }

    /**
     * 要求已选择诊所（无入参场景）
     *
     * @return 当前诊所ID
     */
    public static Long requireClinicId() {
        return requireClinicId(null);
    }

    /**
     * 查询诊所：请求 clinicId 在授权范围内则生效，空则回退会话当前诊所
     * <p>
     * 用于医生列表、诊疗项目、咨询师列表、预约天视图、状态计数、**新建预约**等
     * 「按所选授权诊所」的接口；禁止越权访问未授权诊所数据。
     * </p>
     *
     * @param requestClinicId 请求诊所ID，可空
     * @return 最终查询诊所ID
     */
    public static Long resolveAuthorizedClinicId(Long requestClinicId) {
        LoginUser user = getLoginUser();
        if (user == null) {
            throw new BusinessException("请先登录后再操作");
        }
        Long current = user.getClinicId();
        if (current == null) {
            throw new BusinessException("请先选择诊所后再操作");
        }
        if (requestClinicId == null) {
            return current;
        }
        if (requestClinicId.equals(current)) {
            return requestClinicId;
        }
        // 校验是否在账号可进入诊所列表中
        List<Long> authorized = user.getClinicIds();
        if (authorized == null || !authorized.contains(requestClinicId)) {
            throw new BusinessException(403, "无权访问该诊所");
        }
        return requestClinicId;
    }

    /**
     * 清理 ThreadLocal，防止线程复用导致用户串号
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
