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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工管理服务实现
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Service
public class SysEmployeeServiceImpl extends ServiceImpl<SysEmployeeMapper, SysEmployee> implements ISysEmployeeService {

    private static final Logger log = LoggerFactory.getLogger(SysEmployeeServiceImpl.class);

    /** 账号状态：正常 */
    private static final int STATUS_NORMAL = 0;

    /** 账号状态：停用 */
    private static final int STATUS_DISABLE = 1;

    @Autowired
    private SysEmployeeRoleMapper employeeRoleMapper;

    /**
     * 员工分页列表，并批量回填 roleIds
     *
     * @param keyword      关键字
     * @param clinicId     诊所ID
     * @param deptId       部门ID
     * @param employStatus 在职状态
     * @param pageNum      页码
     * @param pageSize     每页条数
     * @return 分页结果
     */
    @Override
    public PageResult<SysEmployee> selectEmployeePage(String keyword, Long clinicId, Long deptId,
                                                      Integer employStatus, long pageNum, long pageSize) {
        Page<SysEmployee> page = new Page<SysEmployee>(pageNum, pageSize);
        PageResult<SysEmployee> result = PageResult.of(
                baseMapper.selectEmployeePage(page, keyword, clinicId, deptId, employStatus));
        // 列表一并带回 roleIds，避免前端停用/编辑前再打详情
        fillRoleIds(result.getRecords());
        return result;
    }

    /**
     * 员工详情（脱敏密码，加载角色）
     *
     * @param id 员工ID
     * @return 员工信息
     */
    @Override
    public SysEmployee selectEmployeeById(Long id) {
        SysEmployee employee = this.getById(id);
        if (employee != null) {
            employee.setRoleIds(employeeRoleMapper.selectRoleIdsByEmployeeId(id));
            employee.setPassword(null);
        }
        return employee;
    }

    /**
     * 新增员工：加密密码后落库，再写角色关联
     *
     * @param employee 员工
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertEmployee(SysEmployee employee) {
        log.info("【员工】新增开始, username={}, empNo={}", employee.getUsername(), employee.getEmpNo());
        if (!checkUsernameUnique(employee)) {
            throw new BusinessException("登录账号已存在");
        }
        if (!checkEmpNoUnique(employee)) {
            throw new BusinessException("工号已存在");
        }
        if (StringUtils.isBlank(employee.getPassword())) {
            employee.setPassword(Constants.DEFAULT_PASSWORD);
        }
        // 明文密码仅在此加密入库，后续接口不回传
        employee.setPassword(SecurityUtils.encryptPassword(employee.getPassword()));
        if (employee.getEmployStatus() == null) {
            employee.setEmployStatus(Constants.EMPLOY_STATUS_ON);
        }
        if (employee.getStatus() == null) {
            employee.setStatus(STATUS_NORMAL);
        }
        if (employee.getMobileLink() == null) {
            employee.setMobileLink(0);
        }
        if (employee.getSortOrder() == null) {
            employee.setSortOrder(0);
        }
        boolean saved = this.save(employee);
        insertEmployeeRole(employee);
        log.info("【员工】新增完成, employeeId={}, roleCount={}",
                employee.getId(), employee.getRoleIds() == null ? 0 : employee.getRoleIds().size());
        return saved ? 1 : 0;
    }

    /**
     * 修改员工：密码字段强制忽略；roleIds 为 null 不改角色，非 null（含空数组）则全量同步
     *
     * @param employee 员工
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateEmployee(SysEmployee employee) {
        log.info("【员工】修改开始, employeeId={}, roleIdsPresent={}",
                employee.getId(), employee.getRoleIds() != null);
        if (employee.getId() == null) {
            throw new BusinessException("员工ID不能为空");
        }
        if (!checkUsernameUnique(employee)) {
            throw new BusinessException("登录账号已存在");
        }
        if (!checkEmpNoUnique(employee)) {
            throw new BusinessException("工号已存在");
        }
        // 改密码请走 resetPwd，避免误覆盖密文
        employee.setPassword(null);
        boolean updated = this.updateById(employee);
        // null：保留原角色；非 null：先删后插（空列表表示清空角色）
        if (employee.getRoleIds() != null) {
            employeeRoleMapper.deleteByEmployeeId(employee.getId());
            insertEmployeeRole(employee);
            log.info("【员工】角色已同步, employeeId={}, roleCount={}",
                    employee.getId(), employee.getRoleIds().size());
        } else {
            log.info("【员工】未传 roleIds，跳过角色同步, employeeId={}", employee.getId());
        }
        return updated ? 1 : 0;
    }

    /**
     * 写入员工-角色关联（调用方需保证 roleIds 非 null；空列表不插入）
     *
     * @param employee 含 id、roleIds
     */
    private void insertEmployeeRole(SysEmployee employee) {
        List<Long> roleIds = employee.getRoleIds();
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        for (Long roleId : roleIds) {
            SysEmployeeRole er = new SysEmployeeRole();
            er.setEmployeeId(employee.getId());
            er.setRoleId(roleId);
            employeeRoleMapper.insert(er);
        }
    }

