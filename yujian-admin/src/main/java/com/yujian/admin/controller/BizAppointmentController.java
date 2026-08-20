package com.yujian.admin.controller;

import com.yujian.admin.dto.request.AppointmentCancelRequest;
import com.yujian.admin.dto.request.AppointmentStatusRequest;
import com.yujian.admin.service.IBizAppointmentService;
import com.yujian.common.biz.domain.BizAppointment;
import com.yujian.common.biz.domain.BizAppointmentLog;
import com.yujian.common.core.domain.PageResult;
import com.yujian.common.core.domain.R;
import com.yujian.common.exception.BusinessException;
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
 * 预约管理接口：列表 / 天视图 / 新建改删 / 回收站 / 日志。
 * <p>
 * 列表、天视图、回收站的 clinicId 在账号授权诊所范围内生效；
 * 按 ID 的读写在服务层校验预约所属诊所，防止跨店越权。
 * </p>
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
     * 分页查询预约列表；clinicId 授权生效；支持创建时间、预约类型、状态多选。
     * <p>
     * 左侧「待确定预约」传 appointType=pending（非 status）。
     * </p>
     *
     * @param keyword         患者姓名/手机/病历号关键字，可选
     * @param clinicId        授权诊所ID，空=会话当前诊所
     * @param doctorId        医生ID，可选
     * @param consultantId    咨询师ID，可选
     * @param visitType       就诊类型：1初诊/2复诊/3新诊，可选
     * @param status          预约状态，逗号多选如 1,2,6，可选
     * @param appointType     预约类型：normal/walkin/online/pending，可选
     * @param appointSource   预约来源，可选
     * @param beginTime       预约开始时间起，可选
     * @param endTime         预约开始时间止，可选
     * @param createBeginTime 创建时间起，可选
     * @param createEndTime   创建时间止，可选
     * @param pageNum         页码，默认 1
     * @param pageSize        每页条数，默认 20
     * @return 统一响应，data 为分页结果（含 clinicName、items）
     */
    @ApiOperation("预约分页列表")
    @GetMapping("/list")
    public R<PageResult<BizAppointment>> list(
            @ApiParam("关键字") @RequestParam(required = false) String keyword,
            @ApiParam("诊所ID（授权范围内生效）") @RequestParam(required = false) Long clinicId,
            @ApiParam("医生ID") @RequestParam(required = false) Long doctorId,
            @ApiParam("咨询师ID") @RequestParam(required = false) Long consultantId,
            @ApiParam("就诊类型") @RequestParam(required = false) Integer visitType,
            @ApiParam("状态，逗号分隔") @RequestParam(required = false) String status,
            @ApiParam("预约类型 normal/walkin/online/pending") @RequestParam(required = false) String appointType,
            @ApiParam("预约来源") @RequestParam(required = false) String appointSource,
            @ApiParam("预约开始时间起") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
            @ApiParam("预约开始时间止") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @ApiParam("创建时间起") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date createBeginTime,
            @ApiParam("创建时间止") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date createEndTime,
            @ApiParam("页码") @RequestParam(defaultValue = "1") long pageNum,
            @ApiParam("每页条数") @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(appointmentService.selectPage(keyword, clinicId, doctorId, consultantId,
                visitType, parseStatus(status), appointType, appointSource,
                beginTime, endTime, createBeginTime, createEndTime, pageNum, pageSize));
    }

    /**
     * 查询周/月日历视图的预约扁平列表；clinicId 授权范围内生效。
     *
     * @param clinicId  授权诊所ID，空=会话当前诊所
     * @param beginTime 时间范围开始，必填
     * @param endTime   时间范围结束，必填
     * @param doctorId  医生ID，可选
     * @param status    预约状态，多个以逗号分隔，可选
     * @return 统一响应，data 为 {@link BizAppointment} 列表
     */
    @ApiOperation("预约日历")
    @GetMapping("/calendar")
    public R<List<BizAppointment>> calendar(
            @ApiParam("诊所ID（授权范围内生效）") @RequestParam(required = false) Long clinicId,
            @ApiParam(value = "开始时间", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
            @ApiParam(value = "结束时间", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @ApiParam("医生ID") @RequestParam(required = false) Long doctorId,
            @ApiParam("状态，逗号分隔") @RequestParam(required = false) String status) {
        return R.ok(appointmentService.selectCalendar(clinicId, beginTime, endTime, doctorId, parseStatus(status)));
    }

    /**
     * 查询指定日期的预约天视图，按医生分列；clinicId 在授权范围内生效。
     *
     * @param clinicId  授权诊所ID，空=会话当前诊所
     * @param day       日期，格式 yyyy-MM-dd，必填
     * @param status    预约状态，多个以逗号分隔，可选
     * @param doctorIds 医生ID逗号分隔（含 0=未指定），空=全部列
     * @return 统一响应，data 为 Map：day / columns[{doctorId,doctorName,count,appointments}] / total
     */
    @ApiOperation("预约天视图")
    @GetMapping("/dayGrid")
    public R<Map<String, Object>> dayGrid(
            @ApiParam("诊所ID（授权范围内生效）") @RequestParam(required = false) Long clinicId,
            @ApiParam(value = "日期", required = true)
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date day,
            @ApiParam("状态，逗号分隔") @RequestParam(required = false) String status,
            @ApiParam("医生ID，逗号分隔，0=未指定") @RequestParam(required = false) String doctorIds) {
        return R.ok(appointmentService.selectDayGrid(clinicId, day, parseStatus(status), parseLongIds(doctorIds)));
    }

    /**
     * 统计各预约状态的数量；clinicId 与 dayGrid 同一规则（授权范围内生效）。
     *
     * @param clinicId  授权诊所ID，空=会话当前诊所
     * @param beginTime 统计开始时间，可选
     * @param endTime   统计结束时间，可选
     * @param doctorId  医生ID，可选
     * @return 统一响应，data 为 Map（all/booked/confirmed/.../byStatus）
     */
    @ApiOperation("预约状态计数")
    @GetMapping("/stats/statusCount")
    public R<Map<String, Object>> statusCount(
            @ApiParam("诊所ID（授权范围内生效）") @RequestParam(required = false) Long clinicId,
            @ApiParam("开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
            @ApiParam("结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @ApiParam("医生ID") @RequestParam(required = false) Long doctorId) {
        return R.ok(appointmentService.statusCount(clinicId, beginTime, endTime, doctorId));
    }

    /**
     * 查询今日预约统计卡片数据；按当前所选诊所隔离。
     *
     * @param clinicId 诊所ID（前端传入将被忽略，以当前所选诊所为准）
     * @return 统一响应，data 为 Map，含今日各状态预约数量等统计指标
     */
    @ApiOperation("今日预约统计")
    @GetMapping("/stats/today")
    public R<Map<String, Object>> todayStats(@ApiParam("诊所ID（忽略）") @RequestParam(required = false) Long clinicId) {
        return R.ok(appointmentService.todayStats(clinicId));
    }

    /**
     * 查询预约详情（含 itemIds/items）；须属账号授权诊所。
     *
     * @param id 预约ID
     * @return 统一响应，data 为 {@link BizAppointment}
     */
    @ApiOperation("预约详情")
    @GetMapping("/{id}")
    public R<BizAppointment> getInfo(@ApiParam(value = "预约ID", required = true) @PathVariable Long id) {
        return R.ok(appointmentService.selectById(id));
    }

    /**
     * 新增预约；clinicId 在授权范围内生效（空=会话诊所），患者/项目按该诊所校验。
     *
     * @param appointment 预约信息（可含 itemIds 多选、visitType=1/2/3）
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("新增预约")
    @PostMapping
    public R<?> add(@RequestBody BizAppointment appointment) {
        return appointmentService.insertAppointment(appointment) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改预约；禁止改诊所；可传 itemIds 覆盖项目；历史单可改备注，改期新日期不得早于今天。
     *
     * @param appointment 预约信息（须含 id）
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("修改预约")
    @PostMapping("/edit")
    public R<?> edit(@RequestBody BizAppointment appointment) {
        return appointmentService.updateAppointment(appointment) > 0 ? R.ok() : R.fail();
    }

    /**
     * 将预约软删除并移入回收站；按当前所选诊所隔离。
     *
     * @param id           预约ID
     * @param cancelReason 取消/删除原因，可选
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("预约进回收站")
    @PostMapping("/remove/{id}")
    public R<?> remove(@ApiParam(value = "预约ID", required = true) @PathVariable Long id,
                       @ApiParam("取消原因") @RequestParam(required = false) String cancelReason) {
        return appointmentService.deleteAppointment(id, cancelReason) > 0 ? R.ok() : R.fail();
    }

    /**
     * 更新预约状态并记录备注；按当前所选诊所隔离。
     *
     * @param body 请求体，含预约 id、目标 status 及 remark
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("更新预约状态")
    @PostMapping("/status")
    public R<?> updateStatus(@RequestBody AppointmentStatusRequest body) {
        return appointmentService.updateStatus(body.getId(), body.getStatus(), body.getRemark()) > 0
                ? R.ok() : R.fail();
    }

    /**
     * 确认预约，将状态更新为已确认；按当前所选诊所隔离。
     *
     * @param id 预约ID
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("确认预约")
    @PostMapping("/confirm/{id}")
    public R<?> confirm(@ApiParam(value = "预约ID", required = true) @PathVariable Long id) {
        return appointmentService.confirm(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 取消预约，将状态更新为已流失；按当前所选诊所隔离。
     *
     * @param body 请求体，含预约 id 与 cancelReason
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("取消预约")
    @PostMapping("/cancel")
    public R<?> cancel(@RequestBody AppointmentCancelRequest body) {
        return appointmentService.cancel(body.getId(), body.getCancelReason()) > 0 ? R.ok() : R.fail();
    }

    /**
     * 患者入座，将预约状态更新为已到诊；按当前所选诊所隔离。
     *
     * @param id 预约ID
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("患者入座")
    @PostMapping("/seat/{id}")
    public R<?> seat(@ApiParam(value = "预约ID", required = true) @PathVariable Long id) {
        return appointmentService.seatPatient(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 查询指定预约的操作日志；按当前所选诊所隔离。
     *
     * @param id 预约ID
     * @return 统一响应，data 为 {@link BizAppointmentLog} 列表
     */
    @ApiOperation("预约操作日志")
    @GetMapping("/{id}/logs")
    public R<List<BizAppointmentLog>> logs(@ApiParam(value = "预约ID", required = true) @PathVariable Long id) {
        return R.ok(appointmentService.selectLogs(id));
    }

    /**
     * 分页查询回收站；clinicId 授权范围内生效。
     *
     * @param keyword      患者姓名/手机/病历号关键字，可选
     * @param clinicId     授权诊所ID，空=会话当前诊所
     * @param doctorId     医生ID，可选
     * @param consultantId 咨询师ID，可选
     * @param beginTime    预约开始时间起，可选
     * @param endTime      预约开始时间止，可选
     * @param pageNum      页码，默认 1
     * @param pageSize     每页条数，默认 20
     * @return 统一响应，data 为分页结果
     */
    @ApiOperation("预约回收站")
    @GetMapping("/recycle/list")
    public R<PageResult<BizAppointment>> recycleList(
            @ApiParam("关键字") @RequestParam(required = false) String keyword,
            @ApiParam("诊所ID（授权范围内生效）") @RequestParam(required = false) Long clinicId,
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
     * 从回收站还原已删除的预约；按当前所选诊所隔离。
     *
     * @param id 预约ID
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("还原预约")
    @PostMapping("/recycle/restore/{id}")
    public R<?> restore(@ApiParam(value = "预约ID", required = true) @PathVariable Long id) {
        return appointmentService.restore(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 从回收站彻底删除预约，不可恢复；按当前所选诊所隔离。
     *
     * @param id 预约ID
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("彻底删除预约")
    @PostMapping("/recycle/remove/{id}")
    public R<?> permanentDelete(@ApiParam(value = "预约ID", required = true) @PathVariable Long id) {
        return appointmentService.permanentDelete(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 清空回收站（物理删除）；clinicId 授权生效；可选按预约开始时间范围过滤。
     *
     * @param clinicId  授权诊所ID，空=会话
     * @param beginTime 预约开始时间起，可选
     * @param endTime   预约开始时间止，可选
     * @return 统一响应，data 为删除条数
     */
    @ApiOperation("清空回收站")
    @PostMapping("/recycle/clear")
    public R<Integer> clearRecycle(
            @ApiParam("诊所ID（授权范围内生效）") @RequestParam(required = false) Long clinicId,
            @ApiParam("预约开始时间起") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
            @ApiParam("预约开始时间止") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        return R.ok(appointmentService.clearRecycle(clinicId, beginTime, endTime));
    }

    /**
     * 将逗号分隔的预约状态字符串解析为整数列表；非法数字抛业务异常。
     *
     * @param status 状态字符串，如 "1,2,3"
     * @return 状态整数列表；入参为空或空白时返回 null
     */
    private List<Integer> parseStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return null;
        }
        try {
            return Arrays.stream(status.split(","))
                    .map(String::trim)
                    .filter(s -> s.length() > 0)
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new BusinessException("预约状态参数不正确");
        }
    }

    /**
     * 将逗号分隔的医生 ID 字符串解析为 Long 列表（可含 0 表示未指定医生）。
     *
     * @param doctorIds 如 "0,12,15"
     * @return ID 列表；空入参返回 null（表示不过滤）
     */
    private List<Long> parseLongIds(String doctorIds) {
        if (doctorIds == null || doctorIds.trim().isEmpty()) {
            return null;
        }
        try {
            return Arrays.stream(doctorIds.split(","))
                    .map(String::trim)
                    .filter(s -> s.length() > 0)
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new BusinessException("医生ID参数不正确");
        }
    }
}
