package com.yujian.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yujian.admin.mapper.*;
import com.yujian.admin.service.PatientLogHelper;
import com.yujian.common.biz.domain.*;
import com.yujian.common.core.context.SecurityContextHolder;
import com.yujian.common.core.domain.R;
import com.yujian.common.exception.BusinessException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 患者详情各 Tab 接口，涵盖病历、处置、收费、回访、影像文档、计划、咨询、亲友、日志及就诊；按当前所选诊所隔离。
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Api(tags = "患者详情Tab")
@RestController
@RequestMapping("/biz/patient")
public class BizPatientTabController {

    @Autowired
    private BizPatientRelationMapper relationMapper;
    @Autowired
    private BizPatientLogMapper patientLogMapper;
    @Autowired
    private BizVisitMapper visitMapper;
    @Autowired
    private BizMedicalRecordMapper medicalRecordMapper;
    @Autowired
    private BizTreatmentRecordMapper treatmentRecordMapper;
    @Autowired
    private BizChargeRecordMapper chargeRecordMapper;
    @Autowired
    private BizFollowUpMapper followUpMapper;
    @Autowired
    private BizPatientFileMapper patientFileMapper;
    @Autowired
    private BizTreatPlanMapper treatPlanMapper;
    @Autowired
    private BizConsultRecordMapper consultRecordMapper;
    @Autowired
    private BizPatientMapper patientMapper;
    @Autowired
    private PatientLogHelper patientLogHelper;

    /**
     * 查询指定患者的亲友关系列表；按当前所选诊所隔离。
     *
     * @param patientId 患者ID
     * @return 统一响应，data 为 {@link BizPatientRelation} 列表
     */
    @ApiOperation("亲友关系列表")
    @GetMapping("/{patientId}/relations")
    public R<List<BizPatientRelation>> relations(
            @ApiParam(value = "患者ID", required = true) @PathVariable Long patientId) {
        return R.ok(relationMapper.selectByPatientId(patientId));
    }

    /**
     * 新增亲友关系，未传 clinicId 时自动写入当前所选诊所；按当前所选诊所隔离。
     *
     * @param patientId 患者ID
     * @param relation  亲友关系信息（relatedId、relationType 必填）
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("新增亲友关系")
    @PostMapping("/{patientId}/relations")
    public R<?> addRelation(
            @ApiParam(value = "患者ID", required = true) @PathVariable Long patientId,
            @RequestBody BizPatientRelation relation) {
        relation.setPatientId(patientId);
        if (relation.getClinicId() == null) {
            relation.setClinicId(SecurityContextHolder.requireClinicId());
        }
        if (relation.getRelatedId() == null) {
            throw new BusinessException("关联患者不能为空");
        }
        if (StringUtils.isBlank(relation.getRelationType())) {
            throw new BusinessException("关系类型不能为空");
        }
        int rows = relationMapper.insert(relation);
        patientLogHelper.write(patientId, relation.getClinicId(), "relation", "添加亲友关系");
        return rows > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除指定亲友关系；按当前所选诊所隔离。
     *
     * @param id 亲友关系ID
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("删除亲友关系")
    @PostMapping("/relations/remove/{id}")
    public R<?> removeRelation(@ApiParam(value = "关系ID", required = true) @PathVariable Long id) {
        return relationMapper.deleteById(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 查询指定患者的操作日志，按时间倒序；按当前所选诊所隔离。
     *
     * @param patientId 患者ID
     * @return 统一响应，data 为 {@link BizPatientLog} 列表
     */
    @ApiOperation("患者操作日志")
    @GetMapping("/{patientId}/logs")
    public R<List<BizPatientLog>> logs(
            @ApiParam(value = "患者ID", required = true) @PathVariable Long patientId) {
        return R.ok(patientLogMapper.selectList(new LambdaQueryWrapper<BizPatientLog>()
                .eq(BizPatientLog::getPatientId, patientId)
                .orderByDesc(BizPatientLog::getId)));
    }

    /**
     * 查询指定患者的就诊记录列表，按开始时间倒序；按当前所选诊所隔离。
     *
     * @param patientId 患者ID
     * @return 统一响应，data 为 {@link BizVisit} 列表
     */
    @ApiOperation("就诊记录列表")
    @GetMapping("/{patientId}/visits")
    public R<List<BizVisit>> visits(
            @ApiParam(value = "患者ID", required = true) @PathVariable Long patientId) {
        return R.ok(visitMapper.selectList(new LambdaQueryWrapper<BizVisit>()
                .eq(BizVisit::getPatientId, patientId)
                .orderByDesc(BizVisit::getStartTime)));
    }

