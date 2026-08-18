package com.yujian.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yujian.admin.mapper.SysDeptMapper;
import com.yujian.admin.service.ISysDeptService;
import com.yujian.common.core.context.SecurityContextHolder;
import com.yujian.common.exception.BusinessException;
import com.yujian.common.system.domain.SysDept;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

    @Override
    public List<SysDept> selectDeptList(SysDept dept) {
        Long clinicId = dept == null ? null : dept.getClinicId();
        clinicId = SecurityContextHolder.requireClinicId(clinicId);
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<SysDept>();
        wrapper.eq(SysDept::getClinicId, clinicId);
        if (dept != null) {
            if (StringUtils.isNotBlank(dept.getDeptName())) {
                wrapper.like(SysDept::getDeptName, dept.getDeptName());
            }
            if (dept.getStatus() != null) {
                wrapper.eq(SysDept::getStatus, dept.getStatus());
            }
        }
        wrapper.orderByAsc(SysDept::getSortOrder).orderByAsc(SysDept::getId);
        return this.list(wrapper);
    }

    @Override
    public int insertDept(SysDept dept) {
        if (dept.getClinicId() == null) {
            dept.setClinicId(SecurityContextHolder.requireClinicId());
        }
        if (dept.getParentId() == null) {
            dept.setParentId(0L);
        }
        if (dept.getStatus() == null) {
            dept.setStatus(0);
        }
        if (dept.getSortOrder() == null) {
            dept.setSortOrder(0);
        }
        return this.save(dept) ? 1 : 0;
    }

    @Override
    public int updateDept(SysDept dept) {
        return this.updateById(dept) ? 1 : 0;
    }

    @Override
    public int deleteDeptById(Long id) {
        long childCount = this.count(new LambdaQueryWrapper<SysDept>().eq(SysDept::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException("存在下级部门，不允许删除");
        }
        return this.removeById(id) ? 1 : 0;
    }
}
