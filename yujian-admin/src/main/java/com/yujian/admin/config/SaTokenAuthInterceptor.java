package com.yujian.admin.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.yujian.common.constant.Constants;
import com.yujian.common.core.context.SecurityContextHolder;
import com.yujian.common.core.domain.LoginUser;
import com.yujian.common.core.domain.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Sa-Token 登录鉴权拦截器
 * <p>
 * 校验 Token，将 Session 中的 {@link LoginUser} 写入 ThreadLocal；
 * 未登录时直接返回 401 JSON（避免拦截器异常无法被 ControllerAdvice 捕获）。
 * </p>
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Component
public class SaTokenAuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(SaTokenAuthInterceptor.class);

    /**
     * 请求前置：校验登录并填充上下文
     *
     * @param request  请求
     * @param response 响应
     * @param handler  处理器
     * @return true=放行
     * @throws Exception 写出响应失败时抛出
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        try {
            // 校验 Token（无效/过期抛 NotLoginException）
            StpUtil.checkLogin();
            Object cached = StpUtil.getSession().get(Constants.LOGIN_USER_SESSION_KEY);
            if (cached instanceof LoginUser) {
                LoginUser loginUser = (LoginUser) cached;
                loginUser.setToken(StpUtil.getTokenValue());
                SecurityContextHolder.setLoginUser(loginUser);
                // 患者/预约/员工等业务接口必须先选定诊所
                if (needSelectedClinic(request) && loginUser.getClinicId() == null) {
                    log.info("【鉴权】未选择诊所, path={}", request.getRequestURI());
                    writeJson(response, 400, "请先选择诊所后再操作");
                    return false;
                }
            } else {
                log.warn("【鉴权】已登录但 Session 缺少 LoginUser, loginId={}", StpUtil.getLoginIdDefaultNull());
            }
            return true;
        } catch (NotLoginException e) {
            log.info("【鉴权】未登录或 Token 失效, path={}, type={}", request.getRequestURI(), e.getType());
            writeJson(response, 401, "未登录或登录已过期，请重新登录");
            return false;
        }
    }

    /**
     * 患者、预约、员工、部门等业务查询必须带当前诊所
     *
     * @param request 请求
     * @return true=需要已选诊所
     */
    private boolean needSelectedClinic(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri == null) {
            return false;
        }
        String context = request.getContextPath();
        if (context != null && !context.isEmpty() && uri.startsWith(context)) {
            uri = uri.substring(context.length());
        }
        return uri.startsWith("/biz/")
                || uri.startsWith("/system/employee")
                || uri.startsWith("/system/dept");
    }

    /**
     * 请求结束：清理 ThreadLocal
     *
     * @param request  请求
     * @param response 响应
     * @param handler  处理器
     * @param ex       异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        SecurityContextHolder.clear();
    }

    /**
     * 输出鉴权/诊所校验失败 JSON
     *
     * @param response 响应
     * @param code     业务码
     * @param msg      提示信息
     * @throws IOException IO 异常
     */
    private void writeJson(HttpServletResponse response, int code, String msg) throws IOException {
        response.setStatus(code == 401 ? 401 : 200);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(R.fail(code, msg)));
    }
}
