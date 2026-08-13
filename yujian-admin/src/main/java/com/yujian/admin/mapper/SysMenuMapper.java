package com.yujian.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yujian.common.system.domain.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> selectMenusByRoleId(@Param("roleId") Long roleId);

    List<SysMenu> selectMenusByEmployeeId(@Param("employeeId") Long employeeId, @Param("platform") String platform);

    List<String> selectPermsByEmployeeId(@Param("employeeId") Long employeeId);
}
