package com.yujian.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.yujian.common.core.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理（业务异常、参数校验、请求体解析、Sa-Token 鉴权异常）
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
     * 处理请求体 JSON 无法解析（格式错误、多余转义等）
     *
     * @param e 消息不可读异常
     * @return 400 响应
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        Throwable cause = e.getMostSpecificCause();
        String detail = cause == null ? e.getMessage() : cause.getMessage();
        log.warn("【请求体】JSON 解析失败: {}", detail);
        if (cause instanceof JsonParseException) {
            return R.fail(400, "请求体 JSON 格式错误，请使用标准 JSON（字段名和字符串用双引号，不要加反斜杠转义）");
        }
        if (cause instanceof JsonMappingException) {
            return R.fail(400, "请求体字段类型或结构不正确");
        }
        return R.fail(400, "请求体无法解析，请检查 Content-Type 是否为 application/json 且 Body 为合法 JSON");
    }

    /**
     * 处理缺少必填请求参数
     *
     * @param e 缺少参数异常
     * @return 400 响应
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("【请求参数】缺少必填参数: {}", e.getParameterName());
        return R.fail(400, "缺少必填参数: " + e.getParameterName());
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
