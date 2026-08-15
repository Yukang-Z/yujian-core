package com.yujian.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yujian.common.system.domain.SysEmployeeRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 员工-角色关联 Mapper
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Mapper
public interface SysEmployeeRoleMapper extends BaseMapper<SysEmployeeRole> {

    /**
     * 按员工删除全部角色关联
     *
     * @param employeeId 员工ID
     * @return 影响行数
     */
    int deleteByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * 查询员工已绑定的角色ID列表
     *
     * @param employeeId 员工ID
     * @return 角色ID列表
     */
    List<Long> selectRoleIdsByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * 批量查询员工-角色关联（列表回填 roleIds 用）
     *
     * @param employeeIds 员工ID列表
     * @return 关联记录
     */
    List<SysEmployeeRole> selectByEmployeeIds(@Param("employeeIds") List<Long> employeeIds);
}
