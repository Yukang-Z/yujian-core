package com.yujian.common.constant;

/**
 * 系统常量
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

    /** 删除标志：正常 */
    public static final Integer DEL_FLAG_NORMAL = 0;

    /** 删除标志：已删除 */
    public static final Integer DEL_FLAG_DELETED = 1;

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

    /** 默认密码 */
    public static final String DEFAULT_PASSWORD = "123456";

    /** Redis Token 前缀 */
    public static final String REDIS_TOKEN_KEY = "yujian:token:";

    /** Redis 登录用户缓存前缀 */
    public static final String REDIS_LOGIN_USER_KEY = "yujian:login:user:";

    /** Token 有效期（秒） */
    public static final long TOKEN_EXPIRE = 7200L;
}
