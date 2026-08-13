package com.yujian.admin.controller;

import com.yujian.admin.service.IBizAppointmentService;
import com.yujian.common.biz.domain.BizAppointment;
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
 * 预约管理
 */
@RestController
@RequestMapping("/biz/appointment")
public class BizAppointmentController {

    @Autowired
    private IBizAppointmentService appointmentService;

    /**
     * 列表视图 / 首页今日任务
     */
    @GetMapping("/list")
    public R<PageResult<BizAppointment>> list(@RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) Long clinicId,
                                              @RequestParam(required = false) Long doctorId,
                                              @RequestParam(required = false) Long consultantId,
                                              @RequestParam(required = false) Integer visitType,
                                              @RequestParam(required = false) Integer status,
                                              @RequestParam(required = false)
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
                                              @RequestParam(required = false)
                                              @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
                                              @RequestParam(defaultValue = "1") long pageNum,
                                              @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(appointmentService.selectPage(keyword, clinicId, doctorId, consultantId,
                visitType, status, beginTime, endTime, pageNum, pageSize));
    }

    /**
     * 日历视图（天/周/月）
     * status: 逗号分隔，如 1,2,3
     */
    @GetMapping("/calendar")
    public R<List<BizAppointment>> calendar(@RequestParam(required = false) Long clinicId,
                                            @RequestParam
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
                                            @RequestParam
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
                                            @RequestParam(required = false) Long doctorId,
                                            @RequestParam(required = false) String status) {
        List<Integer> statusList = null;
        if (status != null && status.trim().length() > 0) {
            statusList = Arrays.stream(status.split(","))
                    .filter(s -> s.trim().length() > 0)
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
        }
        return R.ok(appointmentService.selectCalendar(clinicId, beginTime, endTime, doctorId, statusList));
    }

    @GetMapping("/{id}")
    public R<BizAppointment> getInfo(@PathVariable Long id) {
        return R.ok(appointmentService.selectById(id));
    }

    /** 新增预约 */
    @PostMapping
    public R<?> add(@RequestBody BizAppointment appointment) {
        return appointmentService.insertAppointment(appointment) > 0 ? R.ok() : R.fail();
    }

    /** 修改预约 */
    @PutMapping
    public R<?> edit(@RequestBody BizAppointment appointment) {
        return appointmentService.updateAppointment(appointment) > 0 ? R.ok() : R.fail();
    }

    /** 删除预约 */
    @DeleteMapping("/{id}")
    public R<?> remove(@PathVariable Long id) {
        return appointmentService.deleteAppointment(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 变更预约状态
     * status: 1已预约 2已确认 3已到达 4治疗中 5已离开 6已过期 7已流失 8预约未到
     */
    @PutMapping("/status")
    public R<?> updateStatus(@RequestBody Map<String, Object> body) {
        Long id = Long.valueOf(String.valueOf(body.get("id")));
        Integer status = Integer.valueOf(String.valueOf(body.get("status")));
        return appointmentService.updateStatus(id, status) > 0 ? R.ok() : R.fail();
    }

    /** 接诊入位 */
    @PutMapping("/seat/{id}")
    public R<?> seat(@PathVariable Long id) {
        return appointmentService.seatPatient(id) > 0 ? R.ok() : R.fail();
    }

    /** 首页今日统计卡片 */
    @GetMapping("/stats/today")
    public R<Map<String, Object>> todayStats(@RequestParam(required = false) Long clinicId) {
        return R.ok(appointmentService.todayStats(clinicId));
    }
}
