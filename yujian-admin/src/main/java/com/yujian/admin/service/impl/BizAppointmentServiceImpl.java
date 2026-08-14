package com.yujian.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yujian.admin.mapper.BizAppointmentLogMapper;
import com.yujian.admin.mapper.BizAppointmentMapper;
import com.yujian.admin.mapper.BizPatientMapper;
import com.yujian.admin.mapper.BizTreatItemMapper;
import com.yujian.admin.mapper.SysEmployeeMapper;
import com.yujian.admin.service.IBizAppointmentService;
import com.yujian.common.biz.domain.BizAppointment;
import com.yujian.common.biz.domain.BizAppointmentLog;
import com.yujian.common.biz.domain.BizPatient;
import com.yujian.common.biz.domain.BizTreatItem;
import com.yujian.common.core.context.SecurityContextHolder;
import com.yujian.common.core.domain.LoginUser;
import com.yujian.common.core.domain.PageResult;
import com.yujian.common.exception.BusinessException;
import com.yujian.common.system.domain.SysEmployee;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BizAppointmentServiceImpl extends ServiceImpl<BizAppointmentMapper, BizAppointment>
        implements IBizAppointmentService {

    @Autowired
    private BizPatientMapper patientMapper;

    @Autowired
    private BizTreatItemMapper treatItemMapper;

    @Autowired
    private BizAppointmentLogMapper logMapper;

    @Autowired
    private SysEmployeeMapper employeeMapper;

    @Override
    public PageResult<BizAppointment> selectPage(String keyword, Long clinicId, Long doctorId, Long consultantId,
                                                 Integer visitType, Integer status, String appointSource,
                                                 Date beginTime, Date endTime,
                                                 long pageNum, long pageSize) {
        clinicId = resolveClinicId(clinicId);
        Page<BizAppointment> page = new Page<BizAppointment>(pageNum, pageSize);
        return PageResult.of(baseMapper.selectAppointmentPage(page, clinicId, keyword, doctorId, consultantId,
                visitType, status, appointSource, null, beginTime, endTime));
    }

    @Override
    public List<BizAppointment> selectCalendar(Long clinicId, Date beginTime, Date endTime,
                                               Long doctorId, List<Integer> statusList) {
        return baseMapper.selectCalendarList(resolveClinicId(clinicId), beginTime, endTime, doctorId, statusList);
    }

    @Override
    public Map<String, Object> selectDayGrid(Long clinicId, Date day, List<Integer> statusList) {
        clinicId = resolveClinicId(clinicId);
        Calendar cal = Calendar.getInstance();
        cal.setTime(day == null ? new Date() : day);
        clearTime(cal);
        Date begin = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date end = cal.getTime();

        List<BizAppointment> list = baseMapper.selectCalendarList(clinicId, begin, end, null, statusList);
        List<SysEmployee> doctors = employeeMapper.selectList(new LambdaQueryWrapper<SysEmployee>()
                .eq(SysEmployee::getClinicId, clinicId)
                .eq(SysEmployee::getEmployStatus, 1)
                .eq(SysEmployee::getStatus, 0)
                .and(w -> w.like(SysEmployee::getPosition, "医生")
                        .or().like(SysEmployee::getPosition, "医师"))
                .orderByAsc(SysEmployee::getSortOrder));

        Map<Long, List<BizAppointment>> doctorMap = new LinkedHashMap<Long, List<BizAppointment>>();
        doctorMap.put(0L, new ArrayList<BizAppointment>());
        if (doctors != null) {
            for (SysEmployee d : doctors) {
                doctorMap.put(d.getId(), new ArrayList<BizAppointment>());
            }
        }
        if (list != null) {
            for (BizAppointment a : list) {
                Long did = a.getDoctorId() == null ? 0L : a.getDoctorId();
                if (!doctorMap.containsKey(did)) {
                    doctorMap.put(did, new ArrayList<BizAppointment>());
                }
                doctorMap.get(did).add(a);
            }
        }

        List<Map<String, Object>> columns = new ArrayList<Map<String, Object>>();
        Map<String, Object> unassigned = new HashMap<String, Object>(8);
        unassigned.put("doctorId", 0);
        unassigned.put("doctorName", "未指定医生");
        unassigned.put("count", doctorMap.get(0L).size());
        unassigned.put("appointments", doctorMap.get(0L));
        columns.add(unassigned);

        if (doctors != null) {
            for (SysEmployee d : doctors) {
                Map<String, Object> col = new HashMap<String, Object>(8);
                col.put("doctorId", d.getId());
                col.put("doctorName", d.getName());
                List<BizAppointment> appts = doctorMap.get(d.getId());
                col.put("count", appts == null ? 0 : appts.size());
                col.put("appointments", appts == null ? new ArrayList<BizAppointment>() : appts);
                columns.add(col);
            }
        }

        Map<String, Object> result = new HashMap<String, Object>(4);
        result.put("day", begin);
        result.put("columns", columns);
        result.put("total", list == null ? 0 : list.size());
        return result;
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
        appointment.setClinicId(resolveClinicId(appointment.getClinicId()));
        checkConflict(appointment);
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
        if (StringUtils.isBlank(appointment.getAppointType())) {
            appointment.setAppointType("normal");
        }
        if (StringUtils.isBlank(appointment.getAppointSource())) {
            appointment.setAppointSource("clinic");
        }
        LoginUser user = SecurityContextHolder.getLoginUser();
        if (user != null && StringUtils.isBlank(appointment.getCreatorName())) {
            appointment.setCreatorName(user.getName());
        }
        fillItemName(appointment);
        boolean saved = this.save(appointment);
        syncPatientVisitTime(appointment);
        writeLog(appointment.getId(), appointment.getClinicId(), "create", null, appointment.getStatus(), "新增预约");
        return saved ? 1 : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateAppointment(BizAppointment appointment) {
        validateAppointment(appointment);
        checkConflict(appointment);
        fillItemName(appointment);
        BizAppointment old = this.getById(appointment.getId());
        boolean updated = this.updateById(appointment);
        syncPatientVisitTime(appointment);
        writeLog(appointment.getId(), appointment.getClinicId(), "update",
                old == null ? null : old.getStatus(), appointment.getStatus(), "修改预约");
        return updated ? 1 : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAppointment(Long id, String cancelReason) {
        BizAppointment appt = this.getById(id);
        if (appt == null) {
            throw new BusinessException("预约不存在");
        }
        BizAppointment update = new BizAppointment();
        update.setId(id);
        update.setCancelReason(cancelReason);
        this.updateById(update);
        boolean removed = this.removeById(id);
        writeLog(id, appt.getClinicId(), "delete", appt.getStatus(), appt.getStatus(),
                "删除进回收站:" + StringUtils.defaultString(cancelReason));
        return removed ? 1 : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(Long id, Integer status, String remark) {
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
        boolean ok = this.updateById(update);
        writeLog(id, appt.getClinicId(), "status", appt.getStatus(), status,
                StringUtils.isBlank(remark) ? "变更状态" : remark);
        return ok ? 1 : 0;
    }

    @Override
    public int confirm(Long id) {
        return updateStatus(id, BizAppointment.STATUS_CONFIRMED, "确认预约");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancel(Long id, String cancelReason) {
        BizAppointment appt = this.getById(id);
        if (appt == null) {
            throw new BusinessException("预约不存在");
        }
        BizAppointment update = new BizAppointment();
        update.setId(id);
        update.setStatus(BizAppointment.STATUS_LOST);
        update.setCancelReason(cancelReason);
        boolean ok = this.updateById(update);
        writeLog(id, appt.getClinicId(), "cancel", appt.getStatus(), BizAppointment.STATUS_LOST,
                "取消预约:" + StringUtils.defaultString(cancelReason));
        return ok ? 1 : 0;
    }

    @Override
    public int seatPatient(Long id) {
        return updateStatus(id, BizAppointment.STATUS_TREATING, "接诊入位");
    }

    @Override
    public Map<String, Object> todayStats(Long clinicId) {
        clinicId = resolveClinicId(clinicId);
        if (clinicId == null) {
            throw new BusinessException("诊所ID不能为空");
        }
        Calendar cal = Calendar.getInstance();
        clearTime(cal);
        Date dayStart = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date dayEnd = cal.getTime();
        Map<String, Object> stats = baseMapper.selectTodayStats(clinicId, dayStart, dayEnd);
        return stats == null ? new HashMap<String, Object>() : stats;
    }

    @Override
    public Map<String, Object> statusCount(Long clinicId, Date beginTime, Date endTime, Long doctorId) {
        clinicId = resolveClinicId(clinicId);
        List<Map<String, Object>> rows = baseMapper.selectStatusCount(clinicId, beginTime, endTime, doctorId);
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        long total = 0;
        Map<Integer, Long> byStatus = new HashMap<Integer, Long>();
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                Object st = row.get("status");
                Object cnt = row.get("cnt");
                if (st == null || cnt == null) {
                    continue;
                }
                int status = Integer.parseInt(String.valueOf(st));
                long c = Long.parseLong(String.valueOf(cnt));
                byStatus.put(status, c);
                total += c;
            }
        }
        result.put("all", total);
        result.put("booked", byStatus.getOrDefault(1, 0L));
        result.put("confirmed", byStatus.getOrDefault(2, 0L));
        result.put("arrived", byStatus.getOrDefault(3, 0L));
        result.put("treating", byStatus.getOrDefault(4, 0L));
        result.put("left", byStatus.getOrDefault(5, 0L));
        result.put("expired", byStatus.getOrDefault(6, 0L));
        result.put("lost", byStatus.getOrDefault(7, 0L));
        result.put("missed", byStatus.getOrDefault(8, 0L));
        result.put("byStatus", byStatus);
        return result;
    }

    @Override
    public PageResult<BizAppointment> selectRecyclePage(String keyword, Long clinicId, Long doctorId, Long consultantId,
                                                        Date beginTime, Date endTime, long pageNum, long pageSize) {
        clinicId = resolveClinicId(clinicId);
        Page<BizAppointment> page = new Page<BizAppointment>(pageNum, pageSize);
        return PageResult.of(baseMapper.selectRecyclePage(page, clinicId, keyword, doctorId, consultantId, beginTime, endTime));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int restore(Long id) {
        int rows = baseMapper.restoreById(id);
        if (rows > 0) {
            writeLog(id, SecurityContextHolder.getClinicId(), "restore", null, null, "从回收站还原");
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int permanentDelete(Long id) {
        logMapper.delete(new LambdaQueryWrapper<BizAppointmentLog>().eq(BizAppointmentLog::getAppointmentId, id));
        int rows = baseMapper.permanentDeleteById(id);
        return rows;
    }

    @Override
    public List<BizAppointmentLog> selectLogs(Long appointmentId) {
        return logMapper.selectList(new LambdaQueryWrapper<BizAppointmentLog>()
                .eq(BizAppointmentLog::getAppointmentId, appointmentId)
                .orderByDesc(BizAppointmentLog::getId));
    }

    private void checkConflict(BizAppointment appointment) {
        if (appointment.getDoctorId() == null) {
            return;
        }
        Long clinicId = resolveClinicId(appointment.getClinicId());
        int count = baseMapper.countDoctorConflict(clinicId, appointment.getDoctorId(),
                appointment.getStartTime(), appointment.getEndTime(), appointment.getId());
        if (count > 0) {
            throw new BusinessException("该医生在此时间段已有预约，请调整时间");
        }
    }

    private void validateAppointment(BizAppointment appointment) {
        if (appointment.getPatientId() == null) {
            throw new BusinessException("患者不能为空");
        }
        if (appointment.getStartTime() == null || appointment.getEndTime() == null) {
            throw new BusinessException("预约时间不能为空");
        }
        if (!appointment.getEndTime().after(appointment.getStartTime())) {
            throw new BusinessException("结束时间必须晚于开始时间");
        }
        BizPatient patient = patientMapper.selectById(appointment.getPatientId());
        if (patient == null) {
            throw new BusinessException("患者不存在");
        }
    }

    private void fillItemName(BizAppointment appointment) {
        if (appointment.getItemId() == null) {
            return;
        }
        BizTreatItem item = treatItemMapper.selectById(appointment.getItemId());
        if (item == null) {
            return;
        }
        if (StringUtils.isBlank(appointment.getItemName())) {
            appointment.setItemName(item.getItemName());
        }
        if (StringUtils.isBlank(appointment.getItemColor())) {
            appointment.setItemColor(item.getItemColor());
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

    private void writeLog(Long appointmentId, Long clinicId, String action,
                          Integer before, Integer after, String content) {
        BizAppointmentLog log = new BizAppointmentLog();
        log.setAppointmentId(appointmentId);
        log.setClinicId(clinicId);
        log.setAction(action);
        log.setBeforeStatus(before);
        log.setAfterStatus(after);
        log.setContent(content);
        LoginUser user = SecurityContextHolder.getLoginUser();
        if (user != null) {
            log.setOperatorId(user.getUserId());
            log.setOperatorName(user.getName());
        }
        log.setCreateTime(new Date());
        logMapper.insert(log);
    }

    private Long resolveClinicId(Long clinicId) {
        return clinicId != null ? clinicId : SecurityContextHolder.getClinicId();
    }

    private void clearTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
}
