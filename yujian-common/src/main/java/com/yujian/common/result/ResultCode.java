package com.yujian.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一业务状态码枚举
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(0, "操作成功"),

    /**
     * 失败
     */
    FAIL(1, "操作失败"),

    /**
     * 参数校验失败
     */
    PARAM_ERROR(400, "参数校验失败"),

    /**
     * 未登录或登录失效
     */
    UNAUTHORIZED(401, "未登录或登录已失效"),

    /**
     * 无权限
     */
    FORBIDDEN(403, "无访问权限"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 系统异常
     */
    SYSTEM_ERROR(500, "系统异常，请稍后重试");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String message;
}
