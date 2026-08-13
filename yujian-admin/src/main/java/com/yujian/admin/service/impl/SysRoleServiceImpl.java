package com.yujian.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yujian.admin.mapper.SysEmployeeRoleMapper;
import com.yujian.admin.mapper.SysRoleMapper;
import com.yujian.admin.mapper.SysRoleMenuMapper;
import com.yujian.admin.service.ISysRoleService;
import com.yujian.common.exception.BusinessException;
import com.yujian.common.system.domain.SysEmployeeRole;
import com.yujian.common.system.domain.SysRole;
import com.yujian.common.system.domain.SysRoleMenu;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Autowired
    private SysEmployeeRoleMapper employeeRoleMapper;

    @Override
    public List<SysRole> selectRoleList(SysRole role) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>();
        if (role != null) {
            if (StringUtils.isNotBlank(role.getRoleName())) {
                wrapper.like(SysRole::getRoleName, role.getRoleName());
            }
            if (StringUtils.isNotBlank(role.getRoleKey())) {
                wrapper.eq(SysRole::getRoleKey, role.getRoleKey());
            }
            if (role.getStatus() != null) {
                wrapper.eq(SysRole::getStatus, role.getStatus());
            }
        }
        wrapper.orderByAsc(SysRole::getSortOrder).orderByAsc(SysRole::getId);
        return this.list(wrapper);
    }

    @Override
    public SysRole selectRoleById(Long id) {
        SysRole role = this.getById(id);
        if (role != null) {
            role.setMenuIds(roleMenuMapper.selectMenuIdsByRoleId(id));
        }
        return role;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRole(SysRole role) {
        if (!checkRoleKeyUnique(role)) {
            throw new BusinessException("角色编码已存在");
        }
        if (role.getStatus() == null) {
            role.setStatus(0);
        }
        if (role.getDataScope() == null) {
            role.setDataScope(1);
        }
        if (role.getSortOrder() == null) {
            long count = this.count();
            role.setSortOrder((int) count + 1);
        }
        boolean saved = this.save(role);
        saveRoleMenus(role.getId(), role.getMenuIds());
        return saved ? 1 : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRole(SysRole role) {
        if (!checkRoleKeyUnique(role)) {
            throw new BusinessException("角色编码已存在");
        }
        boolean updated = this.updateById(role);
        if (role.getMenuIds() != null) {
            saveRoleMenus(role.getId(), role.getMenuIds());
        }
        return updated ? 1 : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleById(Long id) {
        long used = employeeRoleMapper.selectCount(new LambdaQueryWrapper<SysEmployeeRole>().eq(SysEmployeeRole::getRoleId, id));
        if (used > 0) {
            throw new BusinessException("角色已分配给员工，不允许删除");
        }
        roleMenuMapper.deleteByRoleId(id);
        return this.removeById(id) ? 1 : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveRoleMenus(Long roleId, List<Long> menuIds) {
        roleMenuMapper.deleteByRoleId(roleId);
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                roleMenuMapper.insert(rm);
            }
        }
        return 1;
    }

    @Override
    public List<Long> selectMenuIdsByRoleId(Long roleId) {
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    public boolean checkRoleKeyUnique(SysRole role) {
        Long id = role.getId() == null ? -1L : role.getId();
        SysRole info = this.getOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleKey, role.getRoleKey())
                .last("LIMIT 1"));
        return info == null || info.getId().equals(id);
    }

    @Override
    public int moveRole(Long id, String direction) {
        SysRole current = this.getById(id);
        if (current == null) {
            throw new BusinessException("角色不存在");
        }
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>();
        if ("up".equalsIgnoreCase(direction)) {
            wrapper.lt(SysRole::getSortOrder, current.getSortOrder())
                    .orderByDesc(SysRole::getSortOrder)
                    .last("LIMIT 1");
        } else {
            wrapper.gt(SysRole::getSortOrder, current.getSortOrder())
                    .orderByAsc(SysRole::getSortOrder)
                    .last("LIMIT 1");
        }
        SysRole target = this.getOne(wrapper);
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
}
