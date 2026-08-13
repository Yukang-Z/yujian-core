package com.yujian.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 在职状态枚举（对应 sys_staff.work_status）
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Getter
@AllArgsConstructor
public enum WorkStatusEnum {

    /**
     * 在职
     */
    ON_JOB(1, "在职"),

    /**
     * 离职
     */
    LEFT(0, "离职");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String desc;

    /**
     * 按编码解析枚举
     *
     * @param code 状态码
     * @return 枚举，未匹配返回 null
     */
    public static WorkStatusEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (WorkStatusEnum item : values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