    /**
     * 新增就诊记录，未传 clinicId 时自动写入当前所选诊所；按当前所选诊所隔离。
     *
     * @param patientId 患者ID
     * @param visit     就诊信息
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("新增就诊记录")
    @PostMapping("/{patientId}/visits")
    public R<?> addVisit(
            @ApiParam(value = "患者ID", required = true) @PathVariable Long patientId,
            @RequestBody BizVisit visit) {
        visit.setPatientId(patientId);
        if (visit.getClinicId() == null) {
            visit.setClinicId(SecurityContextHolder.requireClinicId());
        }
        if (visit.getStartTime() == null) {
            visit.setStartTime(new Date());
        }
        if (visit.getVisitStatus() == null) {
            visit.setVisitStatus(1);
        }
        int rows = visitMapper.insert(visit);
        syncLastVisit(patientId, visit.getDoctorId(), visit.getStartTime());
        patientLogHelper.write(patientId, visit.getClinicId(), "visit", "新增就诊");
        return rows > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改就诊记录信息；按当前所选诊所隔离。
     *
     * @param visit 就诊信息（须含 id）
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("修改就诊记录")
    @PostMapping("/visits/edit")
    public R<?> editVisit(@RequestBody BizVisit visit) {
        return visitMapper.updateById(visit) > 0 ? R.ok() : R.fail();
    }

    /**
     * 查询指定患者的电子病历列表，支持按就诊类型与时间范围过滤；按当前所选诊所隔离。
     *
     * @param patientId 患者ID
     * @param visitType 就诊类型：1 初诊 / 2 复诊，可选
     * @param beginTime 就诊时间起，可选
     * @param endTime   就诊时间止，可选
     * @return 统一响应，data 为 {@link BizMedicalRecord} 列表
     */
    @ApiOperation("电子病历列表")
    @GetMapping("/{patientId}/medicalRecords")
    public R<List<BizMedicalRecord>> medicalRecords(
            @ApiParam(value = "患者ID", required = true) @PathVariable Long patientId,
            @ApiParam("就诊类型：1初诊 2复诊") @RequestParam(required = false) Integer visitType,
            @ApiParam("就诊时间起") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
            @ApiParam("就诊时间止") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        return R.ok(medicalRecordMapper.selectList(new LambdaQueryWrapper<BizMedicalRecord>()
                .eq(BizMedicalRecord::getPatientId, patientId)
                .eq(visitType != null, BizMedicalRecord::getVisitType, visitType)
                .ge(beginTime != null, BizMedicalRecord::getVisitTime, beginTime)
                .le(endTime != null, BizMedicalRecord::getVisitTime, endTime)
                .orderByDesc(BizMedicalRecord::getVisitTime)));
    }

    /**
     * 新增电子病历，未传 clinicId 时自动写入当前所选诊所；按当前所选诊所隔离。
     *
     * @param patientId 患者ID
     * @param record    病历内容
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("新增电子病历")
    @PostMapping("/{patientId}/medicalRecords")
    public R<?> addMedicalRecord(
            @ApiParam(value = "患者ID", required = true) @PathVariable Long patientId,
            @RequestBody BizMedicalRecord record) {
        record.setPatientId(patientId);
        fillClinic(record);
        if (record.getVisitTime() == null) {
            record.setVisitTime(new Date());
        }
        int rows = medicalRecordMapper.insert(record);
        patientLogHelper.write(patientId, record.getClinicId(), "medical", "新增病历");
        return rows > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改电子病历内容；按当前所选诊所隔离。
     *
     * @param record 病历信息（须含 id）
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("修改电子病历")
    @PostMapping("/medicalRecords/edit")
    public R<?> editMedicalRecord(@RequestBody BizMedicalRecord record) {
        int rows = medicalRecordMapper.updateById(record);
        if (rows > 0) {
            patientLogHelper.write(record.getPatientId(), record.getClinicId(), "medical", "修改病历");
        }
        return rows > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除指定电子病历；按当前所选诊所隔离。
     *
     * @param id 病历ID
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("删除电子病历")
    @PostMapping("/medicalRecords/remove/{id}")
    public R<?> removeMedicalRecord(@ApiParam(value = "病历ID", required = true) @PathVariable Long id) {
        return medicalRecordMapper.deleteById(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 查询指定患者的处置记录列表，按处置时间倒序；按当前所选诊所隔离。
     *
     * @param patientId 患者ID
     * @return 统一响应，data 为 {@link BizTreatmentRecord} 列表
     */
    @ApiOperation("处置记录列表")
    @GetMapping("/{patientId}/treatments")
    public R<List<BizTreatmentRecord>> treatments(
            @ApiParam(value = "患者ID", required = true) @PathVariable Long patientId) {
        return R.ok(treatmentRecordMapper.selectList(new LambdaQueryWrapper<BizTreatmentRecord>()
                .eq(BizTreatmentRecord::getPatientId, patientId)
                .orderByDesc(BizTreatmentRecord::getTreatTime)));
    }

