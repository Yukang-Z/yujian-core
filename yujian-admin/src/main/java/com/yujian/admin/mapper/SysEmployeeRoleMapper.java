package com.yujian.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yujian.common.system.domain.SysEmployeeRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysEmployeeRoleMapper extends BaseMapper<SysEmployeeRole> {

    int deleteByEmployeeId(@Param("employeeId") Long employeeId);

    List<Long> selectRoleIdsByEmployeeId(@Param("employeeId") Long employeeId);
}
