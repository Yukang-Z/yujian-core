package com.yujian.admin.controller;

import com.yujian.admin.service.IBizPatientService;
import com.yujian.common.biz.domain.BizPatient;
import com.yujian.common.core.domain.PageResult;
import com.yujian.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 患者管理
 */
@RestController
@RequestMapping("/biz/patient")
public class BizPatientController {

    @Autowired
    private IBizPatientService patientService;

    /**
     * 患者分页列表
     * keyword: 姓名/手机/病历号/拼音
     */
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

    /** 顶部全局搜索 */
    @GetMapping("/search")
    public R<List<BizPatient>> search(@RequestParam String keyword,
                                      @RequestParam(required = false) Long clinicId,
                                      @RequestParam(defaultValue = "20") int limit) {
        return R.ok(patientService.search(keyword, clinicId, limit));
    }

    @GetMapping("/{id}")
    public R<BizPatient> getInfo(@PathVariable Long id) {
        return R.ok(patientService.selectById(id));
    }

    /** 新增患者 */
    @PostMapping
    public R<?> add(@RequestBody BizPatient patient) {
        return patientService.insertPatient(patient) > 0 ? R.ok() : R.fail();
    }

    /** 修改患者 */
    @PutMapping
    public R<?> edit(@RequestBody BizPatient patient) {
        return patientService.updatePatient(patient) > 0 ? R.ok() : R.fail();
    }

    /** 删除患者 */
    @DeleteMapping("/{id}")
    public R<?> remove(@PathVariable Long id) {
        return patientService.deletePatient(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 保存并预约 / 保存并到达 / 普通保存
     * action: save | appoint | arrive
     */
    @PostMapping("/saveWithAction")
    public R<Map<String, Object>> saveWithAction(@RequestBody BizPatient patient,
                                                 @RequestParam(defaultValue = "save") String action) {
        Long id;
        if ("arrive".equalsIgnoreCase(action)) {
            id = patientService.saveAndArrive(patient);
        } else if ("appoint".equalsIgnoreCase(action)) {
            id = patientService.saveAndAppoint(patient);
        } else {
            patientService.insertPatient(patient);
            id = patient.getId();
        }
        java.util.HashMap<String, Object> map = new java.util.HashMap<String, Object>(4);
        map.put("patientId", id);
        map.put("action", action);
        return R.ok(map);
    }
}
