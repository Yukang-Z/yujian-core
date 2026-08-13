package com.yujian.admin.controller;

import com.yujian.admin.service.IBizBasicDataService;
import com.yujian.common.biz.domain.BizDictData;
import com.yujian.common.biz.domain.BizDictType;
import com.yujian.common.biz.domain.BizPatientSource;
import com.yujian.common.biz.domain.BizPatientTag;
import com.yujian.common.biz.domain.BizTreatItem;
import com.yujian.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 基础数据管理
 */
@RestController
@RequestMapping("/biz/basic")
public class BizBasicDataController {

    @Autowired
    private IBizBasicDataService basicDataService;

    // ---------- 字典 ----------

    @GetMapping("/dict/types")
    public R<List<BizDictType>> dictTypes() {
        return R.ok(basicDataService.selectDictTypeList());
    }

    @GetMapping("/dict/{dictType}")
    public R<List<BizDictData>> dictData(@PathVariable String dictType) {
        return R.ok(basicDataService.selectDictByType(dictType));
    }

    @PostMapping("/dict/data")
    public R<?> saveDictData(@RequestBody BizDictData data) {
        return basicDataService.saveDictData(data) > 0 ? R.ok() : R.fail();
    }

    @DeleteMapping("/dict/data/{id}")
    public R<?> removeDictData(@PathVariable Long id) {
        return basicDataService.deleteDictData(id) > 0 ? R.ok() : R.fail();
    }

    // ---------- 患者标签 ----------

    @GetMapping("/tag/list")
    public R<List<BizPatientTag>> tagList(@RequestParam(required = false) Long clinicId) {
        return R.ok(basicDataService.selectTagList(clinicId));
    }

    @PostMapping("/tag")
    public R<?> saveTag(@RequestBody BizPatientTag tag) {
        return basicDataService.saveTag(tag) > 0 ? R.ok() : R.fail();
    }

    @DeleteMapping("/tag/{id}")
    public R<?> removeTag(@PathVariable Long id) {
        return basicDataService.deleteTag(id) > 0 ? R.ok() : R.fail();
    }

    // ---------- 患者来源 ----------

    @GetMapping("/source/tree")
    public R<List<BizPatientSource>> sourceTree(@RequestParam(required = false) Long clinicId) {
        return R.ok(basicDataService.selectSourceTree(clinicId));
    }

    @PostMapping("/source")
    public R<?> saveSource(@RequestBody BizPatientSource source) {
        return basicDataService.saveSource(source) > 0 ? R.ok() : R.fail();
    }

    @DeleteMapping("/source/{id}")
    public R<?> removeSource(@PathVariable Long id) {
        return basicDataService.deleteSource(id) > 0 ? R.ok() : R.fail();
    }

    // ---------- 诊疗项目 ----------

    @GetMapping("/item/list")
    public R<List<BizTreatItem>> itemList(@RequestParam(required = false) Long clinicId) {
        return R.ok(basicDataService.selectTreatItemList(clinicId));
    }

    @PostMapping("/item")
    public R<?> saveItem(@RequestBody BizTreatItem item) {
        return basicDataService.saveTreatItem(item) > 0 ? R.ok() : R.fail();
    }

    @DeleteMapping("/item/{id}")
    public R<?> removeItem(@PathVariable Long id) {
        return basicDataService.deleteTreatItem(id) > 0 ? R.ok() : R.fail();
    }

    // ---------- 医生列表（日历列） ----------

    @GetMapping("/doctor/list")
    public R<List<?>> doctorList(@RequestParam(required = false) Long clinicId) {
        return R.ok(basicDataService.selectDoctorList(clinicId));
    }
}