    /**
     * 新增处置记录，未传 clinicId 时自动写入当前所选诊所；按当前所选诊所隔离。
     *
     * @param patientId 患者ID
     * @param record    处置信息
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("新增处置记录")
    @PostMapping("/{patientId}/treatments")
    public R<?> addTreatment(
            @ApiParam(value = "患者ID", required = true) @PathVariable Long patientId,
            @RequestBody BizTreatmentRecord record) {
        record.setPatientId(patientId);
        fillClinic(record);
        if (record.getTreatTime() == null) {
            record.setTreatTime(new Date());
        }
        int rows = treatmentRecordMapper.insert(record);
        syncLastVisit(patientId, record.getDoctorId(), record.getTreatTime());
        patientLogHelper.write(patientId, record.getClinicId(), "treatment", "新增处置");
        return rows > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改处置记录信息；按当前所选诊所隔离。
     *
     * @param record 处置信息（须含 id）
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("修改处置记录")
    @PostMapping("/treatments/edit")
    public R<?> editTreatment(@RequestBody BizTreatmentRecord record) {
        return treatmentRecordMapper.updateById(record) > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除指定处置记录；按当前所选诊所隔离。
     *
     * @param id 处置记录ID
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("删除处置记录")
    @PostMapping("/treatments/remove/{id}")
    public R<?> removeTreatment(@ApiParam(value = "处置记录ID", required = true) @PathVariable Long id) {
        return treatmentRecordMapper.deleteById(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 查询指定患者的收费记录列表，按收费时间倒序；按当前所选诊所隔离。
     *
     * @param patientId 患者ID
     * @return 统一响应，data 为 {@link BizChargeRecord} 列表
     */
    @ApiOperation("收费记录列表")
    @GetMapping("/{patientId}/charges")
    public R<List<BizChargeRecord>> charges(
            @ApiParam(value = "患者ID", required = true) @PathVariable Long patientId) {
        return R.ok(chargeRecordMapper.selectList(new LambdaQueryWrapper<BizChargeRecord>()
                .eq(BizChargeRecord::getPatientId, patientId)
                .orderByDesc(BizChargeRecord::getChargeTime)));
    }

    /**
     * 新增收费记录并回写患者累计消费金额，未传 clinicId 时自动写入当前所选诊所；按当前所选诊所隔离。
     *
     * @param patientId 患者ID
     * @param record    收费信息
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("新增收费记录")
    @PostMapping("/{patientId}/charges")
    public R<?> addCharge(
            @ApiParam(value = "患者ID", required = true) @PathVariable Long patientId,
            @RequestBody BizChargeRecord record) {
        record.setPatientId(patientId);
        fillClinic(record);
        if (StringUtils.isBlank(record.getChargeNo())) {
            record.setChargeNo("C" + System.currentTimeMillis());
        }
        if (record.getChargeTime() == null) {
            record.setChargeTime(new Date());
        }
        if (record.getTotalAmount() == null) {
            record.setTotalAmount(BigDecimal.ZERO);
        }
        if (record.getPaidAmount() == null) {
            record.setPaidAmount(BigDecimal.ZERO);
        }
        if (record.getOweAmount() == null) {
            record.setOweAmount(record.getTotalAmount().subtract(record.getPaidAmount()));
        }
        if (record.getChargeStatus() == null) {
            int cmp = record.getOweAmount().compareTo(BigDecimal.ZERO);
            record.setChargeStatus(cmp <= 0 ? 2 : (record.getPaidAmount().compareTo(BigDecimal.ZERO) > 0 ? 1 : 0));
        }
        int rows = chargeRecordMapper.insert(record);
        refreshPatientMoney(patientId);
        patientLogHelper.write(patientId, record.getClinicId(), "charge", "新增收费");
        return rows > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改收费记录并回写患者累计消费金额；按当前所选诊所隔离。
     *
     * @param record 收费信息（须含 id）
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("修改收费记录")
    @PostMapping("/charges/edit")
    public R<?> editCharge(@RequestBody BizChargeRecord record) {
        int rows = chargeRecordMapper.updateById(record);
        if (rows > 0 && record.getPatientId() != null) {
            refreshPatientMoney(record.getPatientId());
        }
        return rows > 0 ? R.ok() : R.fail();
    }

    /**
     * 查询指定患者的回访计划列表，按计划时间倒序；按当前所选诊所隔离。
     *
     * @param patientId 患者ID
     * @return 统一响应，data 为 {@link BizFollowUp} 列表
     */
    @ApiOperation("回访列表")
    @GetMapping("/{patientId}/followUps")
    public R<List<BizFollowUp>> followUps(
            @ApiParam(value = "患者ID", required = true) @PathVariable Long patientId) {
        return R.ok(followUpMapper.selectList(new LambdaQueryWrapper<BizFollowUp>()
                .eq(BizFollowUp::getPatientId, patientId)
                .orderByDesc(BizFollowUp::getPlanTime)));
    }

