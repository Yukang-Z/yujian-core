package com.yujian.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yujian.common.system.domain.SysMenu;

import java.util.List;

public interface ISysMenuService extends IService<SysMenu> {

    List<SysMenu> selectMenuList(SysMenu menu);

    List<SysMenu> selectMenuTree(String platform);

    List<SysMenu> buildMenuTree(List<SysMenu> menus);

    SysMenu selectMenuById(Long id);

    int insertMenu(SysMenu menu);

    int updateMenu(SysMenu menu);

    int deleteMenuById(Long id);

    List<SysMenu> selectMenusByEmployeeId(Long employeeId, String platform);

    List<String> selectPermsByEmployeeId(Long employeeId);
}
