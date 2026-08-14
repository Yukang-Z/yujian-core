package com.yujian.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 菜单类型枚举（对应 t_menu.menu_type：M目录/C菜单/F按钮）
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Getter
@AllArgsConstructor
public enum PermissionTypeEnum {

    /**
     * 目录
     */
    DIRECTORY("M", "目录"),

    /**
     * 菜单
     */
    MENU("C", "菜单"),

    /**
     * 按钮
     */
    BUTTON("F", "按钮");

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
    public static PermissionTypeEnum fromCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        for (PermissionTypeEnum item : values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
