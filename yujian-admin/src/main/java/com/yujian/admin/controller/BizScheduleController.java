package com.yujian.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yujian.admin.mapper.BizScheduleMapper;
import com.yujian.common.biz.domain.BizSchedule;
import com.yujian.common.core.context.SecurityContextHolder;
import com.yujian.common.core.domain.R;
import com.yujian.common.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 员工日程（截图「新增日程」）
 */
@RestController
@RequestMapping("/biz/schedule")
public class BizScheduleController {

    @Autowired
    private BizScheduleMapper scheduleMapper;

    @GetMapping("/list")
    public R<List<BizSchedule>> list(@RequestParam(required = false) Long clinicId,
                                     @RequestParam(required = false) Long doctorId,
                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        if (clinicId == null) {
            clinicId = SecurityContextHolder.getClinicId();
        }
        return R.ok(scheduleMapper.selectList(new LambdaQueryWrapper<BizSchedule>()
                .eq(clinicId != null, BizSchedule::getClinicId, clinicId)
                .eq(doctorId != null, BizSchedule::getDoctorId, doctorId)
                .ge(BizSchedule::getStartTime, beginTime)
                .le(BizSchedule::getStartTime, endTime)
                .eq(BizSchedule::getStatus, 0)
                .orderByAsc(BizSchedule::getStartTime)));
    }

    @PostMapping
    public R<?> add(@RequestBody BizSchedule schedule) {
        if (StringUtils.isBlank(schedule.getTitle())) {
            throw new BusinessException("日程标题不能为空");
        }
        if (schedule.getStartTime() == null || schedule.getEndTime() == null) {
            throw new BusinessException("日程时间不能为空");
        }
        if (schedule.getClinicId() == null) {
            schedule.setClinicId(SecurityContextHolder.getClinicId());
        }
        if (schedule.getStatus() == null) {
            schedule.setStatus(0);
        }
        return scheduleMapper.insert(schedule) > 0 ? R.ok() : R.fail();
    }

    @PutMapping
    public R<?> edit(@RequestBody BizSchedule schedule) {
        return scheduleMapper.updateById(schedule) > 0 ? R.ok() : R.fail();
    }

    @DeleteMapping("/{id}")
    public R<?> remove(@PathVariable Long id) {
        return scheduleMapper.deleteById(id) > 0 ? R.ok() : R.fail();
    }
}
