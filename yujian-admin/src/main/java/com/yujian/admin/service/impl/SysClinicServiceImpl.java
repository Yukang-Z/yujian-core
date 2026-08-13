package com.yujian.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yujian.admin.mapper.SysClinicMapper;
import com.yujian.admin.mapper.SysEmployeeMapper;
import com.yujian.admin.service.ISysClinicService;
import com.yujian.common.exception.BusinessException;
import com.yujian.common.system.domain.SysClinic;
import com.yujian.common.system.domain.SysEmployee;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class SysClinicServiceImpl extends ServiceImpl<SysClinicMapper, SysClinic> implements ISysClinicService {

    @Autowired
    private SysEmployeeMapper employeeMapper;

    @Override
    public List<SysClinic> selectClinicList(SysClinic clinic) {
        LambdaQueryWrapper<SysClinic> wrapper = new LambdaQueryWrapper<SysClinic>();
        if (clinic != null) {
            if (StringUtils.isNotBlank(clinic.getClinicName())) {
                wrapper.like(SysClinic::getClinicName, clinic.getClinicName());
            }
            if (StringUtils.isNotBlank(clinic.getClinicCode())) {
                wrapper.eq(SysClinic::getClinicCode, clinic.getClinicCode());
            }
            if (clinic.getStatus() != null) {
                wrapper.eq(SysClinic::getStatus, clinic.getStatus());
            }
            if (clinic.getParentId() != null) {
                wrapper.eq(SysClinic::getParentId, clinic.getParentId());
            }
        }
        wrapper.orderByAsc(SysClinic::getSortOrder).orderByAsc(SysClinic::getId);
        return this.list(wrapper);
    }

    @Override
    public List<SysClinic> selectClinicTree(SysClinic clinic) {
        List<SysClinic> clinics = selectClinicList(clinic);
        return buildTree(clinics);
    }

    private List<SysClinic> buildTree(List<SysClinic> clinics) {
        List<SysClinic> returnList = new ArrayList<SysClinic>();
        List<Long> tempList = new ArrayList<Long>();
        for (SysClinic c : clinics) {
            tempList.add(c.getId());
        }
        for (SysClinic c : clinics) {
            if (!tempList.contains(c.getParentId())) {
                recursionFn(clinics, c);
                returnList.add(c);
            }
        }
        if (returnList.isEmpty()) {
            returnList = clinics;
        }
        return returnList;
    }

    private void recursionFn(List<SysClinic> list, SysClinic clinic) {
        List<SysClinic> childList = getChildList(list, clinic);
        // 使用临时字段存储，此处简化：树结构由前端根据 parentId 组装也可
        // 为保持简单，不在实体加 children，树接口返回扁平列表 + parentId
    }

    private List<SysClinic> getChildList(List<SysClinic> list, SysClinic clinic) {
        List<SysClinic> children = new ArrayList<SysClinic>();
        Iterator<SysClinic> it = list.iterator();
        while (it.hasNext()) {
            SysClinic n = it.next();
            if (n.getParentId() != null && n.getParentId().equals(clinic.getId())) {
                children.add(n);
            }
        }
        return children;
    }

    @Override
    public boolean checkClinicCodeUnique(SysClinic clinic) {
        Long id = clinic.getId() == null ? -1L : clinic.getId();
        SysClinic info = this.getOne(new LambdaQueryWrapper<SysClinic>()
                .eq(SysClinic::getClinicCode, clinic.getClinicCode())
                .last("LIMIT 1"));
        return info == null || info.getId().equals(id);
    }

    @Override
    public int insertClinic(SysClinic clinic) {
        if (!checkClinicCodeUnique(clinic)) {
            throw new BusinessException("诊所编码已存在");
        }
        if (clinic.getParentId() == null) {
            clinic.setParentId(0L);
        }
        if (clinic.getStatus() == null) {
            clinic.setStatus(0);
        }
        if (clinic.getSortOrder() == null) {
            clinic.setSortOrder(0);
        }
        return this.save(clinic) ? 1 : 0;
    }

    @Override
    public int updateClinic(SysClinic clinic) {
        if (!checkClinicCodeUnique(clinic)) {
            throw new BusinessException("诊所编码已存在");
        }
        if (clinic.getId().equals(clinic.getParentId())) {
            throw new BusinessException("上级诊所不能是自己");
        }
        return this.updateById(clinic) ? 1 : 0;
    }

    @Override
    public int deleteClinicById(Long id) {
        long childCount = this.count(new LambdaQueryWrapper<SysClinic>().eq(SysClinic::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException("存在下级诊所，不允许删除");
        }
        long empCount = employeeMapper.selectCount(new LambdaQueryWrapper<SysEmployee>().eq(SysEmployee::getClinicId, id));
        if (empCount > 0) {
            throw new BusinessException("诊所下存在员工，不允许删除");
        }
        return this.removeById(id) ? 1 : 0;
    }
}
