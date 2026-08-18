package com.yujian.admin.controller;

import com.yujian.admin.dto.request.AppointmentCancelRequest;
import com.yujian.admin.dto.request.AppointmentStatusRequest;
import com.yujian.admin.service.IBizAppointmentService;
import com.yujian.common.biz.domain.BizAppointment;
import com.yujian.common.biz.domain.BizAppointmentLog;
import com.yujian.common.core.domain.PageResult;
import com.yujian.common.core.domain.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 预约管理接口（按当前所选诊所隔离）
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Api(tags = "预约管理")
@RestController
@RequestMapping("/biz/appointment")
public class BizAppointmentController {

    @Autowired
    private IBizAppointmentService appointmentService;

    /**
     * 预约分页列表
     *
     * @param keyword       患者姓名/手机/病历号
     * @param clinicId      忽略
     * @param doctorId      医生
     * @param consultantId  咨询师
     * @param visitType     1初诊 2复诊
     * @param status        预约状态
     * @param appointSource 来源
     * @param beginTime     开始时间
     * @param endTime       结束时间
     * @param pageNum       页码
     * @param pageSize      每页条数
     * @return 分页数据
     */
    @ApiOperation("预约分页列表")
    @GetMapping("/list")
    public R<PageResult<BizAppointment>> list(
            @ApiParam("关键字") @RequestParam(required = false) String keyword,
            @ApiParam("诊所ID（忽略）") @RequestParam(required = false) Long clinicId,
            @ApiParam("医生ID") @RequestParam(required = false) Long doctorId,
            @ApiParam("咨询师ID") @RequestParam(required = false) Long consultantId,
            @ApiParam("就诊类型") @RequestParam(required = false) Integer visitType,
            @ApiParam("状态") @RequestParam(required = false) Integer status,
            @ApiParam("预约来源") @RequestParam(required = false) String appointSource,
            @ApiParam("开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
            @ApiParam("结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @ApiParam("页码") @RequestParam(defaultValue = "1") long pageNum,
            @ApiParam("每页条数") @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(appointmentService.selectPage(keyword, clinicId, doctorId, consultantId,
                visitType, status, appointSource, beginTime, endTime, pageNum, pageSize));
    }

    /**
     * 周/月日历扁平列表
     *
     * @param clinicId  忽略
     * @param beginTime 开始时间，必填
     * @param endTime   结束时间，必填
     * @param doctorId  医生
     * @param status    多状态逗号分隔
     * @return 预约列表
     */
    @ApiOperation("预约日历")
    @GetMapping("/calendar")
    public R<List<BizAppointment>> calendar(
            @ApiParam("诊所ID（忽略）") @RequestParam(required = false) Long clinicId,
            @ApiParam(value = "开始时间", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
            @ApiParam(value = "结束时间", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @ApiParam("医生ID") @RequestParam(required = false) Long doctorId,
            @ApiParam("状态，逗号分隔") @RequestParam(required = false) String status) {
        return R.ok(appointmentService.selectCalendar(clinicId, beginTime, endTime, doctorId, parseStatus(status)));
    }

    /**
     * 天视图：按医生分列
     *
     * @param clinicId 忽略
     * @param day      日期 yyyy-MM-dd
     * @param status   多状态逗号分隔
     * @return day / columns / total
     */
    @ApiOperation("预约天视图")
    @GetMapping("/dayGrid")
    public R<Map<String, Object>> dayGrid(
            @ApiParam("诊所ID（忽略）") @RequestParam(required = false) Long clinicId,
            @ApiParam(value = "日期", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date day,
            @ApiParam("状态") @RequestParam(required = false) String status) {
        return R.ok(appointmentService.selectDayGrid(clinicId, day, parseStatus(status)));
    }

    /**
     * 状态筛选计数
     *
     * @param clinicId  忽略
     * @param beginTime 开始
     * @param endTime   结束
     * @param doctorId  医生
     * @return 各状态数量
     */
    @ApiOperation("预约状态计数")
    @GetMapping("/stats/statusCount")
    public R<Map<String, Object>> statusCount(
            @ApiParam("诊所ID（忽略）") @RequestParam(required = false) Long clinicId,
            @ApiParam("开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
            @ApiParam("结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @ApiParam("医生ID") @RequestParam(required = false) Long doctorId) {
        return R.ok(appointmentService.statusCount(clinicId, beginTime, endTime, doctorId));
    }

    /**
     * 今日预约统计卡片
     *
     * @param clinicId 忽略
     * @return 今日统计
     */
    @ApiOperation("今日预约统计")
    @GetMapping("/stats/today")
    public R<Map<String, Object>> todayStats(@ApiParam("诊所ID（忽略）") @RequestParam(required = false) Long clinicId) {
        return R.ok(appointmentService.todayStats(clinicId));
    }

    /**
     * 预约详情
     *
     * @param id 预约ID
     * @return 预约
     */
    @ApiOperation("预约详情")
    @GetMapping("/{id}")
    public R<BizAppointment> getInfo(@ApiParam(value = "预约ID", required = true) @PathVariable Long id) {
        return R.ok(appointmentService.selectById(id));
    }

    /**
     * 新增预约
     *
     * @param appointment 预约信息
     * @return 操作结果
     */
    @ApiOperation("新增预约")
    @PostMapping
    public R<?> add(@RequestBody BizAppointment appointment) {
        return appointmentService.insertAppointment(appointment) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改预约
     *
     * @param appointment 预约信息（须含 id）
     * @return 操作结果
     */
    @ApiOperation("修改预约")
    @PostMapping("/edit")
    public R<?> edit(@RequestBody BizAppointment appointment) {
        return appointmentService.updateAppointment(appointment) > 0 ? R.ok() : R.fail();
    }

    /**
     * 软删除进回收站
     *
     * @param id           预约ID
     * @param cancelReason 原因
     * @return 操作结果
     */
    @ApiOperation("预约进回收站")
    @PostMapping("/remove/{id}")
    public R<?> remove(@ApiParam(value = "预约ID", required = true) @PathVariable Long id,
                       @ApiParam("取消原因") @RequestParam(required = false) String cancelReason) {
        return appointmentService.deleteAppointment(id, cancelReason) > 0 ? R.ok() : R.fail();
    }

    /**
     * 更新预约状态
     *
     * @param body 预约ID、状态、备注
     * @return 操作结果
     */
    @ApiOperation("更新预约状态")
    @PostMapping("/status")
    public R<?> updateStatus(@RequestBody AppointmentStatusRequest body) {
        return appointmentService.updateStatus(body.getId(), body.getStatus(), body.getRemark()) > 0
                ? R.ok() : R.fail();
    }

    /**
     * 确认预约
     *
     * @param id 预约ID
     * @return 操作结果
     */
    @ApiOperation("确认预约")
    @PostMapping("/confirm/{id}")
    public R<?> confirm(@ApiParam(value = "预约ID", required = true) @PathVariable Long id) {
        return appointmentService.confirm(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 取消预约（状态=已流失）
     *
     * @param body 预约ID与取消原因
     * @return 操作结果
     */
    @ApiOperation("取消预约")
    @PostMapping("/cancel")
    public R<?> cancel(@RequestBody AppointmentCancelRequest body) {
        return appointmentService.cancel(body.getId(), body.getCancelReason()) > 0 ? R.ok() : R.fail();
    }

    /**
     * 患者入座
     *
     * @param id 预约ID
     * @return 操作结果
     */
    @ApiOperation("患者入座")
    @PostMapping("/seat/{id}")
    public R<?> seat(@ApiParam(value = "预约ID", required = true) @PathVariable Long id) {
        return appointmentService.seatPatient(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 预约操作日志
     *
     * @param id 预约ID
     * @return 日志列表
     */
    @ApiOperation("预约操作日志")
    @GetMapping("/{id}/logs")
    public R<List<BizAppointmentLog>> logs(@ApiParam(value = "预约ID", required = true) @PathVariable Long id) {
        return R.ok(appointmentService.selectLogs(id));
    }

    /**
     * 回收站分页
     *
     * @param keyword      关键字
     * @param clinicId     忽略
     * @param doctorId     医生
     * @param consultantId 咨询师
     * @param beginTime    开始
     * @param endTime      结束
     * @param pageNum      页码
     * @param pageSize     每页条数
     * @return 分页数据
     */
    @ApiOperation("预约回收站")
    @GetMapping("/recycle/list")
    public R<PageResult<BizAppointment>> recycleList(
            @ApiParam("关键字") @RequestParam(required = false) String keyword,
            @ApiParam("诊所ID（忽略）") @RequestParam(required = false) Long clinicId,
            @ApiParam("医生ID") @RequestParam(required = false) Long doctorId,
            @ApiParam("咨询师ID") @RequestParam(required = false) Long consultantId,
            @ApiParam("开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
            @ApiParam("结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @ApiParam("页码") @RequestParam(defaultValue = "1") long pageNum,
            @ApiParam("每页条数") @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(appointmentService.selectRecyclePage(keyword, clinicId, doctorId, consultantId,
                beginTime, endTime, pageNum, pageSize));
    }

    /**
     * 回收站还原
     *
     * @param id 预约ID
     * @return 操作结果
     */
    @ApiOperation("还原预约")
    @PostMapping("/recycle/restore/{id}")
    public R<?> restore(@ApiParam(value = "预约ID", required = true) @PathVariable Long id) {
        return appointmentService.restore(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 回收站彻底删除
     *
     * @param id 预约ID
     * @return 操作结果
     */
    @ApiOperation("彻底删除预约")
    @PostMapping("/recycle/remove/{id}")
    public R<?> permanentDelete(@ApiParam(value = "预约ID", required = true) @PathVariable Long id) {
        return appointmentService.permanentDelete(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 将逗号分隔状态字符串转为列表
     *
     * @param status 如 1,2,3
     * @return 状态列表，空则 null
     */
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
