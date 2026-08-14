package com.yujian.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 权限平台枚举（对应 t_menu.platform：web/mobile）
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
    WEB("web", "网页版"),

    /**
     * 移动版
     */
    MOBILE("mobile", "移动版");

    /**
     * 编码（与库字段一致）
     */
    private final String code;

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
    public static PlatformEnum fromCode(String code) {
        if (code == null || code.isEmpty()) {
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
