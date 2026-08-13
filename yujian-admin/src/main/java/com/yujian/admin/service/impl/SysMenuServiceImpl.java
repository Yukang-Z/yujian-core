package com.yujian.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yujian.admin.mapper.SysMenuMapper;
import com.yujian.admin.mapper.SysRoleMenuMapper;
import com.yujian.admin.service.ISysMenuService;
import com.yujian.common.exception.BusinessException;
import com.yujian.common.system.domain.SysMenu;
import com.yujian.common.system.domain.SysRoleMenu;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Override
    public List<SysMenu> selectMenuList(SysMenu menu) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<SysMenu>();
        if (menu != null) {
            if (StringUtils.isNotBlank(menu.getMenuName())) {
                wrapper.like(SysMenu::getMenuName, menu.getMenuName());
            }
            if (StringUtils.isNotBlank(menu.getPlatform())) {
                wrapper.eq(SysMenu::getPlatform, menu.getPlatform());
            }
            if (menu.getStatus() != null) {
                wrapper.eq(SysMenu::getStatus, menu.getStatus());
            }
            if (StringUtils.isNotBlank(menu.getMenuType())) {
                wrapper.eq(SysMenu::getMenuType, menu.getMenuType());
            }
        }
        wrapper.orderByAsc(SysMenu::getSortOrder).orderByAsc(SysMenu::getId);
        return this.list(wrapper);
    }

    @Override
    public List<SysMenu> selectMenuTree(String platform) {
        SysMenu query = new SysMenu();
        query.setPlatform(platform);
        query.setStatus(0);
        List<SysMenu> menus = selectMenuList(query);
        return buildMenuTree(menus);
    }

    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        List<Long> tempList = new ArrayList<Long>();
        for (SysMenu menu : menus) {
            tempList.add(menu.getId());
        }
        for (SysMenu menu : menus) {
            if (!tempList.contains(menu.getParentId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    private void recursionFn(List<SysMenu> list, SysMenu menu) {
        List<SysMenu> childList = getChildList(list, menu);
        menu.setChildren(childList);
        for (SysMenu child : childList) {
            if (hasChild(list, child)) {
                recursionFn(list, child);
            }
        }
    }

    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu menu) {
        List<SysMenu> children = new ArrayList<SysMenu>();
        Iterator<SysMenu> it = list.iterator();
        while (it.hasNext()) {
            SysMenu n = it.next();
            if (n.getParentId() != null && n.getParentId().equals(menu.getId())) {
                children.add(n);
            }
        }
        return children;
    }

    private boolean hasChild(List<SysMenu> list, SysMenu menu) {
        return !getChildList(list, menu).isEmpty();
    }

    @Override
    public SysMenu selectMenuById(Long id) {
        return this.getById(id);
    }

    @Override
    public int insertMenu(SysMenu menu) {
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(0);
        }
        if (menu.getVisible() == null) {
            menu.setVisible(0);
        }
        if (menu.getSortOrder() == null) {
            menu.setSortOrder(0);
        }
        if (StringUtils.isBlank(menu.getPlatform())) {
            menu.setPlatform("web");
        }
        return this.save(menu) ? 1 : 0;
    }

    @Override
    public int updateMenu(SysMenu menu) {
        if (menu.getId().equals(menu.getParentId())) {
            throw new BusinessException("上级菜单不能选择自己");
        }
        return this.updateById(menu) ? 1 : 0;
    }

    @Override
    public int deleteMenuById(Long id) {
        long childCount = this.count(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException("存在子菜单，不允许删除");
        }
        long roleCount = roleMenuMapper.selectCount(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getMenuId, id));
        if (roleCount > 0) {
            throw new BusinessException("菜单已分配角色，不允许删除");
        }
        return this.removeById(id) ? 1 : 0;
    }

    @Override
    public List<SysMenu> selectMenusByEmployeeId(Long employeeId, String platform) {
        List<SysMenu> menus = baseMapper.selectMenusByEmployeeId(employeeId, platform);
        return buildMenuTree(menus);
    }

    @Override
    public List<String> selectPermsByEmployeeId(Long employeeId) {
        return baseMapper.selectPermsByEmployeeId(employeeId);
    }
}
