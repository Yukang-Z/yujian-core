package com.yujian.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yujian.common.system.domain.SysDept;

import java.util.List;

public interface ISysDeptService extends IService<SysDept> {

    List<SysDept> selectDeptList(SysDept dept);

    int insertDept(SysDept dept);

    int updateDept(SysDept dept);

    int deleteDeptById(Long id);
}
