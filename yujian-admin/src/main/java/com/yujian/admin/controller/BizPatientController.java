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
 * 患者管理接口（按当前所选诊所隔离）
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
     * 患者分页列表
     *
     * @param keyword       姓名/手机/病历号
     * @param clinicId      忽略，以当前诊所为准
     * @param doctorId      主治医生
     * @param firstDoctorId 初诊医生
     * @param tagId         标签
     * @param pageNum       页码
     * @param pageSize      每页条数
     * @return 分页数据
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
     * 左侧患者列表
     *
     * @param type     today今日 / all全部 / recent最近
     * @param clinicId 忽略
     * @param keyword  关键字
     * @param day      今日列表对应日期
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 分页数据
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
     * 顶部全局搜索患者
     *
     * @param keyword  关键字，必填
     * @param clinicId 忽略
     * @param limit    条数上限
     * @return 患者列表
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
     * 档案聚合：详情头 + 价值卡片 + 最近日志
     *
     * @param id 患者ID
     * @return patient / cards / logs
     */
    @ApiOperation("患者档案聚合")
    @GetMapping("/{id}/profile")
    public R<Map<String, Object>> profile(@ApiParam(value = "患者ID", required = true) @PathVariable Long id) {
        return R.ok(profileService.profile(id));
    }

    /**
     * 就诊时间线
     *
     * @param id        患者ID
     * @param clinicId  忽略
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 时间线
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
     * 患者详情
     *
     * @param id 患者ID
     * @return 患者
     */
    @ApiOperation("患者详情")
    @GetMapping("/{id}")
    public R<BizPatient> getInfo(@ApiParam(value = "患者ID", required = true) @PathVariable Long id) {
        return R.ok(patientService.selectById(id));
    }

    /**
     * 新增患者（clinicId 由后端写入当前诊所）
     *
     * @param patient 患者信息
     * @return 操作结果
     */
    @ApiOperation("新增患者")
    @PostMapping
    public R<?> add(@RequestBody BizPatient patient) {
        return patientService.insertPatient(patient) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改患者
     *
     * @param patient 患者信息（须含 id）
     * @return 操作结果
     */
    @ApiOperation("修改患者")
    @PostMapping("/edit")
    public R<?> edit(@RequestBody BizPatient patient) {
        return patientService.updatePatient(patient) > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除患者
     *
     * @param id 患者ID
     * @return 操作结果
     */
    @ApiOperation("删除患者")
    @PostMapping("/remove/{id}")
    public R<?> remove(@ApiParam(value = "患者ID", required = true) @PathVariable Long id) {
        return patientService.deletePatient(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 保存患者并执行动作
     *
     * @param patient 患者信息
     * @param action  save仅保存 / arrive保存并到店 / appoint保存并预约
     * @return 患者ID与动作
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