    /**
     * 批量为列表记录回填 roleIds
     *
     * @param employees 员工列表
     */
    private void fillRoleIds(List<SysEmployee> employees) {
        if (employees == null || employees.isEmpty()) {
            return;
        }
        List<Long> employeeIds = new ArrayList<Long>(employees.size());
        for (SysEmployee employee : employees) {
            employeeIds.add(employee.getId());
            employee.setRoleIds(new ArrayList<Long>());
        }
        List<SysEmployeeRole> relations = employeeRoleMapper.selectByEmployeeIds(employeeIds);
        if (relations == null || relations.isEmpty()) {
            return;
        }
        Map<Long, List<Long>> roleMap = new HashMap<Long, List<Long>>(employees.size());
        for (SysEmployeeRole relation : relations) {
            List<Long> ids = roleMap.get(relation.getEmployeeId());
            if (ids == null) {
                ids = new ArrayList<Long>();
                roleMap.put(relation.getEmployeeId(), ids);
            }
            ids.add(relation.getRoleId());
        }
        for (SysEmployee employee : employees) {
            List<Long> roleIds = roleMap.get(employee.getId());
            if (roleIds != null) {
                employee.setRoleIds(roleIds);
            }
        }
    }

    /**
     * 删除员工及角色关联
     *
     * @param id 员工ID
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteEmployeeById(Long id) {
        log.info("【员工】删除, employeeId={}", id);
        employeeRoleMapper.deleteByEmployeeId(id);
        return this.removeById(id) ? 1 : 0;
    }

    /**
     * 重置密码
     *
     * @param id       员工ID
     * @param password 明文密码
     * @return 影响行数
     */
    @Override
    public int resetPassword(Long id, String password) {
        log.info("【员工】重置密码, employeeId={}", id);
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

    /**
     * 仅更新账号状态，不修改角色与其它业务字段
     *
     * @param id     员工ID
     * @param status 0正常 1停用
     * @return 影响行数
     */
    @Override
    public int updateStatus(Long id, Integer status) {
        log.info("【员工】更新状态, employeeId={}, status={}", id, status);
        if (id == null) {
            throw new BusinessException("员工ID不能为空");
        }
        if (status == null || (status != STATUS_NORMAL && status != STATUS_DISABLE)) {
            throw new BusinessException("状态只能为 0（正常）或 1（停用）");
        }
        SysEmployee employee = this.getById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }
        int rows = baseMapper.updateStatusById(id, status);
        log.info("【员工】状态更新完成, employeeId={}, status={}, rows={}", id, status, rows);
        return rows;
    }

    /**
     * 同诊所内与相邻员工交换排序号
     *
     * @param id        员工ID
     * @param direction up / down
     * @return 1成功 0边界
     */
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

    /**
     * 校验登录账号唯一
     *
     * @param employee 员工
     * @return true=唯一
     */
    @Override
    public boolean checkUsernameUnique(SysEmployee employee) {
        Long id = employee.getId() == null ? -1L : employee.getId();
        SysEmployee info = this.getOne(new LambdaQueryWrapper<SysEmployee>()
                .eq(SysEmployee::getUsername, employee.getUsername())
                .last("LIMIT 1"));
        return info == null || info.getId().equals(id);
    }

    /**
     * 校验工号唯一
     *
     * @param employee 员工
     * @return true=唯一
     */
    @Override
    public boolean checkEmpNoUnique(SysEmployee employee) {
        Long id = employee.getId() == null ? -1L : employee.getId();
        SysEmployee info = this.getOne(new LambdaQueryWrapper<SysEmployee>()
                .eq(SysEmployee::getEmpNo, employee.getEmpNo())
                .last("LIMIT 1"));
        return info == null || info.getId().equals(id);
    }
}
