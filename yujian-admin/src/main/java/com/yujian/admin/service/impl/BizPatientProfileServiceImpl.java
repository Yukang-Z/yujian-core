package com.yujian.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yujian.admin.mapper.BizAppointmentMapper;
import com.yujian.admin.mapper.BizPatientLogMapper;
import com.yujian.admin.mapper.BizPatientMapper;
import com.yujian.admin.mapper.BizPatientTagRelMapper;
import com.yujian.admin.mapper.BizTreatmentRecordMapper;
import com.yujian.admin.mapper.SysEmployeeMapper;
import com.yujian.admin.service.IBizPatientProfileService;
import com.yujian.common.biz.domain.BizAppointment;
import com.yujian.common.biz.domain.BizPatient;
import com.yujian.common.biz.domain.BizTreatmentRecord;
import com.yujian.common.core.context.SecurityContextHolder;
import com.yujian.common.core.domain.PageResult;
import com.yujian.common.exception.BusinessException;
import com.yujian.common.system.domain.SysEmployee;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BizPatientProfileServiceImpl implements IBizPatientProfileService {

    @Autowired
    private BizPatientMapper patientMapper;

    @Autowired
    private BizPatientTagRelMapper tagRelMapper;

    @Autowired
    private BizAppointmentMapper appointmentMapper;

    @Autowired
    private BizTreatmentRecordMapper treatmentRecordMapper;

    @Autowired
    private SysEmployeeMapper employeeMapper;

    @Autowired
    private BizPatientLogMapper patientLogMapper;

    @Override
    public PageResult<BizPatient> sidebar(String type, Long clinicId, String keyword, Date day,
                                          long pageNum, long pageSize) {
        clinicId = SecurityContextHolder.requireClinicId(clinicId);
        String t = StringUtils.defaultIfBlank(type, "all").toLowerCase();
        PageResult<BizPatient> page;
        if ("today".equals(t)) {
            Calendar cal = Calendar.getInstance();
            if (day != null) {
                cal.setTime(day);
            }
            clearTime(cal);
            Date begin = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date end = cal.getTime();
            // 今日：有今日预约 或 今日创建/最近就诊
            page = PageResult.of(patientMapper.selectPatientPage(
                    new Page<BizPatient>(pageNum, pageSize), clinicId, keyword, null, null, null));
            List<BizPatient> filtered = new ArrayList<BizPatient>();
            List<BizAppointment> appts = appointmentMapper.selectCalendarList(clinicId, begin, end, null, null);
            java.util.Set<Long> ids = new java.util.HashSet<Long>();
            if (appts != null) {
                for (BizAppointment a : appts) {
                    ids.add(a.getPatientId());
                }
            }
            if (page.getRecords() != null) {
                for (BizPatient p : page.getRecords()) {
                    boolean hit = ids.contains(p.getId());
                    if (!hit && p.getCreateTime() != null
                            && !p.getCreateTime().before(begin) && p.getCreateTime().before(end)) {
                        hit = true;
                    }
                    if (!hit && p.getLastVisitTime() != null
                            && !p.getLastVisitTime().before(begin) && p.getLastVisitTime().before(end)) {
                        hit = true;
                    }
                    if (hit) {
                        filtered.add(p);
                    }
                }
            }
            return PageResult.of(filtered.size(), pageNum, pageSize, filtered);
        }
        if ("recent".equals(t)) {
            LambdaQueryWrapper<BizPatient> wrapper = new LambdaQueryWrapper<BizPatient>()
                    .eq(clinicId != null, BizPatient::getClinicId, clinicId)
                    .and(StringUtils.isNotBlank(keyword), w -> w.like(BizPatient::getName, keyword)
                            .or().like(BizPatient::getMobile, keyword)
                            .or().like(BizPatient::getMedicalRecordNo, keyword))
                    .orderByDesc(BizPatient::getUpdateTime)
                    .orderByDesc(BizPatient::getId);
            Page<BizPatient> mp = patientMapper.selectPage(new Page<BizPatient>(pageNum, pageSize), wrapper);
            return PageResult.of(mp);
        }
        return PageResult.of(patientMapper.selectPatientPage(
                new Page<BizPatient>(pageNum, pageSize), clinicId, keyword, null, null, null));
    }

    @Override
    public Map<String, Object> profile(Long patientId) {
        BizPatient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new BusinessException("患者不存在");
        }
        patient.setTagIds(tagRelMapper.selectTagIdsByPatientId(patientId));
        fillDoctorNames(patient);

        Map<String, Object> cards = new HashMap<String, Object>(8);
        cards.put("referralCount", patient.getReferralCount() == null ? 0 : patient.getReferralCount());
        cards.put("prepayAmount", patient.getPrepayAmount());
        cards.put("lastVisitTime", patient.getLastVisitTime());
        cards.put("totalAmount", patient.getTotalAmount());
        cards.put("avgAmount", patient.getAvgAmount());
        cards.put("oweAmount", patient.getOweAmount());
        cards.put("paidAmount", patient.getPaidAmount());

        Map<String, Object> result = new HashMap<String, Object>(8);
        result.put("patient", patient);
        result.put("cards", cards);
        result.put("logs", patientLogMapper.selectList(
                new LambdaQueryWrapper<com.yujian.common.biz.domain.BizPatientLog>()
                        .eq(com.yujian.common.biz.domain.BizPatientLog::getPatientId, patientId)
                        .orderByDesc(com.yujian.common.biz.domain.BizPatientLog::getId)
                        .last("LIMIT 20")));
        return result;
    }

    @Override
    public List<Map<String, Object>> visitTimeline(Long patientId, Long clinicId,
                                                   Date beginTime, Date endTime) {
        List<BizAppointment> appts = appointmentMapper.selectAppointmentPage(
                new Page<BizAppointment>(1, 200),
                SecurityContextHolder.requireClinicId(clinicId),
                null, null, null, null, null, null, null, patientId, beginTime, endTime, null, null).getRecords();
        List<Map<String, Object>> timeline = new ArrayList<Map<String, Object>>();
        if (appts != null) {
            for (BizAppointment a : appts) {
                Map<String, Object> item = new HashMap<String, Object>(8);
                item.put("type", "appointment");
                item.put("bizId", a.getId());
                item.put("time", a.getStartTime());
                item.put("endTime", a.getEndTime());
                item.put("doctorName", a.getDoctorName());
                item.put("itemName", a.getItemName());
                item.put("status", a.getStatus());
                item.put("visitType", a.getVisitType());
                item.put("data", a);
                timeline.add(item);
            }
        }
        List<BizTreatmentRecord> treats = treatmentRecordMapper.selectList(
                new LambdaQueryWrapper<BizTreatmentRecord>()
                        .eq(BizTreatmentRecord::getPatientId, patientId)
                        .ge(beginTime != null, BizTreatmentRecord::getTreatTime, beginTime)
                        .le(endTime != null, BizTreatmentRecord::getTreatTime, endTime)
                        .orderByDesc(BizTreatmentRecord::getTreatTime));
        if (treats != null) {
            for (BizTreatmentRecord t : treats) {
                Map<String, Object> item = new HashMap<String, Object>(8);
                item.put("type", "treatment");
                item.put("bizId", t.getId());
                item.put("time", t.getTreatTime());
                item.put("itemName", t.getItemName());
                item.put("toothPositions", t.getToothPositions());
                item.put("visitType", t.getVisitType());
                item.put("data", t);
                timeline.add(item);
            }
        }
        Collections.sort(timeline, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Date d1 = (Date) o1.get("time");
                Date d2 = (Date) o2.get("time");
                if (d1 == null && d2 == null) {
                    return 0;
                }
                if (d1 == null) {
                    return 1;
                }
                if (d2 == null) {
                    return -1;
                }
                return d2.compareTo(d1);
            }
        });
        return timeline;
    }

    private void fillDoctorNames(BizPatient patient) {
        if (patient.getDoctorId() != null) {
            SysEmployee e = employeeMapper.selectById(patient.getDoctorId());
            if (e != null) {
                patient.setDoctorName(e.getName());
            }
        }
        if (patient.getFirstDoctorId() != null) {
            SysEmployee e = employeeMapper.selectById(patient.getFirstDoctorId());
            if (e != null) {
                patient.setFirstDoctorName(e.getName());
            }
        }
        if (patient.getLastDoctorId() != null) {
            SysEmployee e = employeeMapper.selectById(patient.getLastDoctorId());
            if (e != null) {
                patient.setLastDoctorName(e.getName());
            }
        }
    }

    private void clearTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
}
