package com.yujian.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yujian.admin.mapper.SysEmployeeMapper;
import com.yujian.admin.mapper.SysEmployeeRoleMapper;
import com.yujian.admin.service.ISysEmployeeService;
import com.yujian.common.constant.Constants;
import com.yujian.common.core.domain.PageResult;
import com.yujian.common.exception.BusinessException;
import com.yujian.common.system.domain.SysEmployee;
import com.yujian.common.system.domain.SysEmployeeRole;
import com.yujian.common.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysEmployeeServiceImpl extends ServiceImpl<SysEmployeeMapper, SysEmployee> implements ISysEmployeeService {

    @Autowired
    private SysEmployeeRoleMapper employeeRoleMapper;

    @Override
    public PageResult<SysEmployee> selectEmployeePage(String keyword, Long clinicId, Long deptId,
                                                      Integer employStatus, long pageNum, long pageSize) {
        Page<SysEmployee> page = new Page<SysEmployee>(pageNum, pageSize);
        return PageResult.of(baseMapper.selectEmployeePage(page, keyword, clinicId, deptId, employStatus));
    }

    @Override
    public SysEmployee selectEmployeeById(Long id) {
        SysEmployee employee = this.getById(id);
        if (employee != null) {
            employee.setRoleIds(employeeRoleMapper.selectRoleIdsByEmployeeId(id));
            employee.setPassword(null);
        }
        return employee;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertEmployee(SysEmployee employee) {
        if (!checkUsernameUnique(employee)) {
            throw new BusinessException("登录账号已存在");
        }
        if (!checkEmpNoUnique(employee)) {
            throw new BusinessException("工号已存在");
        }
        if (StringUtils.isBlank(employee.getPassword())) {
            employee.setPassword(Constants.DEFAULT_PASSWORD);
        }
        employee.setPassword(SecurityUtils.encryptPassword(employee.getPassword()));
        if (employee.getEmployStatus() == null) {
            employee.setEmployStatus(Constants.EMPLOY_STATUS_ON);
        }
        if (employee.getStatus() == null) {
            employee.setStatus(0);
        }
        if (employee.getMobileLink() == null) {
            employee.setMobileLink(0);
        }
        if (employee.getSortOrder() == null) {
            employee.setSortOrder(0);
        }
        boolean saved = this.save(employee);
        insertEmployeeRole(employee);
        return saved ? 1 : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateEmployee(SysEmployee employee) {
        if (!checkUsernameUnique(employee)) {
            throw new BusinessException("登录账号已存在");
        }
        if (!checkEmpNoUnique(employee)) {
            throw new BusinessException("工号已存在");
        }
        employee.setPassword(null);
        boolean updated = this.updateById(employee);
        employeeRoleMapper.deleteByEmployeeId(employee.getId());
        insertEmployeeRole(employee);
        return updated ? 1 : 0;
    }

    private void insertEmployeeRole(SysEmployee employee) {
        List<Long> roleIds = employee.getRoleIds();
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                SysEmployeeRole er = new SysEmployeeRole();
                er.setEmployeeId(employee.getId());
                er.setRoleId(roleId);
                employeeRoleMapper.insert(er);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteEmployeeById(Long id) {
        employeeRoleMapper.deleteByEmployeeId(id);
        return this.removeById(id) ? 1 : 0;
    }

    @Override
    public int resetPassword(Long id, String password) {
        SysEmployee employee = this.getById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }
        String pwd = StringUtils.isBlank(password) ? Constants.DEFAULT_PASSWORD : password;
        SysEmployee update = new SysEmployee();
        update.setId(id);
        update.setPassword(SecurityUtils.encryptPassword(pwd));
        return this.updateById(update) ? 1 : 0;
    }

    @Override
    public int updateSortOrder(Long id, String direction) {
        SysEmployee current = this.getById(id);
        if (current == null) {
            throw new BusinessException("员工不存在");
        }
        LambdaQueryWrapper<SysEmployee> wrapper = new LambdaQueryWrapper<SysEmployee>()
                .eq(SysEmployee::getClinicId, current.getClinicId());
        if ("up".equalsIgnoreCase(direction)) {
            wrapper.lt(SysEmployee::getSortOrder, current.getSortOrder())
                    .orderByDesc(SysEmployee::getSortOrder)
                    .last("LIMIT 1");
        } else {
            wrapper.gt(SysEmployee::getSortOrder, current.getSortOrder())
                    .orderByAsc(SysEmployee::getSortOrder)
                    .last("LIMIT 1");
        }
        SysEmployee target = this.getOne(wrapper);
        if (target == null) {
            return 0;
        }
        Integer temp = current.getSortOrder();
        current.setSortOrder(target.getSortOrder());
        target.setSortOrder(temp);
        this.updateById(current);
        this.updateById(target);
        return 1;
    }

    @Override
    public boolean checkUsernameUnique(SysEmployee employee) {
        Long id = employee.getId() == null ? -1L : employee.getId();
        SysEmployee info = this.getOne(new LambdaQueryWrapper<SysEmployee>()
                .eq(SysEmployee::getUsername, employee.getUsername())
                .last("LIMIT 1"));
        return info == null || info.getId().equals(id);
    }

    @Override
    public boolean checkEmpNoUnique(SysEmployee employee) {
        Long id = employee.getId() == null ? -1L : employee.getId();
        SysEmployee info = this.getOne(new LambdaQueryWrapper<SysEmployee>()
                .eq(SysEmployee::getEmpNo, employee.getEmpNo())
                .last("LIMIT 1"));
        return info == null || info.getId().equals(id);
    }
}
