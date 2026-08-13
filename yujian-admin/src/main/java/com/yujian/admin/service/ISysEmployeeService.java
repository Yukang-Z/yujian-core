package com.yujian.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yujian.common.core.domain.PageResult;
import com.yujian.common.system.domain.SysEmployee;

public interface ISysEmployeeService extends IService<SysEmployee> {

    PageResult<SysEmployee> selectEmployeePage(String keyword, Long clinicId, Long deptId,
                                               Integer employStatus, long pageNum, long pageSize);

    SysEmployee selectEmployeeById(Long id);

    int insertEmployee(SysEmployee employee);

    int updateEmployee(SysEmployee employee);

    int deleteEmployeeById(Long id);

    int resetPassword(Long id, String password);

    int updateSortOrder(Long id, String direction);

    boolean checkUsernameUnique(SysEmployee employee);

    boolean checkEmpNoUnique(SysEmployee employee);
}
