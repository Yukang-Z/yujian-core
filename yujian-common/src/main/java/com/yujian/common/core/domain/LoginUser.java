package com.yujian.common.core.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 登录用户上下文
 */
@Data
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String name;
    private Long clinicId;
    private Long deptId;
    private String token;
    private Set<String> permissions;
    private List<String> roles;
}
