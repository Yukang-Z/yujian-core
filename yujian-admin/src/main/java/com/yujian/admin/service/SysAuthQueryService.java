package com.yujian.admin.service;

import com.yujian.admin.mapper.SysRoleMapper;
import com.yujian.common.system.domain.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 登录鉴权辅助查询（角色等）
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Service
public class SysAuthQueryService {

    @Autowired
    private SysRoleMapper roleMapper;

    /**
     * 按员工ID查询其绑定角色列表
     *
     * @param employeeId 员工ID
     * @return 角色列表
     */
    public List<SysRole> selectRolesByEmployeeId(Long employeeId) {
        return roleMapper.selectRolesByEmployeeId(employeeId);
    }
}
