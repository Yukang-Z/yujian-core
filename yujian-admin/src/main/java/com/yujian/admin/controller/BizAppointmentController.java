package com.yujian.admin.controller;

import com.yujian.admin.service.IBizAppointmentService;
import com.yujian.common.biz.domain.BizAppointment;
import com.yujian.common.biz.domain.BizAppointmentLog;
import com.yujian.common.core.domain.PageResult;
import com.yujian.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 预约管理（天/周/月/列表/回收站）
 */
@RestController
@RequestMapping("/biz/appointment")
public class BizAppointmentController {

    @Autowired
    private IBizAppointmentService appointmentService;

    /** 列表视图 */
    @GetMapping("/list")
    public R<PageResult<BizAppointment>> list(@RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) Long clinicId,
                                              @RequestParam(required = false) Long doctorId,
                                              @RequestParam(required = false) Long consultantId,
                                              @RequestParam(required = false) Integer visitType,
                                              @RequestParam(required = false) Integer status,
                                              @RequestParam(required = false) String appointSource,
                                              @RequestParam(required = false)
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
                                              @RequestParam(required = false)
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
                                              @RequestParam(defaultValue = "1") long pageNum,
                                              @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(appointmentService.selectPage(keyword, clinicId, doctorId, consultantId,
                visitType, status, appointSource, beginTime, endTime, pageNum, pageSize));
    }

    /** 周/月日历（扁平列表，前端按天聚合） */
    @GetMapping("/calendar")
    public R<List<BizAppointment>> calendar(@RequestParam(required = false) Long clinicId,
                                            @RequestParam
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
                                            @RequestParam
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
                                            @RequestParam(required = false) Long doctorId,
                                            @RequestParam(required = false) String status) {
        return R.ok(appointmentService.selectCalendar(clinicId, beginTime, endTime, doctorId, parseStatus(status)));
    }

    /** 天视图：按医生分列 */
    @GetMapping("/dayGrid")
    public R<Map<String, Object>> dayGrid(@RequestParam(required = false) Long clinicId,
                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date day,
                                          @RequestParam(required = false) String status) {
        return R.ok(appointmentService.selectDayGrid(clinicId, day, parseStatus(status)));
    }

    /** 左侧状态筛选计数 */
    @GetMapping("/stats/statusCount")
    public R<Map<String, Object>> statusCount(@RequestParam(required = false) Long clinicId,
                                              @RequestParam(required = false)
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
                                              @RequestParam(required = false)
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
                                              @RequestParam(required = false) Long doctorId) {
        return R.ok(appointmentService.statusCount(clinicId, beginTime, endTime, doctorId));
    }

    @GetMapping("/stats/today")
    public R<Map<String, Object>> todayStats(@RequestParam(required = false) Long clinicId) {
        return R.ok(appointmentService.todayStats(clinicId));
    }

    @GetMapping("/{id}")
    public R<BizAppointment> getInfo(@PathVariable Long id) {
        return R.ok(appointmentService.selectById(id));
    }

    @PostMapping
    public R<?> add(@RequestBody BizAppointment appointment) {
        return appointmentService.insertAppointment(appointment) > 0 ? R.ok() : R.fail();
    }

    @PutMapping
    public R<?> edit(@RequestBody BizAppointment appointment) {
        return appointmentService.updateAppointment(appointment) > 0 ? R.ok() : R.fail();
    }

    /** 软删除进回收站 */
    @DeleteMapping("/{id}")
    public R<?> remove(@PathVariable Long id, @RequestParam(required = false) String cancelReason) {
        return appointmentService.deleteAppointment(id, cancelReason) > 0 ? R.ok() : R.fail();
    }

    @PutMapping("/status")
    public R<?> updateStatus(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(String.valueOf(body.get("id")));
        Integer status = Integer.valueOf(String.valueOf(body.get("status")));
        String remark = body.get("remark") == null ? null : String.valueOf(body.get("remark"));
        return appointmentService.updateStatus(id, status, remark) > 0 ? R.ok() : R.fail();
    }

    /** 列表「确认」 */
    @PutMapping("/confirm/{id}")
    public R<?> confirm(@PathVariable Long id) {
        return appointmentService.confirm(id) > 0 ? R.ok() : R.fail();
    }

    /** 取消预约（状态=已流失） */
    @PutMapping("/cancel")
    public R<?> cancel(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(String.valueOf(body.get("id")));
        String reason = body.get("cancelReason") == null ? null : String.valueOf(body.get("cancelReason"));
        return appointmentService.cancel(id, reason) > 0 ? R.ok() : R.fail();
    }

    @PutMapping("/seat/{id}")
    public R<?> seat(@PathVariable Long id) {
        return appointmentService.seatPatient(id) > 0 ? R.ok() : R.fail();
    }

    /** 操作日志 */
    @GetMapping("/{id}/logs")
    public R<List<BizAppointmentLog>> logs(@PathVariable Long id) {
        return R.ok(appointmentService.selectLogs(id));
    }

    /** 回收站列表 */
    @GetMapping("/recycle/list")
    public R<PageResult<BizAppointment>> recycleList(@RequestParam(required = false) String keyword,
                                                     @RequestParam(required = false) Long clinicId,
                                                     @RequestParam(required = false) Long doctorId,
                                                     @RequestParam(required = false) Long consultantId,
                                                     @RequestParam(required = false)
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
                                                     @RequestParam(required = false)
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
                                                     @RequestParam(defaultValue = "1") long pageNum,
                                                     @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(appointmentService.selectRecyclePage(keyword, clinicId, doctorId, consultantId,
                beginTime, endTime, pageNum, pageSize));
    }

    @PutMapping("/recycle/restore/{id}")
    public R<?> restore(@PathVariable Long id) {
        return appointmentService.restore(id) > 0 ? R.ok() : R.fail();
    }

    @DeleteMapping("/recycle/{id}")
    public R<?> permanentDelete(@PathVariable Long id) {
        return appointmentService.permanentDelete(id) > 0 ? R.ok() : R.fail();
    }

    private List<Integer> parseStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return null;
        }
        return Arrays.stream(status.split(","))
                .filter(s -> s.trim().length() > 0)
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }
}