    /**
     * 新增回访计划，未传 clinicId 时自动写入当前所选诊所；按当前所选诊所隔离。
     *
     * @param patientId 患者ID
     * @param followUp  回访信息
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("新增回访")
    @PostMapping("/{patientId}/followUps")
    public R<?> addFollowUp(
            @ApiParam(value = "患者ID", required = true) @PathVariable Long patientId,
            @RequestBody BizFollowUp followUp) {
        followUp.setPatientId(patientId);
        fillClinic(followUp);
        if (followUp.getFollowStatus() == null) {
            followUp.setFollowStatus(0);
        }
        int rows = followUpMapper.insert(followUp);
        patientLogHelper.write(patientId, followUp.getClinicId(), "followUp", "新增回访");
        return rows > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改回访计划信息；按当前所选诊所隔离。
     *
     * @param followUp 回访信息（须含 id）
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("修改回访")
    @PostMapping("/followUps/edit")
    public R<?> editFollowUp(@RequestBody BizFollowUp followUp) {
        return followUpMapper.updateById(followUp) > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除指定回访计划；按当前所选诊所隔离。
     *
     * @param id 回访ID
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("删除回访")
    @PostMapping("/followUps/remove/{id}")
    public R<?> removeFollowUp(@ApiParam(value = "回访ID", required = true) @PathVariable Long id) {
        return followUpMapper.deleteById(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 查询指定患者的影像/文档/协议附件，支持按分类、类型及上传时间过滤；按当前所选诊所隔离。
     *
     * @param patientId    患者ID
     * @param fileCategory 文件分类：image 图片 / document 文档 / agreement 协议，可选
     * @param fileType     文件类型，可选
     * @param beginTime    上传时间起，可选
     * @param endTime      上传时间止，可选
     * @return 统一响应，data 为 {@link BizPatientFile} 列表
     */
    @ApiOperation("患者附件列表")
    @GetMapping("/{patientId}/files")
    public R<List<BizPatientFile>> files(
            @ApiParam(value = "患者ID", required = true) @PathVariable Long patientId,
            @ApiParam("分类：image图片 / document文档 / agreement协议") @RequestParam(required = false) String fileCategory,
            @ApiParam("文件类型") @RequestParam(required = false) String fileType,
            @ApiParam("上传时间起") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
            @ApiParam("上传时间止") @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        return R.ok(patientFileMapper.selectList(new LambdaQueryWrapper<BizPatientFile>()
                .eq(BizPatientFile::getPatientId, patientId)
                .eq(StringUtils.isNotBlank(fileCategory), BizPatientFile::getFileCategory, fileCategory)
                .eq(StringUtils.isNotBlank(fileType), BizPatientFile::getFileType, fileType)
                .ge(beginTime != null, BizPatientFile::getUploadTime, beginTime)
                .le(endTime != null, BizPatientFile::getUploadTime, endTime)
                .orderByDesc(BizPatientFile::getUploadTime)));
    }

