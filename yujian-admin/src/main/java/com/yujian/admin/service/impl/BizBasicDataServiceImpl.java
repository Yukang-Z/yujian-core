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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 基础数据服务实现（字典、标签、来源、诊疗项目、医生/咨询师列表）。
 * <p>
 * 医生、咨询师、诊疗项目列表的 clinicId 均走授权解析，禁止越权看未授权诊所数据。
 * </p>
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Service
public class BizBasicDataServiceImpl implements IBizBasicDataService {

    private static final Logger log = LoggerFactory.getLogger(BizBasicDataServiceImpl.class);
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

    @Autowired
    private com.yujian.admin.mapper.SysEmployeeClinicMapper employeeClinicMapper;

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

    /**
     * 查询授权诊所下启用诊疗项目；clinicId 授权生效；keyword 搜名称/编码；duration 空则补 30。
     *
     * @param clinicId 授权诊所，空=会话
     * @param keyword  名称/编码关键字
     * @return 项目列表
     */
    @Override
    public List<BizTreatItem> selectTreatItemList(Long clinicId, String keyword) {
        // 查询场景：授权诊所内可查看，右侧预约项目随「预约门诊」切换
        clinicId = SecurityContextHolder.resolveAuthorizedClinicId(clinicId);
        if (StringUtils.isNotBlank(keyword)) {
            keyword = keyword.trim();
            if (keyword.length() > 64) {
                keyword = keyword.substring(0, 64);
            }
        } else {
            keyword = null;
        }
        log.info("【基础数据】查询诊疗项目, clinicId={}, keyword={}", clinicId, keyword);
        LambdaQueryWrapper<BizTreatItem> wrapper = new LambdaQueryWrapper<BizTreatItem>()
                .eq(BizTreatItem::getClinicId, clinicId)
                .eq(BizTreatItem::getStatus, 0);
        if (StringUtils.isNotBlank(keyword)) {
            final String kw = keyword;
            wrapper.and(w -> w.like(BizTreatItem::getItemName, kw)
                    .or().like(BizTreatItem::getItemCode, kw));
        }
        wrapper.orderByAsc(BizTreatItem::getSortOrder).orderByAsc(BizTreatItem::getId);
        List<BizTreatItem> list = treatItemMapper.selectList(wrapper);
        if (list != null) {
            for (BizTreatItem item : list) {
                // 前端拖格默认时长；库空时兜底 30 分钟
                if (item.getDuration() == null) {
                    item.setDuration(30);
                }
            }
        }
        log.info("【基础数据】诊疗项目查询完成, clinicId={}, count={}", clinicId, list == null ? 0 : list.size());
        return list == null ? new ArrayList<BizTreatItem>() : list;
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

    /**
     * 查询指定诊所下可预约医生列表（在职、启用、职位含医生/医师）
     * <p>
     * clinicId 在账号授权诊所范围内生效，空则回退会话当前诊所；支持姓名/手机模糊搜索。
     * </p>
     *
     * @param clinicId 要查询的诊所ID，可空
     * @param keyword  姓名/手机号关键字，可空
     * @return 医生列表（含 id、name、empNo、position、clinicId、mobile）
     */
    @Override
    public List<?> selectDoctorList(Long clinicId, String keyword) {
        // 查询场景：授权诊所内可查看，禁止越权
        clinicId = SecurityContextHolder.resolveAuthorizedClinicId(clinicId);
        log.info("【基础数据】查询医生列表, clinicId={}, keyword={}", clinicId, keyword);
        List<Long> employeeIds = employeeClinicMapper.selectEmployeeIdsByClinicId(clinicId);
        if (employeeIds == null || employeeIds.isEmpty()) {
            log.info("【基础数据】诊所下无关联员工, clinicId={}", clinicId);
            return new ArrayList<SysEmployee>();
        }
        LambdaQueryWrapper<SysEmployee> wrapper = new LambdaQueryWrapper<SysEmployee>()
                .in(SysEmployee::getId, employeeIds)
                .eq(SysEmployee::getEmployStatus, 1)
                .eq(SysEmployee::getStatus, 0)
                .and(w -> w.like(SysEmployee::getPosition, "医生")
                        .or().like(SysEmployee::getPosition, "医师")
                        .or().eq(SysEmployee::getPosition, "Doctor"));
        // 关键字：姓名或手机号模糊
        if (StringUtils.isNotBlank(keyword)) {
            final String kw = keyword.trim();
            wrapper.and(w -> w.like(SysEmployee::getName, kw).or().like(SysEmployee::getMobile, kw));
        }
        wrapper.orderByAsc(SysEmployee::getSortOrder)
                .select(SysEmployee::getId, SysEmployee::getName, SysEmployee::getEmpNo,
                        SysEmployee::getPosition, SysEmployee::getClinicId, SysEmployee::getMobile);
        List<SysEmployee> list = employeeMapper.selectList(wrapper);
        log.info("【基础数据】医生列表查询完成, clinicId={}, count={}", clinicId, list == null ? 0 : list.size());
        return list == null ? new ArrayList<SysEmployee>() : list;
    }

    /**
     * 查询指定诊所下咨询师列表（在职、启用、职位含「咨询」）
     * <p>
     * clinicId 在账号授权诊所范围内生效，空则回退会话当前诊所；支持姓名/手机模糊搜索。
     * </p>
     *
     * @param clinicId 要查询的诊所ID，可空
     * @param keyword  姓名/手机号关键字，可空
     * @return 咨询师列表
     */
    @Override
    public List<?> selectConsultantList(Long clinicId, String keyword) {
        clinicId = SecurityContextHolder.resolveAuthorizedClinicId(clinicId);
        log.info("【基础数据】查询咨询师列表, clinicId={}, keyword={}", clinicId, keyword);
        List<Long> employeeIds = employeeClinicMapper.selectEmployeeIdsByClinicId(clinicId);
        if (employeeIds == null || employeeIds.isEmpty()) {
            log.info("【基础数据】诊所下无关联员工, clinicId={}", clinicId);
            return new ArrayList<SysEmployee>();
        }
        LambdaQueryWrapper<SysEmployee> wrapper = new LambdaQueryWrapper<SysEmployee>()
                .in(SysEmployee::getId, employeeIds)
                .eq(SysEmployee::getEmployStatus, 1)
                .eq(SysEmployee::getStatus, 0)
                .and(w -> w.like(SysEmployee::getPosition, "咨询")
                        .or().eq(SysEmployee::getPosition, "Consultant"));
        if (StringUtils.isNotBlank(keyword)) {
            final String kw = keyword.trim();
            wrapper.and(w -> w.like(SysEmployee::getName, kw).or().like(SysEmployee::getMobile, kw));
        }
        wrapper.orderByAsc(SysEmployee::getSortOrder)
                .select(SysEmployee::getId, SysEmployee::getName, SysEmployee::getEmpNo,
                        SysEmployee::getPosition, SysEmployee::getClinicId, SysEmployee::getMobile);
        List<SysEmployee> list = employeeMapper.selectList(wrapper);
        log.info("【基础数据】咨询师列表查询完成, clinicId={}, count={}", clinicId, list == null ? 0 : list.size());
        return list == null ? new ArrayList<SysEmployee>() : list;
    }

    /**
     * 写操作诊所：强制会话当前诊所
     *
     * @param clinicId 请求诊所ID（忽略）
     * @return 会话诊所ID
     */
    private Long resolveClinicId(Long clinicId) {
        return SecurityContextHolder.requireClinicId(clinicId);
    }
}
