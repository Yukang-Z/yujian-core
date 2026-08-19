package com.yujian.admin.controller;

import com.yujian.admin.dto.response.PatientActionVO;
import com.yujian.admin.service.IBizPatientProfileService;
import com.yujian.admin.service.IBizPatientService;
import com.yujian.common.biz.domain.BizPatient;
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

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 患者管理接口，提供列表查询、档案聚合、增删改及快捷动作；按当前所选诊所隔离。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Api(tags = "患者管理")
@RestController
@RequestMapping("/biz/patient")
public class BizPatientController {

    @Autowired
    private IBizPatientService patientService;

    @Autowired
    private IBizPatientProfileService profileService;

    /**
     * 分页查询患者列表，支持关键字、医生、标签等条件筛选；按当前所选诊所隔离。
     *
     * @param keyword       姓名/手机/病历号关键字，可选
     * @param clinicId      诊所ID（前端传入将被忽略，以当前所选诊所为准）
     * @param doctorId      主治医生ID，可选
     * @param firstDoctorId 初诊医生ID，可选
     * @param tagId         患者标签ID，可选
     * @param pageNum       页码，默认 1
     * @param pageSize      每页条数，默认 20
     * @return 统一响应，data 为分页结果（records 为 {@link BizPatient} 列表、total 为总条数）
     */
    @ApiOperation("患者分页列表")
    @GetMapping("/list")
    public R<PageResult<BizPatient>> list(
            @ApiParam("关键字") @RequestParam(required = false) String keyword,
            @ApiParam("诊所ID（忽略）") @RequestParam(required = false) Long clinicId,
            @ApiParam("主治医生ID") @RequestParam(required = false) Long doctorId,
            @ApiParam("初诊医生ID") @RequestParam(required = false) Long firstDoctorId,
            @ApiParam("标签ID") @RequestParam(required = false) Long tagId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") long pageNum,
            @ApiParam("每页条数") @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(patientService.selectPage(keyword, clinicId, doctorId, firstDoctorId, tagId, pageNum, pageSize));
    }

    /**
     * 查询患者侧栏列表，支持今日/全部/最近等视图；按当前所选诊所隔离。
     *
     * @param type     列表类型：today 今日 / all 全部 / recent 最近
     * @param clinicId 诊所ID（前端传入将被忽略，以当前所选诊所为准）
     * @param keyword  姓名/手机/病历号关键字，可选
     * @param day      今日列表对应日期（type=today 时使用），格式 yyyy-MM-dd
     * @param pageNum  页码，默认 1
     * @param pageSize 每页条数，默认 50
     * @return 统一响应，data 为分页结果（records 为 {@link BizPatient} 列表、total 为总条数）
     */
    @ApiOperation("患者侧栏列表")
    @GetMapping("/sidebar")
    public R<PageResult<BizPatient>> sidebar(
            @ApiParam("today|all|recent") @RequestParam(defaultValue = "all") String type,
            @ApiParam("诊所ID（忽略）") @RequestParam(required = false) Long clinicId,
            @ApiParam("关键字") @RequestParam(required = false) String keyword,
            @ApiParam("日期 yyyy-MM-dd") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date day,
            @ApiParam("页码") @RequestParam(defaultValue = "1") long pageNum,
            @ApiParam("每页条数") @RequestParam(defaultValue = "50") long pageSize) {
        return R.ok(profileService.sidebar(type, clinicId, keyword, day, pageNum, pageSize));
    }

    /**
     * 顶部全局搜索患者，按关键字模糊匹配；按当前所选诊所隔离。
     *
     * @param keyword  姓名/手机/病历号关键字，必填
     * @param clinicId 诊所ID（前端传入将被忽略，以当前所选诊所为准）
     * @param limit    返回条数上限，默认 20
     * @return 统一响应，data 为 {@link BizPatient} 列表
     */
    @ApiOperation("搜索患者")
    @GetMapping("/search")
    public R<List<BizPatient>> search(
            @ApiParam(value = "关键字", required = true) @RequestParam String keyword,
            @ApiParam("诊所ID（忽略）") @RequestParam(required = false) Long clinicId,
            @ApiParam("条数") @RequestParam(defaultValue = "20") int limit) {
        return R.ok(patientService.search(keyword, clinicId, limit));
    }

