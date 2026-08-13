package com.yujian.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 权限平台枚举（对应 sys_permission.platform）
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Getter
@AllArgsConstructor
public enum PlatformEnum {

    /**
     * 网页版
     */
    WEB(1, "网页版"),

    /**
     * 移动版
     */
    MOBILE(2, "移动版"),

    /**
     * 数据权限
     */
    DATA(3, "数据权限");

    /**
     * 编码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 按编码解析枚举
     *
     * @param code 编码
     * @return 枚举，未匹配返回 null
     */
    public static PlatformEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PlatformEnum item : values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
