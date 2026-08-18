package com.yujian.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yujian.common.system.domain.SysClinic;
import com.yujian.common.system.domain.SysEmployeeClinic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 员工-诊所关联 Mapper
 *
 * @author Zhangyk
 * @date 2026-08-18 14:45
 */
@Mapper
public interface SysEmployeeClinicMapper extends BaseMapper<SysEmployeeClinic> {

    /**
     * 删除员工全部诊所关联
     *
     * @param employeeId 员工ID
     * @return 影响行数
     */
    int deleteByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * 查询员工关联的诊所ID列表
     *
     * @param employeeId 员工ID
     * @return 诊所ID列表
     */
    List<Long> selectClinicIdsByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * 查询员工可进入的诊所详情列表（仅正常未删除诊所）
     *
     * @param employeeId 员工ID
     * @return 诊所列表
     */
    List<SysClinic> selectClinicsByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * 判断员工是否关联指定诊所
     *
     * @param employeeId 员工ID
     * @param clinicId   诊所ID
     * @return 关联条数
     */
    int countByEmployeeAndClinic(@Param("employeeId") Long employeeId, @Param("clinicId") Long clinicId);

    /**
     * 批量查询员工-诊所关联（列表回填 clinicIds）
     *
     * @param employeeIds 员工ID列表
     * @return 关联记录
     */
    List<SysEmployeeClinic> selectByEmployeeIds(@Param("employeeIds") List<Long> employeeIds);

    /**
     * 查询指定诊所下的员工ID列表
     *
     * @param clinicId 诊所ID
     * @return 员工ID列表
     */
    List<Long> selectEmployeeIdsByClinicId(@Param("clinicId") Long clinicId);
}
