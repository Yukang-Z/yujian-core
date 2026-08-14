package com.yujian.common.utils;

import cn.hutool.crypto.digest.BCrypt;

/**
 * 密码安全工具（BCrypt 加密与校验）
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * 对明文密码做 BCrypt 哈希
     *
     * @param password 明文密码
     * @return 密文
     */
    public static String encryptPassword(String password) {
        return BCrypt.hashpw(password);
    }

    /**
     * 校验明文密码是否与密文匹配
     *
     * @param rawPassword     明文密码
     * @param encodedPassword 库中密文
     * @return true=匹配
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
