package com.yujian.common.util;

import cn.hutool.core.util.StrUtil;

/**
 * 字符串工具扩展
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
public final class StringUtils {

    private StringUtils() {
    }

    /**
     * 判断字符串是否为空（null/空白）
     *
     * @param str 字符串
     * @return true为空
     */
    public static boolean isBlank(String str) {
        return StrUtil.isBlank(str);
    }

    /**
     * 判断字符串是否非空
     *
     * @param str 字符串
     * @return true非空
     */
    public static boolean isNotBlank(String str) {
        return StrUtil.isNotBlank(str);
    }
}
