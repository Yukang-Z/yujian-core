package com.yujian.common.exception;

/**
 * 业务异常（可指定业务错误码，默认 500）
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 业务错误码，写入统一响应 R.code */
    private final int code;

    /**
     * 使用默认错误码 500 构造业务异常
     *
     * @param message 用户可读提示
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    /**
     * 指定错误码构造业务异常
     *
     * @param code    错误码，如 401
     * @param message 用户可读提示
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 获取业务错误码
     *
     * @return 错误码
     */
    public int getCode() {
        return code;
    }
}
