package com.yujian.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性别枚举（对应 sys_staff.gender）
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Getter
@AllArgsConstructor
public enum GenderEnum {

    /**
     * 未知
     */
    UNKNOWN(0, "未知"),

    /**
     * 男
     */
    MALE(1, "男"),

    /**
     * 女
     */
    FEMALE(2, "女");

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
    public static GenderEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (GenderEnum item : values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
