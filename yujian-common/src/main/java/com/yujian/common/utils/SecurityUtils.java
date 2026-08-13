package com.yujian.common.utils;

import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.crypto.digest.DigestUtil;

/**
 * 安全工具
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static String encryptPassword(String password) {
        return BCrypt.hashpw(password);
    }

    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }

    public static String md5(String text) {
        return DigestUtil.md5Hex(text);
    }
}
