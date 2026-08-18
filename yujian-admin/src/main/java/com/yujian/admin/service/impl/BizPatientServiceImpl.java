package com.yujian.admin.service.impl;

import cn.hutool.extra.pinyin.PinyinUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yujian.admin.mapper.BizAppointmentMapper;
import com.yujian.admin.mapper.BizPatientMapper;
import com.yujian.admin.mapper.BizPatientTagRelMapper;
import com.yujian.admin.service.IBizPatientService;
import com.yujian.common.biz.domain.BizAppointment;
import com.yujian.common.biz.domain.BizPatient;
import com.yujian.common.biz.domain.BizPatientTagRel;
import com.yujian.common.core.context.SecurityContextHolder;
import com.yujian.common.core.domain.PageResult;
import com.yujian.common.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class BizPatientServiceImpl extends ServiceImpl<BizPatientMapper, BizPatient> implements IBizPatientService {

    @Autowired
    private BizPatientTagRelMapper tagRelMapper;

    @Autowired
    private BizAppointmentMapper appointmentMapper;

    @Autowired
    private com.yujian.admin.service.PatientLogHelper patientLogHelper;

    @Override
    public PageResult<BizPatient> selectPage(String keyword, Long clinicId, Long doctorId,
                                             Long firstDoctorId, Long tagId, long pageNum, long pageSize) {
        clinicId = SecurityContextHolder.requireClinicId(clinicId);
        Page<BizPatient> page = new Page<BizPatient>(pageNum, pageSize);
        return PageResult.of(baseMapper.selectPatientPage(page, clinicId, keyword, doctorId, firstDoctorId, tagId));
    }

    @Override
    public BizPatient selectById(Long id) {
        BizPatient patient = this.getById(id);
        if (patient == null) {
            return null;
        }
        Long clinicId = SecurityContextHolder.requireClinicId();
        if (patient.getClinicId() != null && !clinicId.equals(patient.getClinicId())) {
            throw new BusinessException("患者不属于当前诊所");
        }
        patient.setTagIds(tagRelMapper.selectTagIdsByPatientId(id));
        return patient;
    }

    @Override
    public List<BizPatient> search(String keyword, Long clinicId, int limit) {
        clinicId = SecurityContextHolder.requireClinicId(clinicId);
        LambdaQueryWrapper<BizPatient> wrapper = new LambdaQueryWrapper<BizPatient>();
        wrapper.eq(BizPatient::getClinicId, clinicId);
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(BizPatient::getName, keyword)
                    .or().like(BizPatient::getMobile, keyword)
                    .or().like(BizPatient::getMedicalRecordNo, keyword)
                    .or().like(BizPatient::getNamePinyin, keyword));
        }
        wrapper.orderByDesc(BizPatient::getId).last("LIMIT " + Math.max(limit, 1));
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPatient(BizPatient patient) {
        fillDefaults(patient);
        if (StringUtils.isBlank(patient.getCreatorName())
                && SecurityContextHolder.getLoginUser() != null) {
            patient.setCreatorName(SecurityContextHolder.getLoginUser().getName());
        }
        boolean saved = this.save(patient);
        saveTags(patient);
        if (saved) {
            patientLogHelper.write(patient.getId(), patient.getClinicId(), "create", "新增患者");
        }
        return saved ? 1 : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePatient(BizPatient patient) {
        if (StringUtils.isNotBlank(patient.getName()) && StringUtils.isBlank(patient.getNamePinyin())) {
            try {
                patient.setNamePinyin(PinyinUtil.getFirstLetter(patient.getName(), "").toUpperCase());
            } catch (Exception e) {
                patient.setNamePinyin("");
            }
        }
        boolean updated = this.updateById(patient);
        if (patient.getTagIds() != null) {
            tagRelMapper.deleteByPatientId(patient.getId());
            saveTags(patient);
        }
        if (updated) {
            patientLogHelper.write(patient.getId(), patient.getClinicId(), "update", "修改患者信息");
        }
        return updated ? 1 : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePatient(Long id) {
        tagRelMapper.deleteByPatientId(id);
        return this.removeById(id) ? 1 : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveAndAppoint(BizPatient patient) {
        insertPatient(patient);
        return patient.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveAndArrive(BizPatient patient) {
        insertPatient(patient);
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MINUTE, 30);
        BizAppointment appt = new BizAppointment();
        appt.setClinicId(patient.getClinicId());
        appt.setPatientId(patient.getId());
        appt.setDoctorId(patient.getDoctorId());
        appt.setStartTime(now);
        appt.setEndTime(cal.getTime());
        appt.setVisitType(1);
        appt.setStatus(BizAppointment.STATUS_ARRIVED);
        appt.setRegistered(1);
        appt.setTriaged(1);
        appointmentMapper.insert(appt);
        return patient.getId();
    }

    private void fillDefaults(BizPatient patient) {
        if (StringUtils.isBlank(patient.getName())) {
            throw new BusinessException("患者姓名不能为空");
        }
        if (StringUtils.isBlank(patient.getMobile())) {
            throw new BusinessException("手机号不能为空");
        }
        // 新增患者强制归属当前所选诊所
        patient.setClinicId(SecurityContextHolder.requireClinicId());
        if (StringUtils.isBlank(patient.getMedicalRecordNo())) {
            patient.setMedicalRecordNo(generateMedicalRecordNo(patient.getClinicId()));
        } else {
            long exists = this.count(new LambdaQueryWrapper<BizPatient>()
                    .eq(BizPatient::getClinicId, patient.getClinicId())
                    .eq(BizPatient::getMedicalRecordNo, patient.getMedicalRecordNo()));
            if (exists > 0) {
                throw new BusinessException("病历号已存在");
            }
        }
        if (StringUtils.isBlank(patient.getNamePinyin())) {
            try {
                patient.setNamePinyin(PinyinUtil.getFirstLetter(patient.getName(), "").toUpperCase());
            } catch (Exception e) {
                patient.setNamePinyin("");
            }
        }
        if (patient.getGender() == null) {
            patient.setGender(2);
        }
        if (patient.getPatientType() == null) {
            patient.setPatientType(1);
        }
        if (patient.getStarLevel() == null) {
            patient.setStarLevel(0);
        }
        if (patient.getStatus() == null) {
            patient.setStatus(0);
        }
        if (patient.getOweAmount() == null) {
            patient.setOweAmount(BigDecimal.ZERO);
        }
        if (patient.getPaidAmount() == null) {
            patient.setPaidAmount(BigDecimal.ZERO);
        }
        if (patient.getAge() == null && patient.getBirthday() != null) {
            Calendar birth = Calendar.getInstance();
            birth.setTime(patient.getBirthday());
            Calendar now = Calendar.getInstance();
            int age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
            patient.setAge(Math.max(age, 0));
        }
    }

    private String generateMedicalRecordNo(Long clinicId) {
        String max = baseMapper.selectMaxMedicalRecordNo(clinicId);
        int next = 1;
        if (StringUtils.isNotBlank(max)) {
            try {
                next = Integer.parseInt(max) + 1;
            } catch (NumberFormatException ignored) {
                next = 1;
            }
        }
        return String.format("%06d", next);
    }

    private void saveTags(BizPatient patient) {
        List<Long> tagIds = patient.getTagIds();
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        for (Long tagId : tagIds) {
            BizPatientTagRel rel = new BizPatientTagRel();
            rel.setPatientId(patient.getId());
            rel.setTagId(tagId);
            tagRelMapper.insert(rel);
        }
    }
}
