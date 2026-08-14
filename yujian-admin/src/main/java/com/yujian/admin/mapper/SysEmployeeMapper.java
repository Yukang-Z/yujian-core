package com.yujian.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yujian.common.system.domain.SysEmployee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 员工 Mapper
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Mapper
public interface SysEmployeeMapper extends BaseMapper<SysEmployee> {

    /**
     * 员工分页查询（含诊所名、部门名、角色名）
     *
     * @param page         分页参数
     * @param keyword      姓名/手机/工号关键字
     * @param clinicId     诊所ID
     * @param deptId       部门ID
     * @param employStatus 在职状态
     * @return 分页结果
     */
    IPage<SysEmployee> selectEmployeePage(Page<SysEmployee> page,
                                          @Param("keyword") String keyword,
                                          @Param("clinicId") Long clinicId,
                                          @Param("deptId") Long deptId,
                                          @Param("employStatus") Integer employStatus);

    /**
     * 按登录账号查询员工（登录用）
     *
     * @param username 登录账号
     * @return 员工，不存在为 null
     */
    SysEmployee selectByUsername(@Param("username") String username);
}
