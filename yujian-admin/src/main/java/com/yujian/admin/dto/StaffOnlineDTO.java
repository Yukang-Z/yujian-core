package com.yujian.admin.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 员工上下线状态变更请求参数
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Data
public class StaffOnlineDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 员工ID，关联 sys_staff.id
     */
    private Long staffId;

    /**
     * 账号上线状态：1上线 0下线
     */
    private Integer onlineStatus;
}