    /**
     * 聚合查询患者档案页数据，含详情头、价值卡片与最近日志；按当前所选诊所隔离。
     *
     * @param id 患者ID
     * @return 统一响应，data 为 Map：patient 为患者详情，cards 为价值卡片，logs 为最近操作日志
     */
    @ApiOperation("患者档案聚合")
    @GetMapping("/{id}/profile")
    public R<Map<String, Object>> profile(@ApiParam(value = "患者ID", required = true) @PathVariable Long id) {
        return R.ok(profileService.profile(id));
    }

    /**
     * 查询患者就诊时间线，可按时间范围过滤；按当前所选诊所隔离。
     *
     * @param id        患者ID
     * @param clinicId  诊所ID（前端传入将被忽略，以当前所选诊所为准）
     * @param beginTime 就诊开始时间，可选
     * @param endTime   就诊结束时间，可选
     * @return 统一响应，data 为时间线节点列表（每条含就诊/处置等摘要信息）
     */
    @ApiOperation("就诊时间线")
    @GetMapping("/{id}/timeline")
    public R<List<Map<String, Object>>> timeline(
            @ApiParam(value = "患者ID", required = true) @PathVariable Long id,
            @ApiParam("诊所ID（忽略）") @RequestParam(required = false) Long clinicId,
            @ApiParam("开始时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
            @ApiParam("结束时间") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        return R.ok(profileService.visitTimeline(id, clinicId, beginTime, endTime));
    }

    /**
     * 查询患者基本信息详情；按当前所选诊所隔离。
     *
     * @param id 患者ID
     * @return 统一响应，data 为 {@link BizPatient} 实体
     */
    @ApiOperation("患者详情")
    @GetMapping("/{id}")
    public R<BizPatient> getInfo(@ApiParam(value = "患者ID", required = true) @PathVariable Long id) {
        return R.ok(patientService.selectById(id));
    }

    /**
     * 新增患者，clinicId 由后端自动写入当前所选诊所；按当前所选诊所隔离。
     *
     * @param patient 患者信息
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("新增患者")
    @PostMapping
    public R<?> add(@RequestBody BizPatient patient) {
        return patientService.insertPatient(patient) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改患者基本信息；按当前所选诊所隔离。
     *
     * @param patient 患者信息（须含 id）
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("修改患者")
    @PostMapping("/edit")
    public R<?> edit(@RequestBody BizPatient patient) {
        return patientService.updatePatient(patient) > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除指定患者；按当前所选诊所隔离。
     *
     * @param id 患者ID
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("删除患者")
    @PostMapping("/remove/{id}")
    public R<?> remove(@ApiParam(value = "患者ID", required = true) @PathVariable Long id) {
        return patientService.deletePatient(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 保存患者并执行后续动作（仅保存、保存并到店或保存并预约）；按当前所选诊所隔离。
     *
     * @param patient 患者信息
     * @param action  动作类型：save 仅保存 / arrive 保存并到店 / appoint 保存并预约，默认 save
     * @return 统一响应，data 为 {@link PatientActionVO}（含 patientId 与 action）
     */
    @ApiOperation("保存患者并执行动作")
    @PostMapping("/saveWithAction")
    public R<PatientActionVO> saveWithAction(
            @RequestBody BizPatient patient,
            @ApiParam("save|arrive|appoint") @RequestParam(defaultValue = "save") String action) {
        Long pid;
        if ("arrive".equalsIgnoreCase(action)) {
            pid = patientService.saveAndArrive(patient);
        } else if ("appoint".equalsIgnoreCase(action)) {
            pid = patientService.saveAndAppoint(patient);
        } else {
            patientService.insertPatient(patient);
            pid = patient.getId();
        }
        PatientActionVO vo = new PatientActionVO();
        vo.setPatientId(pid);
        vo.setAction(action);
        return R.ok(vo);
    }
}
