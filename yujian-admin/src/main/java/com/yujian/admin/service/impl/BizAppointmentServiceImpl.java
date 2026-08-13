package com.yujian.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yujian.admin.mapper.BizAppointmentMapper;
import com.yujian.admin.mapper.BizPatientMapper;
import com.yujian.admin.mapper.BizTreatItemMapper;
import com.yujian.admin.service.IBizAppointmentService;
import com.yujian.common.biz.domain.BizAppointment;
import com.yujian.common.biz.domain.BizPatient;
import com.yujian.common.biz.domain.BizTreatItem;
import com.yujian.common.core.context.SecurityContextHolder;
import com.yujian.common.core.domain.PageResult;
import com.yujian.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BizAppointmentServiceImpl extends ServiceImpl<BizAppointmentMapper, BizAppointment>
        implements IBizAppointmentService {

    @Autowired
    private BizPatientMapper patientMapper;

    @Autowired
    private BizTreatItemMapper treatItemMapper;

    @Override
    public PageResult<BizAppointment> selectPage(String keyword, Long clinicId, Long doctorId, Long consultantId,
                                                 Integer visitType, Integer status,
                                                 Date beginTime, Date endTime,
                                                 long pageNum, long pageSize) {
        if (clinicId == null) {
            clinicId = SecurityContextHolder.getClinicId();
        }
        Page<BizAppointment> page = new Page<BizAppointment>(pageNum, pageSize);
        return PageResult.of(baseMapper.selectAppointmentPage(page, clinicId, keyword, doctorId, consultantId,
                visitType, status, beginTime, endTime));
    }

    @Override
    public List<BizAppointment> selectCalendar(Long clinicId, Date beginTime, Date endTime,
                                               Long doctorId, List<Integer> statusList) {
        if (clinicId == null) {
            clinicId = SecurityContextHolder.getClinicId();
        }
        return baseMapper.selectCalendarList(clinicId, beginTime, endTime, doctorId, statusList);
    }

    @Override
    public BizAppointment selectById(Long id) {
        BizAppointment appt = this.getById(id);
        if (appt == null) {
            return null;
        }
        List<BizAppointment> list = baseMapper.selectCalendarList(appt.getClinicId(),
                appt.getStartTime(), appt.getEndTime(), null, null);
        if (list != null) {
            for (BizAppointment item : list) {
                if (id.equals(item.getId())) {
                    return item;
                }
            }
        }
        return appt;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertAppointment(BizAppointment appointment) {
        validateAppointment(appointment);
        if (appointment.getClinicId() == null) {
            appointment.setClinicId(SecurityContextHolder.getClinicId());
        }
        if (appointment.getStatus() == null) {
            appointment.setStatus(BizAppointment.STATUS_BOOKED);
        }
        if (appointment.getVisitType() == null) {
            appointment.setVisitType(2);
        }
        if (appointment.getTriaged() == null) {
            appointment.setTriaged(0);
        }
        if (appointment.getRegistered() == null) {
            appointment.setRegistered(0);
        }
        fillItemName(appointment);
        boolean saved = this.save(appointment);
        syncPatientVisitTime(appointment);
        return saved ? 1 : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateAppointment(BizAppointment appointment) {
        validateAppointment(appointment);
        fillItemName(appointment);
        boolean updated = this.updateById(appointment);
        syncPatientVisitTime(appointment);
        return updated ? 1 : 0;
    }

    @Override
    public int deleteAppointment(Long id) {
        return this.removeById(id) ? 1 : 0;
    }

    @Override
    public int updateStatus(Long id, Integer status) {
        BizAppointment appt = this.getById(id);
        if (appt == null) {
            throw new BusinessException("预约不存在");
        }
        BizAppointment update = new BizAppointment();
        update.setId(id);
        update.setStatus(status);
        if (status != null && status == BizAppointment.STATUS_ARRIVED) {
            update.setRegistered(1);
        }
        if (status != null && (status == BizAppointment.STATUS_TREATING || status == BizAppointment.STATUS_ARRIVED)) {
            update.setTriaged(1);
        }
        return this.updateById(update) ? 1 : 0;
    }

    @Override
    public int seatPatient(Long id) {
        // 接诊入位：已到达 -> 治疗中
        return updateStatus(id, BizAppointment.STATUS_TREATING);
    }

    @Override
    public Map<String, Object> todayStats(Long clinicId) {
        if (clinicId == null) {
            clinicId = SecurityContextHolder.getClinicId();
        }
        if (clinicId == null) {
            throw new BusinessException("诊所ID不能为空");
        }
        Calendar cal = Calendar.getInstance();
        clearTime(cal);
        Date dayStart = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date dayEnd = cal.getTime();
        Map<String, Object> stats = baseMapper.selectTodayStats(clinicId, dayStart, dayEnd);
        if (stats == null) {
            stats = new HashMap<String, Object>();
        }
        return stats;
    }

    private void validateAppointment(BizAppointment appointment) {
        if (appointment.getPatientId() == null) {
            throw new BusinessException("患者不能为空");
        }
        if (appointment.getStartTime() == null || appointment.getEndTime() == null) {
            throw new BusinessException("预约时间不能为空");
        }
        if (appointment.getEndTime().before(appointment.getStartTime())) {
            throw new BusinessException("结束时间不能早于开始时间");
        }
        BizPatient patient = patientMapper.selectById(appointment.getPatientId());
        if (patient == null) {
            throw new BusinessException("患者不存在");
        }
    }

    private void fillItemName(BizAppointment appointment) {
        if (appointment.getItemId() != null && (appointment.getItemName() == null || appointment.getItemName().isEmpty())) {
            BizTreatItem item = treatItemMapper.selectById(appointment.getItemId());
            if (item != null) {
                appointment.setItemName(item.getItemName());
            }
        }
    }

    private void syncPatientVisitTime(BizAppointment appointment) {
        if (appointment.getPatientId() == null || appointment.getStartTime() == null) {
            return;
        }
        BizPatient patient = patientMapper.selectById(appointment.getPatientId());
        if (patient == null) {
            return;
        }
        BizPatient update = new BizPatient();
        update.setId(patient.getId());
        if (appointment.getVisitType() != null && appointment.getVisitType() == 1) {
            if (patient.getFirstVisitTime() == null) {
                update.setFirstVisitTime(appointment.getStartTime());
                update.setFirstDoctorId(appointment.getDoctorId());
            }
        } else {
            update.setNextVisitTime(appointment.getStartTime());
        }
        if (appointment.getDoctorId() != null && patient.getDoctorId() == null) {
            update.setDoctorId(appointment.getDoctorId());
        }
        patientMapper.updateById(update);
    }

    private void clearTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
}
