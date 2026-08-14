package com.yujian.admin.controller;

import com.yujian.admin.service.IBizPatientProfileService;
import com.yujian.admin.service.IBizPatientService;
import com.yujian.common.biz.domain.BizPatient;
import com.yujian.common.core.domain.PageResult;
import com.yujian.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 患者管理（列表 + 档案头 + 侧栏）
 */
@RestController
@RequestMapping("/biz/patient")
public class BizPatientController {

    @Autowired
    private IBizPatientService patientService;

    @Autowired
    private IBizPatientProfileService profileService;

    /** 患者分页列表 */
    @GetMapping("/list")
    public R<PageResult<BizPatient>> list(@RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) Long clinicId,
                                          @RequestParam(required = false) Long doctorId,
                                          @RequestParam(required = false) Long firstDoctorId,
                                          @RequestParam(required = false) Long tagId,
                                          @RequestParam(defaultValue = "1") long pageNum,
                                          @RequestParam(defaultValue = "20") long pageSize) {
        return R.ok(patientService.selectPage(keyword, clinicId, doctorId, firstDoctorId, tagId, pageNum, pageSize));
    }

    /**
     * 左侧患者列表：今日 / 全部 / 最近
     * type: today | all | recent
     */
    @GetMapping("/sidebar")
    public R<PageResult<BizPatient>> sidebar(@RequestParam(defaultValue = "all") String type,
                                             @RequestParam(required = false) Long clinicId,
                                             @RequestParam(required = false) String keyword,
                                             @RequestParam(required = false)
                                             @DateTimeFormat(pattern = "yyyy-MM-dd") Date day,
                                             @RequestParam(defaultValue = "1") long pageNum,
                                             @RequestParam(defaultValue = "50") long pageSize) {
        return R.ok(profileService.sidebar(type, clinicId, keyword, day, pageNum, pageSize));
    }

    /** 顶部全局搜索 */
    @GetMapping("/search")
    public R<List<BizPatient>> search(@RequestParam String keyword,
                                      @RequestParam(required = false) Long clinicId,
                                      @RequestParam(defaultValue = "20") int limit) {
        return R.ok(patientService.search(keyword, clinicId, limit));
    }

    /** 档案聚合（详情头 + 价值卡片 + 最近日志） */
    @GetMapping("/{id}/profile")
    public R<Map<String, Object>> profile(@PathVariable Long id) {
        return R.ok(profileService.profile(id));
    }

    /** 就诊信息时间线 */
    @GetMapping("/{id}/timeline")
    public R<List<Map<String, Object>>> timeline(@PathVariable Long id,
                                                 @RequestParam(required = false) Long clinicId,
                                                 @RequestParam(required = false)
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
                                                 @RequestParam(required = false)
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        return R.ok(profileService.visitTimeline(id, clinicId, beginTime, endTime));
    }

    @GetMapping("/{id}")
    public R<BizPatient> getInfo(@PathVariable Long id) {
        return R.ok(patientService.selectById(id));
    }

    @PostMapping
    public R<?> add(@RequestBody BizPatient patient) {
        return patientService.insertPatient(patient) > 0 ? R.ok() : R.fail();
    }

    @PutMapping
    public R<?> edit(@RequestBody BizPatient patient) {
        return patientService.updatePatient(patient) > 0 ? R.ok() : R.fail();
    }

    @DeleteMapping("/{id}")
    public R<?> remove(@PathVariable Long id) {
        return patientService.deletePatient(id) > 0 ? R.ok() : R.fail();
    }

    @PostMapping("/saveWithAction")
    public R<Map<String, Object>> saveWithAction(@RequestBody BizPatient patient,
                                                 @RequestParam(defaultValue = "save") String action) {
        Long pid;
        if ("arrive".equalsIgnoreCase(action)) {
            pid = patientService.saveAndArrive(patient);
        } else if ("appoint".equalsIgnoreCase(action)) {
            pid = patientService.saveAndAppoint(patient);
        } else {
            patientService.insertPatient(patient);
            pid = patient.getId();
        }
        java.util.HashMap<String, Object> map = new java.util.HashMap<String, Object>(4);
        map.put("patientId", pid);
        map.put("action", action);
        return R.ok(map);
    }
}
