package com.yujian.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性别枚举（对应 t_employee.gender / t_patient.gender：0女 1男 2未知）
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Getter
@AllArgsConstructor
public enum GenderEnum {

    /**
     * 女
     */
    FEMALE(0, "女"),

    /**
     * 男
     */
    MALE(1, "男"),

    /**
     * 未知
     */
    UNKNOWN(2, "未知");

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
