package com.yujian.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yujian.admin.mapper.BizAppointmentItemMapper;
import com.yujian.admin.mapper.BizAppointmentLogMapper;
import com.yujian.admin.mapper.BizAppointmentMapper;
import com.yujian.admin.mapper.BizPatientMapper;
import com.yujian.admin.mapper.BizTreatItemMapper;
import com.yujian.admin.mapper.SysEmployeeMapper;
import com.yujian.admin.service.IBizAppointmentService;
import com.yujian.common.biz.domain.BizAppointment;
import com.yujian.common.biz.domain.BizAppointmentItem;
import com.yujian.common.biz.domain.BizAppointmentLog;
import com.yujian.common.biz.domain.BizPatient;
import com.yujian.common.biz.domain.BizTreatItem;
import com.yujian.common.core.context.SecurityContextHolder;
import com.yujian.common.core.domain.LoginUser;
import com.yujian.common.core.domain.PageResult;
import com.yujian.common.exception.BusinessException;
import com.yujian.common.system.domain.SysEmployee;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约管理服务实现。
 * <p>
 * 查询侧（列表/天视图/回收站）clinicId 在授权范围内生效；
 * 按 ID 读写须校验预约所属诊所在账号授权列表内，防止跨店越权。
 * </p>
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Service
public class BizAppointmentServiceImpl extends ServiceImpl<BizAppointmentMapper, BizAppointment>
        implements IBizAppointmentService {

    private static final Logger log = LoggerFactory.getLogger(BizAppointmentServiceImpl.class);

    /** 列表单页最大条数，防止过大 pageSize 拖垮库 */
    private static final int MAX_PAGE_SIZE = 100;

    /** 一次预约最多勾选项目数 */
    private static final int MAX_ITEM_COUNT = 20;

    /** 关键字最大长度 */
    private static final int MAX_KEYWORD_LEN = 64;

    /** 清空回收站时每批物理删除条数 */
    private static final int CLEAR_BATCH_SIZE = 500;

    @Autowired
    private BizPatientMapper patientMapper;

    @Autowired
    private BizTreatItemMapper treatItemMapper;

    @Autowired
    private BizAppointmentItemMapper appointmentItemMapper;

    @Autowired
    private BizAppointmentLogMapper logMapper;

    @Autowired
    private SysEmployeeMapper employeeMapper;

    @Autowired
    private com.yujian.admin.mapper.SysEmployeeClinicMapper employeeClinicMapper;

    /**
     * 预约分页列表；clinicId 授权生效，反参补 clinicName / items
     */
    @Override
    public PageResult<BizAppointment> selectPage(String keyword, Long clinicId, Long doctorId, Long consultantId,
                                                 Integer visitType, List<Integer> statusList, String appointType,
                                                 String appointSource, Date beginTime, Date endTime,
                                                 Date createBeginTime, Date createEndTime,
                                                 long pageNum, long pageSize) {
        clinicId = resolveQueryClinicId(clinicId);
        keyword = normalizeKeyword(keyword);
        pageSize = normalizePageSize(pageSize);
        if (pageNum < 1L) {
            pageNum = 1L;
        }
        if (StringUtils.isNotBlank(appointType)) {
            validateAppointType(appointType.trim());
        }
        if (StringUtils.isNotBlank(appointSource)) {
            validateAppointSource(appointSource.trim());
        }
        log.info("【预约】列表查询, clinicId={}, appointType={}, statusList={}", clinicId, appointType, statusList);
        Page<BizAppointment> page = new Page<BizAppointment>(pageNum, pageSize);
        PageResult<BizAppointment> result = PageResult.of(baseMapper.selectAppointmentPage(
                page, clinicId, keyword, doctorId, consultantId, visitType, statusList,
                appointType, appointSource, null, beginTime, endTime, createBeginTime, createEndTime));
        if (result != null && result.getRecords() != null) {
            fillAppointmentItems(result.getRecords());
        }
        return result;
    }

    /**
     * 周/月日历扁平列表；clinicId 授权生效（与列表一致）
     */
    @Override
    public List<BizAppointment> selectCalendar(Long clinicId, Date beginTime, Date endTime,
                                               Long doctorId, List<Integer> statusList) {
        clinicId = resolveQueryClinicId(clinicId);
        List<BizAppointment> list = baseMapper.selectCalendarList(clinicId, beginTime, endTime, doctorId, statusList);
        fillAppointmentItems(list);
        return list;
    }

    @Override
    public Map<String, Object> selectDayGrid(Long clinicId, Date day, List<Integer> statusList, List<Long> doctorIds) {
        // 查询场景：授权诊所内可查看（医生查询 Tab 选其他诊所）
        clinicId = resolveQueryClinicId(clinicId);
        log.info("【预约】天视图查询, clinicId={}, day={}, doctorIds={}", clinicId, day, doctorIds);
        Calendar cal = Calendar.getInstance();
        cal.setTime(day == null ? new Date() : day);
        clearTime(cal);
        Date begin = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date end = cal.getTime();

        List<BizAppointment> list = baseMapper.selectCalendarList(clinicId, begin, end, null, statusList);
        fillAppointmentItems(list);
        List<Long> employeeIds = employeeClinicMapper.selectEmployeeIdsByClinicId(clinicId);
        List<SysEmployee> doctors = new ArrayList<SysEmployee>();
        if (employeeIds != null && !employeeIds.isEmpty()) {
            doctors = employeeMapper.selectList(new LambdaQueryWrapper<SysEmployee>()
                    .in(SysEmployee::getId, employeeIds)
                    .eq(SysEmployee::getEmployStatus, 1)
                    .eq(SysEmployee::getStatus, 0)
                    .and(w -> w.like(SysEmployee::getPosition, "医生")
                            .or().like(SysEmployee::getPosition, "医师"))
                    .orderByAsc(SysEmployee::getSortOrder));
        }

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

        // 是否按 doctorIds 过滤列（含 0=未指定医生）
        HashSet<Long> doctorIdFilter = null;
        if (doctorIds != null && !doctorIds.isEmpty()) {
            doctorIdFilter = new HashSet<Long>(doctorIds);
        }

        List<Map<String, Object>> columns = new ArrayList<Map<String, Object>>();
        if (doctorIdFilter == null || doctorIdFilter.contains(0L)) {
            Map<String, Object> unassigned = new HashMap<String, Object>(8);
            unassigned.put("doctorId", 0);
            unassigned.put("doctorName", "未指定医生");
            unassigned.put("count", doctorMap.get(0L).size());
            unassigned.put("appointments", doctorMap.get(0L));
            columns.add(unassigned);
        }

        if (doctors != null) {
            for (SysEmployee d : doctors) {
                if (doctorIdFilter != null && !doctorIdFilter.contains(d.getId())) {
                    continue;
                }
                Map<String, Object> col = new HashMap<String, Object>(8);
                col.put("doctorId", d.getId());
                col.put("doctorName", d.getName());
                List<BizAppointment> appts = doctorMap.get(d.getId());
                col.put("count", appts == null ? 0 : appts.size());
                col.put("appointments", appts == null ? new ArrayList<BizAppointment>() : appts);
                columns.add(col);
            }
        }

        // total：与筛选后列内预约合计一致（若传 doctorIds 则只计返回列）
        int total = 0;
        for (Map<String, Object> col : columns) {
            Object c = col.get("count");
            if (c instanceof Number) {
                total += ((Number) c).intValue();
            }
        }

        Map<String, Object> result = new HashMap<String, Object>(4);
        result.put("day", begin);
        result.put("columns", columns);
        result.put("total", total);
        log.info("【预约】天视图完成, clinicId={}, columns={}, total={}", clinicId, columns.size(), total);
        return result;
    }

    /**
     * 预约详情；校验所属诊所在账号授权范围内
     *
     * @param id 预约ID
     * @return 详情（含 items），不存在返回 null
     */
    @Override
    public BizAppointment selectById(Long id) {
        if (id == null) {
            return null;
        }
        BizAppointment appt = this.getById(id);
        if (appt == null) {
            return null;
        }
        // 越权防护：禁止查看未授权诊所的预约
        assertClinicAuthorized(appt.getClinicId());
        List<BizAppointment> list = baseMapper.selectCalendarList(appt.getClinicId(),
                appt.getStartTime(), appt.getEndTime(), null, null);
        if (list != null) {
            for (BizAppointment item : list) {
                if (id.equals(item.getId())) {
                    fillAppointmentItems(Collections.singletonList(item));
                    return item;
                }
            }
        }
        fillAppointmentItems(Collections.singletonList(appt));
        return appt;
    }

    /**
     * 新建预约；clinicId 授权生效，患者/项目按目标诊所校验
     *
     * @param appointment 预约入参
     * @return 1 成功 / 0 失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertAppointment(BizAppointment appointment) {
        if (appointment == null) {
            throw new BusinessException("预约信息不能为空");
        }
        // 新建：clinicId 在授权范围内生效（弹窗「预约门诊」可选非会话店）
        Long clinicId = SecurityContextHolder.resolveAuthorizedClinicId(appointment.getClinicId());
        appointment.setClinicId(clinicId);
        log.info("【预约】新增, clinicId={}, patientId={}, doctorId={}",
                clinicId, appointment.getPatientId(), appointment.getDoctorId());
        validateAppointment(appointment, null, true);
        applyItemFields(appointment);
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
            appointment.setAppointType(BizAppointment.APPOINT_TYPE_NORMAL);
        } else {
            validateAppointType(appointment.getAppointType().trim());
            appointment.setAppointType(appointment.getAppointType().trim());
        }
        if (StringUtils.isBlank(appointment.getAppointSource())) {
            appointment.setAppointSource(BizAppointment.APPOINT_SOURCE_CLINIC);
        } else {
            validateAppointSource(appointment.getAppointSource().trim());
            appointment.setAppointSource(appointment.getAppointSource().trim());
        }
        LoginUser user = SecurityContextHolder.getLoginUser();
        if (user != null && StringUtils.isBlank(appointment.getCreatorName())) {
            appointment.setCreatorName(user.getName());
        }
        boolean saved = this.save(appointment);
        replaceAppointmentItems(appointment);
        syncPatientVisitTime(appointment);
        writeLog(appointment.getId(), appointment.getClinicId(), "create", null, appointment.getStatus(), "新增预约");
        return saved ? 1 : 0;
    }

    /**
     * 修改预约；禁止改诊所；历史单可改备注等，改期新日期不得早于今天
     *
     * @param appointment 须含 id
     * @return 1 成功 / 0 失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateAppointment(BizAppointment appointment) {
        if (appointment == null || appointment.getId() == null) {
            throw new BusinessException("预约ID不能为空");
        }
        BizAppointment old = this.getById(appointment.getId());
        if (old == null) {
            throw new BusinessException("预约不存在");
        }
        // 越权防护 + 修改不改诊所归属
        assertClinicAuthorized(old.getClinicId());
        if (appointment.getClinicId() == null) {
            appointment.setClinicId(old.getClinicId());
        } else if (!old.getClinicId().equals(appointment.getClinicId())) {
            throw new BusinessException("不允许修改预约所属诊所");
        }
        validateAppointment(appointment, old.getStartTime(), false);
        applyItemFields(appointment);
        checkConflict(appointment);
        boolean updated = this.updateById(appointment);
        // 显式清空项目时，updateById 默认忽略 null，需额外 SET NULL
        if (appointment.getItemIds() != null && appointment.getItemIds().isEmpty()) {
            this.update(new LambdaUpdateWrapper<BizAppointment>()
                    .eq(BizAppointment::getId, appointment.getId())
                    .set(BizAppointment::getItemId, null)
                    .set(BizAppointment::getItemName, null)
                    .set(BizAppointment::getItemColor, null));
            replaceAppointmentItems(appointment);
        } else if (appointment.getItemIds() != null || appointment.getItemId() != null) {
            replaceAppointmentItems(appointment);
        }
        syncPatientVisitTime(appointment);
        writeLog(appointment.getId(), appointment.getClinicId(), "update",
                old.getStatus(), appointment.getStatus() != null ? appointment.getStatus() : old.getStatus(), "修改预约");
        return updated ? 1 : 0;
    }

    /**
     * 软删除进回收站
     *
     * @param id           预约ID
     * @param cancelReason 原因
     * @return 1 成功 / 0 失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAppointment(Long id, String cancelReason) {
        BizAppointment appt = requireAuthorizedAppointment(id);
        BizAppointment update = new BizAppointment();
        update.setId(id);
        update.setCancelReason(cancelReason);
        this.updateById(update);
        boolean removed = this.removeById(id);
        writeLog(id, appt.getClinicId(), "delete", appt.getStatus(), appt.getStatus(),
                "删除进回收站:" + StringUtils.defaultString(cancelReason));
        return removed ? 1 : 0;
    }

    /**
     * 变更预约状态
     *
     * @param id     预约ID
     * @param status 目标状态 1~8
     * @param remark 备注
     * @return 1 成功 / 0 失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateStatus(Long id, Integer status, String remark) {
        BizAppointment appt = requireAuthorizedAppointment(id);
        if (status == null || status < BizAppointment.STATUS_BOOKED || status > BizAppointment.STATUS_MISSED) {
            throw new BusinessException("预约状态不正确");
        }
        BizAppointment update = new BizAppointment();
        update.setId(id);
        update.setStatus(status);
        if (status == BizAppointment.STATUS_ARRIVED) {
            update.setRegistered(1);
        }
        if (status == BizAppointment.STATUS_TREATING || status == BizAppointment.STATUS_ARRIVED) {
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

    /**
     * 取消预约（状态改为已流失）
     *
     * @param id           预约ID
     * @param cancelReason 取消原因
     * @return 1 成功 / 0 失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancel(Long id, String cancelReason) {
        BizAppointment appt = requireAuthorizedAppointment(id);
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
        // 与 dayGrid 同一诊所范围，避免左侧状态数与网格错位
        clinicId = resolveQueryClinicId(clinicId);
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

    /**
     * 回收站分页；clinicId 授权生效
     */
    @Override
    public PageResult<BizAppointment> selectRecyclePage(String keyword, Long clinicId, Long doctorId, Long consultantId,
                                                        Date beginTime, Date endTime, long pageNum, long pageSize) {
        clinicId = resolveQueryClinicId(clinicId);
        keyword = normalizeKeyword(keyword);
        pageSize = normalizePageSize(pageSize);
        if (pageNum < 1L) {
            pageNum = 1L;
        }
        log.info("【预约】回收站列表, clinicId={}", clinicId);
        Page<BizAppointment> page = new Page<BizAppointment>(pageNum, pageSize);
        PageResult<BizAppointment> result = PageResult.of(
                baseMapper.selectRecyclePage(page, clinicId, keyword, doctorId, consultantId, beginTime, endTime));
        if (result != null && result.getRecords() != null) {
            fillAppointmentItems(result.getRecords());
        }
        return result;
    }

    /**
     * 从回收站还原；校验诊所授权
     *
     * @param id 预约ID
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int restore(Long id) {
        BizAppointment appt = requireAuthorizedAppointmentIgnoreDelete(id);
        if (appt.getIsDelete() == null || appt.getIsDelete() != 1) {
            throw new BusinessException("预约不在回收站中");
        }
        int rows = baseMapper.restoreById(id);
        if (rows > 0) {
            writeLog(id, appt.getClinicId(), "restore", null, null, "从回收站还原");
        }
        return rows;
    }

    /**
     * 彻底删除单条回收站预约；校验诊所授权
     *
     * @param id 预约ID
     * @return 影响行数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int permanentDelete(Long id) {
        BizAppointment appt = requireAuthorizedAppointmentIgnoreDelete(id);
        if (appt.getIsDelete() == null || appt.getIsDelete() != 1) {
            throw new BusinessException("仅可彻底删除回收站中的预约");
        }
        appointmentItemMapper.delete(new LambdaQueryWrapper<BizAppointmentItem>()
                .eq(BizAppointmentItem::getAppointmentId, id));
        logMapper.delete(new LambdaQueryWrapper<BizAppointmentLog>().eq(BizAppointmentLog::getAppointmentId, id));
        return baseMapper.permanentDeleteById(id);
    }

    /**
     * 清空指定授权诊所回收站；可选按预约开始时间过滤；分批物理删除
     *
     * @param clinicId  授权诊所
     * @param beginTime 预约开始时间起
     * @param endTime   预约开始时间止
     * @return 物理删除条数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int clearRecycle(Long clinicId, Date beginTime, Date endTime) {
        clinicId = resolveQueryClinicId(clinicId);
        log.info("【预约】清空回收站, clinicId={}, beginTime={}, endTime={}", clinicId, beginTime, endTime);
        List<Long> ids = baseMapper.selectRecycleIds(clinicId, beginTime, endTime);
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        int total = 0;
        // 分批删除，避免一次 IN 过大
        for (int from = 0; from < ids.size(); from += CLEAR_BATCH_SIZE) {
            int to = Math.min(from + CLEAR_BATCH_SIZE, ids.size());
            List<Long> batch = new ArrayList<Long>(ids.subList(from, to));
            appointmentItemMapper.delete(new LambdaQueryWrapper<BizAppointmentItem>()
                    .in(BizAppointmentItem::getAppointmentId, batch));
            logMapper.delete(new LambdaQueryWrapper<BizAppointmentLog>()
                    .in(BizAppointmentLog::getAppointmentId, batch));
            total += baseMapper.permanentDeleteByIds(batch);
        }
        log.info("【预约】清空回收站完成, clinicId={}, deleted={}", clinicId, total);
        return total;
    }

    /**
     * 操作日志；先校验预约所属诊所授权
     *
     * @param appointmentId 预约ID
     * @return 日志列表
     */
    @Override
    public List<BizAppointmentLog> selectLogs(Long appointmentId) {
        // 含回收站记录也可查日志
        requireAuthorizedAppointmentIgnoreDelete(appointmentId);
        return logMapper.selectList(new LambdaQueryWrapper<BizAppointmentLog>()
                .eq(BizAppointmentLog::getAppointmentId, appointmentId)
                .orderByDesc(BizAppointmentLog::getId));
    }

    /**
     * 医生时段冲突检测（排除已过期/流失/未到）
     *
     * @param appointment 预约
     */
    private void checkConflict(BizAppointment appointment) {
        if (appointment.getDoctorId() == null) {
            return;
        }
        Long clinicId = appointment.getClinicId();
        if (clinicId == null) {
            clinicId = SecurityContextHolder.resolveAuthorizedClinicId(null);
        }
        int count = baseMapper.countDoctorConflict(clinicId, appointment.getDoctorId(),
                appointment.getStartTime(), appointment.getEndTime(), appointment.getId());
        if (count > 0) {
            throw new BusinessException("该医生在此时间段已有预约，请调整时间");
        }
    }

    /**
     * 校验预约必填、时间、类型、患者归属；新建强制预约日≥今天，修改仅在改期时校验
     *
     * @param appointment 预约（须已写入目标 clinicId）
     * @param oldStartTime 修改前开始时间，新建传 null
     * @param isCreate     是否新建
     */
    private void validateAppointment(BizAppointment appointment, Date oldStartTime, boolean isCreate) {
        if (appointment.getPatientId() == null) {
            throw new BusinessException("患者不能为空");
        }
        if (appointment.getStartTime() == null || appointment.getEndTime() == null) {
            throw new BusinessException("预约时间不能为空");
        }
        if (!appointment.getEndTime().after(appointment.getStartTime())) {
            throw new BusinessException("结束时间必须晚于开始时间");
        }
        Integer visitType = appointment.getVisitType();
        if (visitType != null && visitType != 1 && visitType != 2 && visitType != 3) {
            throw new BusinessException("就诊类型不正确");
        }
        if (StringUtils.isNotBlank(appointment.getAppointType())) {
            validateAppointType(appointment.getAppointType().trim());
        }
        if (StringUtils.isNotBlank(appointment.getAppointSource())) {
            validateAppointSource(appointment.getAppointSource().trim());
        }
        // 预约日校验：新建必须 ≥ 今天；修改时仅当改到「另一天」且该天早于今天才拦截（允许编辑历史单备注等）
        Calendar today = Calendar.getInstance();
        clearTime(today);
        Calendar startDay = Calendar.getInstance();
        startDay.setTime(appointment.getStartTime());
        clearTime(startDay);
        if (isCreate) {
            if (startDay.before(today)) {
                throw new BusinessException("预约日期不能小于今天");
            }
        } else if (oldStartTime != null) {
            Calendar oldDay = Calendar.getInstance();
            oldDay.setTime(oldStartTime);
            clearTime(oldDay);
            boolean dayChanged = startDay.get(Calendar.YEAR) != oldDay.get(Calendar.YEAR)
                    || startDay.get(Calendar.DAY_OF_YEAR) != oldDay.get(Calendar.DAY_OF_YEAR);
            if (dayChanged && startDay.before(today)) {
                throw new BusinessException("预约日期不能小于今天");
            }
        }
        BizPatient patient = patientMapper.selectById(appointment.getPatientId());
        if (patient == null) {
            throw new BusinessException("患者不存在");
        }
        Long clinicId = appointment.getClinicId();
        if (clinicId == null) {
            clinicId = SecurityContextHolder.resolveAuthorizedClinicId(null);
        }
        if (patient.getClinicId() != null && !clinicId.equals(patient.getClinicId())) {
            throw new BusinessException("患者不属于所选诊所");
        }
    }

    /**
     * 解析 itemIds / itemId，校验项目归属诊所，并回填主表首项名称/颜色
     *
     * @param appointment 预约
     */
    private void applyItemFields(BizAppointment appointment) {
        // 显式传空数组：清空项目
        if (appointment.getItemIds() != null && appointment.getItemIds().isEmpty()) {
            appointment.setItemId(null);
            appointment.setItemName(null);
            appointment.setItemColor(null);
            appointment.setItems(new ArrayList<BizAppointmentItem>());
            return;
        }
        List<Long> itemIds = resolveItemIdList(appointment);
        if (itemIds.isEmpty()) {
            return;
        }
        if (itemIds.size() > MAX_ITEM_COUNT) {
            throw new BusinessException("预约项目最多选择" + MAX_ITEM_COUNT + "个");
        }
        Long clinicId = appointment.getClinicId();
        List<BizTreatItem> treatItems = new ArrayList<BizTreatItem>();
        for (Long itemId : itemIds) {
            BizTreatItem item = treatItemMapper.selectById(itemId);
            if (item == null || (item.getStatus() != null && item.getStatus() != 0)) {
                throw new BusinessException("预约项目不存在或已停用");
            }
            if (clinicId != null && item.getClinicId() != null && !clinicId.equals(item.getClinicId())) {
                throw new BusinessException("预约项目不属于所选诊所");
            }
            treatItems.add(item);
        }
        BizTreatItem first = treatItems.get(0);
        appointment.setItemId(first.getId());
        if (StringUtils.isBlank(appointment.getItemName())) {
            appointment.setItemName(first.getItemName());
        }
        if (StringUtils.isBlank(appointment.getItemColor())) {
            appointment.setItemColor(first.getItemColor());
        }
        // 暂存已解析明细，写入明细表时复用
        List<BizAppointmentItem> rows = new ArrayList<BizAppointmentItem>();
        for (int i = 0; i < treatItems.size(); i++) {
            BizTreatItem t = treatItems.get(i);
            BizAppointmentItem row = new BizAppointmentItem();
            row.setItemId(t.getId());
            row.setItemName(t.getItemName());
            row.setDuration(t.getDuration() == null ? 30 : t.getDuration());
            row.setSortOrder(i);
            rows.add(row);
        }
        appointment.setItems(rows);
        appointment.setItemIds(itemIds);
    }

    /**
     * 解析入参项目列表：优先 itemIds，否则兼容单个 itemId
     *
     * @param appointment 预约
     * @return 项目 ID 列表（去空）
     */
    private List<Long> resolveItemIdList(BizAppointment appointment) {
        List<Long> result = new ArrayList<Long>();
        if (appointment.getItemIds() != null && !appointment.getItemIds().isEmpty()) {
            for (Long id : appointment.getItemIds()) {
                if (id != null && !result.contains(id)) {
                    result.add(id);
                }
            }
            return result;
        }
        if (appointment.getItemId() != null) {
            result.add(appointment.getItemId());
        }
        return result;
    }

    /**
     * 全量覆盖预约项目明细表（须在预约主键已生成后调用）
     *
     * @param appointment 预约（含已解析的 items）
     */
    private void replaceAppointmentItems(BizAppointment appointment) {
        if (appointment.getId() == null) {
            return;
        }
        appointmentItemMapper.delete(new LambdaQueryWrapper<BizAppointmentItem>()
                .eq(BizAppointmentItem::getAppointmentId, appointment.getId()));
        List<BizAppointmentItem> rows = appointment.getItems();
        if (rows == null || rows.isEmpty()) {
            // 仅传了空 itemIds：清空明细即可
            if (appointment.getItemIds() != null && appointment.getItemIds().isEmpty()) {
                appointment.setItemId(null);
                appointment.setItemName(null);
                appointment.setItemColor(null);
            }
            return;
        }
        Date now = new Date();
        for (BizAppointmentItem row : rows) {
            row.setId(null);
            row.setAppointmentId(appointment.getId());
            if (row.getCreateTime() == null) {
                row.setCreateTime(now);
            }
            appointmentItemMapper.insert(row);
        }
    }

    /**
     * 批量回填预约项目明细（详情 / dayGrid）；无明细时用主表首项兜底
     *
     * @param list 预约列表
     */
    private void fillAppointmentItems(List<BizAppointment> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        List<Long> apptIds = new ArrayList<Long>();
        for (BizAppointment a : list) {
            if (a != null && a.getId() != null) {
                apptIds.add(a.getId());
            }
        }
        if (apptIds.isEmpty()) {
            return;
        }
        List<BizAppointmentItem> allItems = appointmentItemMapper.selectList(
                new LambdaQueryWrapper<BizAppointmentItem>()
                        .in(BizAppointmentItem::getAppointmentId, apptIds)
                        .orderByAsc(BizAppointmentItem::getSortOrder)
                        .orderByAsc(BizAppointmentItem::getId));
        Map<Long, List<BizAppointmentItem>> map = new HashMap<Long, List<BizAppointmentItem>>();
        if (allItems != null) {
            for (BizAppointmentItem item : allItems) {
                List<BizAppointmentItem> bucket = map.get(item.getAppointmentId());
                if (bucket == null) {
                    bucket = new ArrayList<BizAppointmentItem>();
                    map.put(item.getAppointmentId(), bucket);
                }
                bucket.add(item);
            }
        }
        for (BizAppointment a : list) {
            if (a == null || a.getId() == null) {
                continue;
            }
            List<BizAppointmentItem> items = map.get(a.getId());
            if (items == null || items.isEmpty()) {
                // 历史数据仅有主表 itemId：合成一条回显
                if (a.getItemId() != null || StringUtils.isNotBlank(a.getItemName())) {
                    BizAppointmentItem one = new BizAppointmentItem();
                    one.setAppointmentId(a.getId());
                    one.setItemId(a.getItemId());
                    one.setItemName(a.getItemName());
                    one.setDuration(30);
                    one.setSortOrder(0);
                    items = new ArrayList<BizAppointmentItem>();
                    items.add(one);
                } else {
                    items = new ArrayList<BizAppointmentItem>();
                }
            }
            a.setItems(items);
            List<Long> ids = new ArrayList<Long>();
            for (BizAppointmentItem it : items) {
                if (it.getItemId() != null) {
                    ids.add(it.getItemId());
                }
            }
            a.setItemIds(ids);
        }
    }

    /**
     * 同步患者首次/下次就诊时间（初诊/新诊写首次，其它写下次）
     *
     * @param appointment 预约
     */
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
        if (appointment.getVisitType() != null && (appointment.getVisitType() == 1 || appointment.getVisitType() == 3)) {
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

    /**
     * 写入预约操作日志
     *
     * @param appointmentId 预约ID
     * @param clinicId      诊所ID
     * @param action        动作编码
     * @param before        变更前状态
     * @param after         变更后状态
     * @param content       文案
     */
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

    /**
     * 校验预约类型枚举：normal / walkin / online / pending
     *
     * @param appointType 预约类型
     */
    private void validateAppointType(String appointType) {
        if (BizAppointment.APPOINT_TYPE_NORMAL.equals(appointType)
                || BizAppointment.APPOINT_TYPE_WALKIN.equals(appointType)
                || BizAppointment.APPOINT_TYPE_ONLINE.equals(appointType)
                || BizAppointment.APPOINT_TYPE_PENDING.equals(appointType)) {
            return;
        }
        throw new BusinessException("预约类型不正确");
    }

    /**
     * 校验预约来源枚举：clinic / online / wechat
     *
     * @param appointSource 预约来源
     */
    private void validateAppointSource(String appointSource) {
        if (BizAppointment.APPOINT_SOURCE_CLINIC.equals(appointSource)
                || BizAppointment.APPOINT_SOURCE_ONLINE.equals(appointSource)
                || BizAppointment.APPOINT_SOURCE_WECHAT.equals(appointSource)) {
            return;
        }
        throw new BusinessException("预约来源不正确");
    }

    /**
     * 加载未删除预约并校验所属诊所授权（详情/改状态/进回收站等）
     *
     * @param id 预约ID
     * @return 预约实体
     */
    private BizAppointment requireAuthorizedAppointment(Long id) {
        if (id == null) {
            throw new BusinessException("预约ID不能为空");
        }
        BizAppointment appt = this.getById(id);
        if (appt == null) {
            throw new BusinessException("预约不存在");
        }
        assertClinicAuthorized(appt.getClinicId());
        return appt;
    }

    /**
     * 加载预约（含回收站）并校验所属诊所授权
     *
     * @param id 预约ID
     * @return 预约实体
     */
    private BizAppointment requireAuthorizedAppointmentIgnoreDelete(Long id) {
        if (id == null) {
            throw new BusinessException("预约ID不能为空");
        }
        BizAppointment appt = baseMapper.selectByIdIgnoreDelete(id);
        if (appt == null) {
            throw new BusinessException("预约不存在");
        }
        assertClinicAuthorized(appt.getClinicId());
        return appt;
    }

    /**
     * 断言诊所在当前账号授权范围内，否则 403
     *
     * @param clinicId 预约所属诊所
     */
    private void assertClinicAuthorized(Long clinicId) {
        if (clinicId == null) {
            throw new BusinessException("预约诊所信息异常");
        }
        SecurityContextHolder.resolveAuthorizedClinicId(clinicId);
    }

    /**
     * 规范化关键字：去空白、限制长度
     *
     * @param keyword 原始关键字
     * @return 规范化后关键字，空则 null
     */
    private String normalizeKeyword(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return null;
        }
        String kw = keyword.trim();
        if (kw.length() > MAX_KEYWORD_LEN) {
            kw = kw.substring(0, MAX_KEYWORD_LEN);
        }
        return kw;
    }

    /**
     * 规范化分页大小：上限 MAX_PAGE_SIZE
     *
     * @param pageSize 请求页大小
     * @return 合法页大小
     */
    private long normalizePageSize(long pageSize) {
        if (pageSize < 1L) {
            return 20L;
        }
        if (pageSize > MAX_PAGE_SIZE) {
            return MAX_PAGE_SIZE;
        }
        return pageSize;
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

    /**
     * 查询诊所：授权范围内 clinicId 生效，空则回退会话诊所
     *
     * @param clinicId 请求诊所ID
     * @return 最终查询诊所ID
     */
    private Long resolveQueryClinicId(Long clinicId) {
        return SecurityContextHolder.resolveAuthorizedClinicId(clinicId);
    }

    /**
     * 将日历时间清零到当天 00:00:00
     *
     * @param cal 日历
     */
    private void clearTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
}