    /**
     * 新增患者附件，未传 clinicId 时自动写入当前所选诊所；按当前所选诊所隔离。
     *
     * @param patientId 患者ID
     * @param file      附件信息（fileUrl 必填）
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("新增患者附件")
    @PostMapping("/{patientId}/files")
    public R<?> addFile(
            @ApiParam(value = "患者ID", required = true) @PathVariable Long patientId,
            @RequestBody BizPatientFile file) {
        file.setPatientId(patientId);
        if (file.getClinicId() == null) {
            file.setClinicId(SecurityContextHolder.requireClinicId());
        }
        if (StringUtils.isBlank(file.getFileUrl())) {
            throw new BusinessException("文件地址不能为空");
        }
        if (StringUtils.isBlank(file.getFileCategory())) {
            file.setFileCategory("document");
        }
        if (file.getUploadTime() == null) {
            file.setUploadTime(new Date());
        }
        int rows = patientFileMapper.insert(file);
        patientLogHelper.write(patientId, file.getClinicId(), "file", "上传附件:" + file.getFileCategory());
        return rows > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除指定患者附件；按当前所选诊所隔离。
     *
     * @param id 附件ID
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("删除患者附件")
    @PostMapping("/files/remove/{id}")
    public R<?> removeFile(@ApiParam(value = "附件ID", required = true) @PathVariable Long id) {
        return patientFileMapper.deleteById(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 查询指定患者的治疗计划列表；按当前所选诊所隔离。
     *
     * @param patientId 患者ID
     * @return 统一响应，data 为 {@link BizTreatPlan} 列表
     */
    @ApiOperation("治疗计划列表")
    @GetMapping("/{patientId}/plans")
    public R<List<BizTreatPlan>> plans(
            @ApiParam(value = "患者ID", required = true) @PathVariable Long patientId) {
        return R.ok(treatPlanMapper.selectList(new LambdaQueryWrapper<BizTreatPlan>()
                .eq(BizTreatPlan::getPatientId, patientId)
                .orderByDesc(BizTreatPlan::getId)));
    }

    /**
     * 新增治疗计划，未传 clinicId 时自动写入当前所选诊所；按当前所选诊所隔离。
     *
     * @param patientId 患者ID
     * @param plan      治疗计划内容
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("新增治疗计划")
    @PostMapping("/{patientId}/plans")
    public R<?> addPlan(
            @ApiParam(value = "患者ID", required = true) @PathVariable Long patientId,
            @RequestBody BizTreatPlan plan) {
        plan.setPatientId(patientId);
        fillClinic(plan);
        if (plan.getPlanStatus() == null) {
            plan.setPlanStatus(0);
        }
        return treatPlanMapper.insert(plan) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改治疗计划信息；按当前所选诊所隔离。
     *
     * @param plan 治疗计划（须含 id）
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("修改治疗计划")
    @PostMapping("/plans/edit")
    public R<?> editPlan(@RequestBody BizTreatPlan plan) {
        return treatPlanMapper.updateById(plan) > 0 ? R.ok() : R.fail();
    }

    /**
     * 删除指定治疗计划；按当前所选诊所隔离。
     *
     * @param id 治疗计划ID
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("删除治疗计划")
    @PostMapping("/plans/remove/{id}")
    public R<?> removePlan(@ApiParam(value = "计划ID", required = true) @PathVariable Long id) {
        return treatPlanMapper.deleteById(id) > 0 ? R.ok() : R.fail();
    }

    /**
     * 查询指定患者的咨询沟通记录列表，按咨询时间倒序；按当前所选诊所隔离。
     *
     * @param patientId 患者ID
     * @return 统一响应，data 为 {@link BizConsultRecord} 列表
     */
    @ApiOperation("咨询沟通列表")
    @GetMapping("/{patientId}/consults")
    public R<List<BizConsultRecord>> consults(
            @ApiParam(value = "患者ID", required = true) @PathVariable Long patientId) {
        return R.ok(consultRecordMapper.selectList(new LambdaQueryWrapper<BizConsultRecord>()
                .eq(BizConsultRecord::getPatientId, patientId)
                .orderByDesc(BizConsultRecord::getConsultTime)));
    }

