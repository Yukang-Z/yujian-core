package com.yujian.common.core.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一 API 响应结果封装
 *
 * @param <T> 业务数据类型
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 业务状态码，200 表示成功 */
    private int code;

    /** 提示信息 */
    private String msg;

    /** 业务数据 */
    private T data;

    /**
     * 构造成功响应（无数据）
     *
     * @param <T> 业务数据类型
     * @return 成功结果
     */
    public static <T> R<T> ok() {
        return restResult(null, 200, "操作成功");
    }

    /**
     * 构造成功响应（带数据）
     *
     * @param data 业务数据
     * @param <T>  业务数据类型
     * @return 成功结果
     */
    public static <T> R<T> ok(T data) {
        return restResult(data, 200, "操作成功");
    }

    /**
     * 构造成功响应（带数据与自定义提示）
     *
     * @param data 业务数据
     * @param msg  提示信息
     * @param <T>  业务数据类型
     * @return 成功结果
     */
    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, 200, msg);
    }

    /**
     * 构造失败响应（默认 500）
     *
     * @param <T> 业务数据类型
     * @return 失败结果
     */
    public static <T> R<T> fail() {
        return restResult(null, 500, "操作失败");
    }

    /**
     * 构造失败响应（自定义提示）
     *
     * @param msg 提示信息
     * @param <T> 业务数据类型
     * @return 失败结果
     */
    public static <T> R<T> fail(String msg) {
        return restResult(null, 500, msg);
    }

    /**
     * 构造失败响应（自定义状态码与提示）
     *
     * @param code 业务状态码
     * @param msg  提示信息
     * @param <T>  业务数据类型
     * @return 失败结果
     */
    public static <T> R<T> fail(int code, String msg) {
        return restResult(null, code, msg);
    }

    /**
     * 组装统一响应对象
     *
     * @param data 业务数据
     * @param code 状态码
     * @param msg  提示信息
     * @param <T>  业务数据类型
     * @return 响应结果
     */
    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setData(data);
        r.setMsg(msg);
        return r;
    }

    /**
     * 判断当前响应是否成功
     *
     * @return true 表示成功（code == 200）
     */
    public boolean isSuccess() {
        return this.code == 200;
    }
}
