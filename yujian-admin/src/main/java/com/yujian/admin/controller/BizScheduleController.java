package com.yujian.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yujian.admin.mapper.BizScheduleMapper;
import com.yujian.common.biz.domain.BizSchedule;
import com.yujian.common.core.context.SecurityContextHolder;
import com.yujian.common.core.domain.R;
import com.yujian.common.exception.BusinessException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 员工日程接口（按当前所选诊所隔离）
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Api(tags = "员工日程")
@RestController
@RequestMapping("/biz/schedule")
public class BizScheduleController {

    @Autowired
    private BizScheduleMapper scheduleMapper;

    /**
     * 日程列表
     *
     * @param clinicId  忽略
     * @param doctorId  医生/员工
     * @param beginTime 开始时间，必填
     * @param endTime   结束时间，必填
     * @return 日程列表
     */
    @ApiOperation("日程列表")
    @GetMapping("/list")
    public R<List<BizSchedule>> list(
            @ApiParam("诊所ID（忽略）") @RequestParam(required = false) Long clinicId,
            @ApiParam("医生ID") @RequestParam(required = false) Long doctorId,
            @ApiParam(value = "开始时间", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
            @ApiParam(value = "结束时间", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        clinicId = SecurityContextHolder.requireClinicId(clinicId);
        return R.ok(scheduleMapper.selectList(new LambdaQueryWrapper<BizSchedule>()
                .eq(BizSchedule::getClinicId, clinicId)
                .eq(doctorId != null, BizSchedule::getDoctorId, doctorId)
                .ge(BizSchedule::getStartTime, beginTime)
                .le(BizSchedule::getStartTime, endTime)
                .eq(BizSchedule::getStatus, 0)
                .orderByAsc(BizSchedule::getStartTime)));
    }

    /**
     * 新增日程
     *
     * @param schedule 日程（title、startTime、endTime 必填）
     * @return 操作结果
     */
    @ApiOperation("新增日程")
    @PostMapping
    public R<?> add(@RequestBody BizSchedule schedule) {
        if (StringUtils.isBlank(schedule.getTitle())) {
            throw new BusinessException("日程标题不能为空");
        }
        if (schedule.getStartTime() == null || schedule.getEndTime() == null) {
            throw new BusinessException("日程时间不能为空");
        }
        if (schedule.getClinicId() == null) {
            schedule.setClinicId(SecurityContextHolder.requireClinicId());
        }
        if (schedule.getStatus() == null) {
            schedule.setStatus(0);
        }
        return scheduleMapper.insert(schedule) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改日程
     *
     * @param schedule 日程（须含 id）
     * @return 操作结果
     */
    @ApiOperation("修改日程")
    @PostMapping("/edit")
    public R<?> edit(@RequestBody BizSchedule schedule) {
        return scheduleMapper.updateById(schedule) > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除日程
     *
     * @param id 日程ID
     * @return 操作结果
     */
    @ApiOperation("删除日程")
    @PostMapping("/remove/{id}")
    public R<?> remove(@ApiParam(value = "日程ID", required = true) @PathVariable Long id) {
        return scheduleMapper.deleteById(id) > 0 ? R.ok() : R.fail();
    }
}
