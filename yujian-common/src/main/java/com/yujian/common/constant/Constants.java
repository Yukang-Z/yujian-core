package com.yujian.common.constant;

/**
 * 系统常量（状态、菜单类型、平台、Sa-Token Session Key 等）
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
public final class Constants {

    private Constants() {
    }

    /** 成功标记 */
    public static final int SUCCESS = 200;

    /** 失败标记 */
    public static final int FAIL = 500;

    /** 正常状态 */
    public static final Integer STATUS_NORMAL = 0;

    /** 停用状态 */
    public static final Integer STATUS_DISABLE = 1;

    /** 删除标识：否 */
    public static final Integer IS_DELETE_NO = 0;

    /** 删除标识：是 */
    public static final Integer IS_DELETE_YES = 1;

    /** 在职 */
    public static final Integer EMPLOY_STATUS_ON = 1;

    /** 离职 */
    public static final Integer EMPLOY_STATUS_OFF = 0;

    /** 菜单类型：目录 */
    public static final String MENU_TYPE_DIR = "M";

    /** 菜单类型：菜单 */
    public static final String MENU_TYPE_MENU = "C";

    /** 菜单类型：按钮 */
    public static final String MENU_TYPE_BUTTON = "F";

    /** 平台：Web */
    public static final String PLATFORM_WEB = "web";

    /** 平台：移动端 */
    public static final String PLATFORM_MOBILE = "mobile";

    /** 默认密码（新增员工未指定密码时使用） */
    public static final String DEFAULT_PASSWORD = "123456";

    /**
     * Sa-Token Session 中存放 {@link com.yujian.common.core.domain.LoginUser} 的 Key
     */
    public static final String LOGIN_USER_SESSION_KEY = "loginUser";

    /** Token 有效期（秒），与 sa-token.timeout 保持一致 */
    public static final long TOKEN_EXPIRE = 7200L;
}
