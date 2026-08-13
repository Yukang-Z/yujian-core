package com.yujian.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yujian.common.system.domain.SysEmployee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysEmployeeMapper extends BaseMapper<SysEmployee> {

    IPage<SysEmployee> selectEmployeePage(Page<SysEmployee> page,
                                          @Param("keyword") String keyword,
                                          @Param("clinicId") Long clinicId,
                                          @Param("deptId") Long deptId,
                                          @Param("employStatus") Integer employStatus);
}
