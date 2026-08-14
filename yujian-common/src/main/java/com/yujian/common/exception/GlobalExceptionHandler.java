package com.yujian.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.yujian.common.core.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理（业务异常、参数校验、Sa-Token 鉴权异常）
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * @param e 业务异常
     * @return 失败响应
     */
    @ExceptionHandler(BusinessException.class)
    public R<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理未登录异常（Controller 内主动校验时触发）
     *
     * @param e 未登录异常
     * @return 401 响应
     */
    @ExceptionHandler(NotLoginException.class)
    public R<?> handleNotLoginException(NotLoginException e) {
        log.info("【鉴权】未登录: {}", e.getMessage());
        return R.fail(401, "未登录或登录已过期，请重新登录");
    }

    /**
     * 处理无权限异常
     *
     * @param e 无权限异常
     * @return 403 响应
     */
    @ExceptionHandler(NotPermissionException.class)
    public R<?> handleNotPermissionException(NotPermissionException e) {
        log.warn("【鉴权】无权限: {}", e.getPermission());
        return R.fail(403, "没有访问权限");
    }

    /**
     * 处理无角色异常
     *
     * @param e 无角色异常
     * @return 403 响应
     */
    @ExceptionHandler(NotRoleException.class)
    public R<?> handleNotRoleException(NotRoleException e) {
        log.warn("【鉴权】无角色: {}", e.getRole());
        return R.fail(403, "没有访问权限");
    }

    /**
     * 处理参数校验异常
     *
     * @param e 校验异常
     * @return 400 响应
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public R<?> handleValidException(Exception e) {
        String message = "参数校验失败";
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            if (ex.getBindingResult().getFieldError() != null) {
                message = ex.getBindingResult().getFieldError().getDefaultMessage();
            }
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            if (ex.getBindingResult().getFieldError() != null) {
                message = ex.getBindingResult().getFieldError().getDefaultMessage();
            }
        }
        return R.fail(400, message);
    }

    /**
     * 处理未捕获系统异常
     *
     * @param e 异常
     * @return 500 响应
     */
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e) {
        log.error("系统异常", e);
        return R.fail("系统异常，请联系管理员");
    }
}
