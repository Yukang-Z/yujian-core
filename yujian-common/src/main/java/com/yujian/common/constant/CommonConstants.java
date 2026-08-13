package com.yujian.common.constant;

/**
 * 系统通用常量
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
public final class CommonConstants {

    private CommonConstants() {
    }

    /**
     * 未删除
     */
    public static final int NOT_DELETE = 0;

    /**
     * 已删除
     */
    public static final int DELETED = 1;

    /**
     * 启用/正常状态
     */
    public static final int STATUS_ENABLE = 1;

    /**
     * 停用状态
     */
    public static final int STATUS_DISABLE = 0;

    /**
     * 默认初始密码（首次创建员工后建议强制修改）
     */
    public static final String DEFAULT_PASSWORD = "Yujian@123";

    /**
     * Redis Key 前缀
     */
    public static final String REDIS_KEY_PREFIX = "yujian:";
}
