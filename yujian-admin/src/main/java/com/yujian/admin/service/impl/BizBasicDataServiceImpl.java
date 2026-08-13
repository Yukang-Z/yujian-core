package com.yujian.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yujian.admin.mapper.BizDictDataMapper;
import com.yujian.admin.mapper.BizDictTypeMapper;
import com.yujian.admin.mapper.BizPatientSourceMapper;
import com.yujian.admin.mapper.BizPatientTagMapper;
import com.yujian.admin.mapper.BizTreatItemMapper;
import com.yujian.admin.mapper.SysEmployeeMapper;
import com.yujian.admin.service.IBizBasicDataService;
import com.yujian.common.biz.domain.BizDictData;
import com.yujian.common.biz.domain.BizDictType;
import com.yujian.common.biz.domain.BizPatientSource;
import com.yujian.common.biz.domain.BizPatientTag;
import com.yujian.common.biz.domain.BizTreatItem;
import com.yujian.common.core.context.SecurityContextHolder;
import com.yujian.common.exception.BusinessException;
import com.yujian.common.system.domain.SysEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class BizBasicDataServiceImpl implements IBizBasicDataService {

    @Autowired
    private BizDictTypeMapper dictTypeMapper;

    @Autowired
    private BizDictDataMapper dictDataMapper;

    @Autowired
    private BizPatientTagMapper tagMapper;

    @Autowired
    private BizPatientSourceMapper sourceMapper;

    @Autowired
    private BizTreatItemMapper treatItemMapper;

    @Autowired
    private SysEmployeeMapper employeeMapper;

    @Override
    public List<BizDictData> selectDictByType(String dictType) {
        return dictDataMapper.selectList(new LambdaQueryWrapper<BizDictData>()
                .eq(BizDictData::getDictType, dictType)
                .eq(BizDictData::getStatus, 0)
                .orderByAsc(BizDictData::getSortOrder));
    }

    @Override
    public List<BizDictType> selectDictTypeList() {
        return dictTypeMapper.selectList(new LambdaQueryWrapper<BizDictType>()
                .orderByAsc(BizDictType::getId));
    }

    @Override
    public int saveDictData(BizDictData data) {
        if (data.getId() == null) {
            if (data.getStatus() == null) {
                data.setStatus(0);
            }
            if (data.getSortOrder() == null) {
                data.setSortOrder(0);
            }
            return dictDataMapper.insert(data);
        }
        return dictDataMapper.updateById(data);
    }

    @Override
    public int deleteDictData(Long id) {
        return dictDataMapper.deleteById(id);
    }

    @Override
    public List<BizPatientTag> selectTagList(Long clinicId) {
        clinicId = resolveClinicId(clinicId);
        return tagMapper.selectList(new LambdaQueryWrapper<BizPatientTag>()
                .eq(clinicId != null, BizPatientTag::getClinicId, clinicId)
                .eq(BizPatientTag::getStatus, 0)
                .orderByAsc(BizPatientTag::getSortOrder));
    }

    @Override
    public int saveTag(BizPatientTag tag) {
        if (tag.getClinicId() == null) {
            tag.setClinicId(resolveClinicId(null));
        }
        if (tag.getId() == null) {
            if (tag.getStatus() == null) {
                tag.setStatus(0);
            }
            if (tag.getSortOrder() == null) {
                tag.setSortOrder(0);
            }
            return tagMapper.insert(tag);
        }
        return tagMapper.updateById(tag);
    }

    @Override
    public int deleteTag(Long id) {
        return tagMapper.deleteById(id);
    }

    @Override
    public List<BizPatientSource> selectSourceTree(Long clinicId) {
        clinicId = resolveClinicId(clinicId);
        List<BizPatientSource> list = sourceMapper.selectList(new LambdaQueryWrapper<BizPatientSource>()
                .eq(clinicId != null, BizPatientSource::getClinicId, clinicId)
                .eq(BizPatientSource::getStatus, 0)
                .orderByAsc(BizPatientSource::getSortOrder));
        return buildSourceTree(list);
    }

    private List<BizPatientSource> buildSourceTree(List<BizPatientSource> list) {
        List<BizPatientSource> roots = new ArrayList<BizPatientSource>();
        List<Long> ids = new ArrayList<Long>();
        for (BizPatientSource s : list) {
            ids.add(s.getId());
        }
        for (BizPatientSource s : list) {
            if (s.getParentId() == null || s.getParentId() == 0L || !ids.contains(s.getParentId())) {
                recursionSource(list, s);
                roots.add(s);
            }
        }
        return roots.isEmpty() ? list : roots;
    }

    private void recursionSource(List<BizPatientSource> list, BizPatientSource parent) {
        List<BizPatientSource> children = new ArrayList<BizPatientSource>();
        Iterator<BizPatientSource> it = list.iterator();
        while (it.hasNext()) {
            BizPatientSource n = it.next();
            if (n.getParentId() != null && n.getParentId().equals(parent.getId())) {
                children.add(n);
            }
        }
        parent.setChildren(children);
        for (BizPatientSource child : children) {
            recursionSource(list, child);
        }
    }

    @Override
    public int saveSource(BizPatientSource source) {
        if (source.getClinicId() == null) {
            source.setClinicId(resolveClinicId(null));
        }
        if (source.getParentId() == null) {
            source.setParentId(0L);
        }
        if (source.getId() == null) {
            if (source.getStatus() == null) {
                source.setStatus(0);
            }
            return sourceMapper.insert(source);
        }
        return sourceMapper.updateById(source);
    }

    @Override
    public int deleteSource(Long id) {
        long child = sourceMapper.selectCount(new LambdaQueryWrapper<BizPatientSource>()
                .eq(BizPatientSource::getParentId, id));
        if (child > 0) {
            throw new BusinessException("存在下级来源，不允许删除");
        }
        return sourceMapper.deleteById(id);
    }

    @Override
    public List<BizTreatItem> selectTreatItemList(Long clinicId) {
        clinicId = resolveClinicId(clinicId);
        return treatItemMapper.selectList(new LambdaQueryWrapper<BizTreatItem>()
                .eq(clinicId != null, BizTreatItem::getClinicId, clinicId)
                .eq(BizTreatItem::getStatus, 0)
                .orderByAsc(BizTreatItem::getSortOrder));
    }

    @Override
    public int saveTreatItem(BizTreatItem item) {
        if (item.getClinicId() == null) {
            item.setClinicId(resolveClinicId(null));
        }
        if (item.getId() == null) {
            if (item.getStatus() == null) {
                item.setStatus(0);
            }
            if (item.getDuration() == null) {
                item.setDuration(30);
            }
            return treatItemMapper.insert(item);
        }
        return treatItemMapper.updateById(item);
    }

    @Override
    public int deleteTreatItem(Long id) {
        return treatItemMapper.deleteById(id);
    }

    @Override
    public List<?> selectDoctorList(Long clinicId) {
        clinicId = resolveClinicId(clinicId);
        // 岗位含医生，或角色后续可扩展；先按岗位/在职筛选
        return employeeMapper.selectList(new LambdaQueryWrapper<SysEmployee>()
                .eq(clinicId != null, SysEmployee::getClinicId, clinicId)
                .eq(SysEmployee::getEmployStatus, 1)
                .eq(SysEmployee::getStatus, 0)
                .and(w -> w.like(SysEmployee::getPosition, "医生")
                        .or().like(SysEmployee::getPosition, "医师")
                        .or().eq(SysEmployee::getPosition, "Doctor"))
                .orderByAsc(SysEmployee::getSortOrder)
                .select(SysEmployee::getId, SysEmployee::getName, SysEmployee::getEmpNo,
                        SysEmployee::getPosition, SysEmployee::getClinicId));
    }

    private Long resolveClinicId(Long clinicId) {
        if (clinicId != null) {
            return clinicId;
        }
        return SecurityContextHolder.getClinicId();
    }
}
