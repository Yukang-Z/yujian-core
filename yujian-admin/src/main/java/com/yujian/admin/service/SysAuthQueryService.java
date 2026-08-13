package com.yujian.admin.service;

import com.yujian.admin.mapper.SysRoleMapper;
import com.yujian.common.system.domain.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysAuthQueryService {

    @Autowired
    private SysRoleMapper roleMapper;

    public List<SysRole> selectRolesByEmployeeId(Long employeeId) {
        return roleMapper.selectRolesByEmployeeId(employeeId);
    }
}
