package com.yujian.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yujian.common.system.domain.SysRole;

import java.util.List;

public interface ISysRoleService extends IService<SysRole> {

    List<SysRole> selectRoleList(SysRole role);

    SysRole selectRoleById(Long id);

    int insertRole(SysRole role);

    int updateRole(SysRole role);

    int deleteRoleById(Long id);

    int saveRoleMenus(Long roleId, List<Long> menuIds);

    List<Long> selectMenuIdsByRoleId(Long roleId);

    boolean checkRoleKeyUnique(SysRole role);

    int moveRole(Long id, String direction);
}