    /**
     * 新增咨询沟通记录，未传 clinicId 时自动写入当前所选诊所；按当前所选诊所隔离。
     *
     * @param patientId 患者ID
     * @param record    咨询内容
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("新增咨询沟通")
    @PostMapping("/{patientId}/consults")
    public R<?> addConsult(
            @ApiParam(value = "患者ID", required = true) @PathVariable Long patientId,
            @RequestBody BizConsultRecord record) {
        record.setPatientId(patientId);
        fillClinic(record);
        if (record.getConsultTime() == null) {
            record.setConsultTime(new Date());
        }
        return consultRecordMapper.insert(record) > 0 ? R.ok() : R.fail();
    }

    /**
     * 修改咨询沟通记录；按当前所选诊所隔离。
     *
     * @param record 咨询记录（须含 id）
     * @return 统一响应，成功时 data 为空，失败时含错误提示
     */
    @ApiOperation("修改咨询沟通")
    @PostMapping("/consults/edit")
    public R<?> editConsult(@RequestBody BizConsultRecord record) {
        return consultRecordMapper.updateById(record) > 0 ? R.ok() : R.fail();
    }

    /**
     * 电子病历未传 clinicId 时，自动写入当前所选诊所。
     *
     * @param r 电子病历实体
     */
    private void fillClinic(BizMedicalRecord r) {
        if (r.getClinicId() == null) {
            r.setClinicId(SecurityContextHolder.requireClinicId());
        }
    }

    /**
     * 处置记录未传 clinicId 时，自动写入当前所选诊所。
     *
     * @param r 处置记录实体
     */
    private void fillClinic(BizTreatmentRecord r) {
        if (r.getClinicId() == null) {
            r.setClinicId(SecurityContextHolder.requireClinicId());
        }
    }

    /**
     * 收费记录未传 clinicId 时，自动写入当前所选诊所。
     *
     * @param r 收费记录实体
     */
    private void fillClinic(BizChargeRecord r) {
        if (r.getClinicId() == null) {
            r.setClinicId(SecurityContextHolder.requireClinicId());
        }
    }

    /**
     * 回访计划未传 clinicId 时，自动写入当前所选诊所。
     *
     * @param r 回访实体
     */
    private void fillClinic(BizFollowUp r) {
        if (r.getClinicId() == null) {
            r.setClinicId(SecurityContextHolder.requireClinicId());
        }
    }

    /**
     * 治疗计划未传 clinicId 时，自动写入当前所选诊所。
     *
     * @param r 治疗计划实体
     */
    private void fillClinic(BizTreatPlan r) {
        if (r.getClinicId() == null) {
            r.setClinicId(SecurityContextHolder.requireClinicId());
        }
    }

    /**
     * 咨询记录未传 clinicId 时，自动写入当前所选诊所。
     *
     * @param r 咨询记录实体
     */
    private void fillClinic(BizConsultRecord r) {
        if (r.getClinicId() == null) {
            r.setClinicId(SecurityContextHolder.requireClinicId());
        }
    }

    /**
     * 回写患者最近就诊时间与主治医生。
     *
     * @param patientId 患者ID
     * @param doctorId  医生ID，可为 null
     * @param time      就诊或处置时间
     */
    private void syncLastVisit(Long patientId, Long doctorId, Date time) {
        BizPatient update = new BizPatient();
        update.setId(patientId);
        update.setLastVisitTime(time);
        if (doctorId != null) {
            update.setLastDoctorId(doctorId);
        }
        patientMapper.updateById(update);
    }

    /**
     * 按收费明细汇总并回写患者累计消费、已付及欠费金额。
     *
     * @param patientId 患者ID
     */
    private void refreshPatientMoney(Long patientId) {
        List<BizChargeRecord> list = chargeRecordMapper.selectList(new LambdaQueryWrapper<BizChargeRecord>()
                .eq(BizChargeRecord::getPatientId, patientId));
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal paid = BigDecimal.ZERO;
        BigDecimal owe = BigDecimal.ZERO;
        if (list != null) {
            for (BizChargeRecord c : list) {
                if (c.getTotalAmount() != null) {
                    total = total.add(c.getTotalAmount());
                }
                if (c.getPaidAmount() != null) {
                    paid = paid.add(c.getPaidAmount());
                }
                if (c.getOweAmount() != null) {
                    owe = owe.add(c.getOweAmount());
                }
            }
        }
        BizPatient update = new BizPatient();
        update.setId(patientId);
        update.setTotalAmount(total);
        update.setPaidAmount(paid);
        update.setOweAmount(owe);
        if (list != null && !list.isEmpty() && total.compareTo(BigDecimal.ZERO) > 0) {
            update.setAvgAmount(total.divide(new BigDecimal(list.size()), 2, java.math.RoundingMode.HALF_UP));
        }
        patientMapper.updateById(update);
    }
}
