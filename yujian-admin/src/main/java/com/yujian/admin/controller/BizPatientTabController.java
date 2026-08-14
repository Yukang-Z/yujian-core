package com.yujian.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yujian.admin.mapper.*;
import com.yujian.admin.service.PatientLogHelper;
import com.yujian.common.biz.domain.*;
import com.yujian.common.core.context.SecurityContextHolder;
import com.yujian.common.core.domain.R;
import com.yujian.common.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 患者详情各 Tab：病历 / 处置 / 收费 / 回访 / 影像文档 / 计划 / 咨询 / 亲友 / 日志 / 就诊
 */
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

    // ---------- 亲友关系 ----------
    @GetMapping("/{patientId}/relations")
    public R<List<BizPatientRelation>> relations(@PathVariable Long patientId) {
        return R.ok(relationMapper.selectByPatientId(patientId));
    }

    @PostMapping("/{patientId}/relations")
    public R<?> addRelation(@PathVariable Long patientId, @RequestBody BizPatientRelation relation) {
        relation.setPatientId(patientId);
        if (relation.getClinicId() == null) {
            relation.setClinicId(SecurityContextHolder.getClinicId());
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

    @DeleteMapping("/relations/{id}")
    public R<?> removeRelation(@PathVariable Long id) {
        return relationMapper.deleteById(id) > 0 ? R.ok() : R.fail();
    }

    // ---------- 操作日志 ----------
    @GetMapping("/{patientId}/logs")
    public R<List<BizPatientLog>> logs(@PathVariable Long patientId) {
        return R.ok(patientLogMapper.selectList(new LambdaQueryWrapper<BizPatientLog>()
                .eq(BizPatientLog::getPatientId, patientId)
                .orderByDesc(BizPatientLog::getId)));
    }

    // ---------- 就诊记录 ----------
    @GetMapping("/{patientId}/visits")
    public R<List<BizVisit>> visits(@PathVariable Long patientId) {
        return R.ok(visitMapper.selectList(new LambdaQueryWrapper<BizVisit>()
                .eq(BizVisit::getPatientId, patientId)
                .orderByDesc(BizVisit::getStartTime)));
    }

    @PostMapping("/{patientId}/visits")
    public R<?> addVisit(@PathVariable Long patientId, @RequestBody BizVisit visit) {
        visit.setPatientId(patientId);
        if (visit.getClinicId() == null) {
            visit.setClinicId(SecurityContextHolder.getClinicId());
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

    @PutMapping("/visits")
    public R<?> editVisit(@RequestBody BizVisit visit) {
        return visitMapper.updateById(visit) > 0 ? R.ok() : R.fail();
    }

    // ---------- 电子病历 ----------
    @GetMapping("/{patientId}/medicalRecords")
    public R<List<BizMedicalRecord>> medicalRecords(@PathVariable Long patientId,
                                                    @RequestParam(required = false) Integer visitType,
                                                    @RequestParam(required = false)
                                                    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
                                                    @RequestParam(required = false)
                                                    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        return R.ok(medicalRecordMapper.selectList(new LambdaQueryWrapper<BizMedicalRecord>()
                .eq(BizMedicalRecord::getPatientId, patientId)
                .eq(visitType != null, BizMedicalRecord::getVisitType, visitType)
                .ge(beginTime != null, BizMedicalRecord::getVisitTime, beginTime)
                .le(endTime != null, BizMedicalRecord::getVisitTime, endTime)
                .orderByDesc(BizMedicalRecord::getVisitTime)));
    }

    @PostMapping("/{patientId}/medicalRecords")
    public R<?> addMedicalRecord(@PathVariable Long patientId, @RequestBody BizMedicalRecord record) {
        record.setPatientId(patientId);
        fillClinic(record);
        if (record.getVisitTime() == null) {
            record.setVisitTime(new Date());
        }
        int rows = medicalRecordMapper.insert(record);
        patientLogHelper.write(patientId, record.getClinicId(), "medical", "新增病历");
        return rows > 0 ? R.ok() : R.fail();
    }

    @PutMapping("/medicalRecords")
    public R<?> editMedicalRecord(@RequestBody BizMedicalRecord record) {
        int rows = medicalRecordMapper.updateById(record);
        if (rows > 0) {
            patientLogHelper.write(record.getPatientId(), record.getClinicId(), "medical", "修改病历");
        }
        return rows > 0 ? R.ok() : R.fail();
    }

    @DeleteMapping("/medicalRecords/{id}")
    public R<?> removeMedicalRecord(@PathVariable Long id) {
        return medicalRecordMapper.deleteById(id) > 0 ? R.ok() : R.fail();
    }

    // ---------- 处置记录 ----------
    @GetMapping("/{patientId}/treatments")
    public R<List<BizTreatmentRecord>> treatments(@PathVariable Long patientId) {
        return R.ok(treatmentRecordMapper.selectList(new LambdaQueryWrapper<BizTreatmentRecord>()
                .eq(BizTreatmentRecord::getPatientId, patientId)
                .orderByDesc(BizTreatmentRecord::getTreatTime)));
    }

    @PostMapping("/{patientId}/treatments")
    public R<?> addTreatment(@PathVariable Long patientId, @RequestBody BizTreatmentRecord record) {
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

    @PutMapping("/treatments")
    public R<?> editTreatment(@RequestBody BizTreatmentRecord record) {
        return treatmentRecordMapper.updateById(record) > 0 ? R.ok() : R.fail();
    }

    @DeleteMapping("/treatments/{id}")
    public R<?> removeTreatment(@PathVariable Long id) {
        return treatmentRecordMapper.deleteById(id) > 0 ? R.ok() : R.fail();
    }

    // ---------- 收费信息 ----------
    @GetMapping("/{patientId}/charges")
    public R<List<BizChargeRecord>> charges(@PathVariable Long patientId) {
        return R.ok(chargeRecordMapper.selectList(new LambdaQueryWrapper<BizChargeRecord>()
                .eq(BizChargeRecord::getPatientId, patientId)
                .orderByDesc(BizChargeRecord::getChargeTime)));
    }

    @PostMapping("/{patientId}/charges")
    public R<?> addCharge(@PathVariable Long patientId, @RequestBody BizChargeRecord record) {
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

    @PutMapping("/charges")
    public R<?> editCharge(@RequestBody BizChargeRecord record) {
        int rows = chargeRecordMapper.updateById(record);
        if (rows > 0 && record.getPatientId() != null) {
            refreshPatientMoney(record.getPatientId());
        }
        return rows > 0 ? R.ok() : R.fail();
    }

    // ---------- 回访 ----------
    @GetMapping("/{patientId}/followUps")
    public R<List<BizFollowUp>> followUps(@PathVariable Long patientId) {
        return R.ok(followUpMapper.selectList(new LambdaQueryWrapper<BizFollowUp>()
                .eq(BizFollowUp::getPatientId, patientId)
                .orderByDesc(BizFollowUp::getPlanTime)));
    }

    @PostMapping("/{patientId}/followUps")
    public R<?> addFollowUp(@PathVariable Long patientId, @RequestBody BizFollowUp followUp) {
        followUp.setPatientId(patientId);
        fillClinic(followUp);
        if (followUp.getFollowStatus() == null) {
            followUp.setFollowStatus(0);
        }
        int rows = followUpMapper.insert(followUp);
        patientLogHelper.write(patientId, followUp.getClinicId(), "followUp", "新增回访");
        return rows > 0 ? R.ok() : R.fail();
    }

    @PutMapping("/followUps")
    public R<?> editFollowUp(@RequestBody BizFollowUp followUp) {
        return followUpMapper.updateById(followUp) > 0 ? R.ok() : R.fail();
    }

    @DeleteMapping("/followUps/{id}")
    public R<?> removeFollowUp(@PathVariable Long id) {
        return followUpMapper.deleteById(id) > 0 ? R.ok() : R.fail();
    }

    // ---------- 影像 / 文档 / 协议 ----------
    @GetMapping("/{patientId}/files")
    public R<List<BizPatientFile>> files(@PathVariable Long patientId,
                                         @RequestParam(required = false) String fileCategory,
                                         @RequestParam(required = false) String fileType,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
                                         @RequestParam(required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        return R.ok(patientFileMapper.selectList(new LambdaQueryWrapper<BizPatientFile>()
                .eq(BizPatientFile::getPatientId, patientId)
                .eq(StringUtils.isNotBlank(fileCategory), BizPatientFile::getFileCategory, fileCategory)
                .eq(StringUtils.isNotBlank(fileType), BizPatientFile::getFileType, fileType)
                .ge(beginTime != null, BizPatientFile::getUploadTime, beginTime)
                .le(endTime != null, BizPatientFile::getUploadTime, endTime)
                .orderByDesc(BizPatientFile::getUploadTime)));
    }

    @PostMapping("/{patientId}/files")
    public R<?> addFile(@PathVariable Long patientId, @RequestBody BizPatientFile file) {
        file.setPatientId(patientId);
        if (file.getClinicId() == null) {
            file.setClinicId(SecurityContextHolder.getClinicId());
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

    @DeleteMapping("/files/{id}")
    public R<?> removeFile(@PathVariable Long id) {
        return patientFileMapper.deleteById(id) > 0 ? R.ok() : R.fail();
    }

    // ---------- 治疗计划 ----------
    @GetMapping("/{patientId}/plans")
    public R<List<BizTreatPlan>> plans(@PathVariable Long patientId) {
        return R.ok(treatPlanMapper.selectList(new LambdaQueryWrapper<BizTreatPlan>()
                .eq(BizTreatPlan::getPatientId, patientId)
                .orderByDesc(BizTreatPlan::getId)));
    }

    @PostMapping("/{patientId}/plans")
    public R<?> addPlan(@PathVariable Long patientId, @RequestBody BizTreatPlan plan) {
        plan.setPatientId(patientId);
        fillClinic(plan);
        if (plan.getPlanStatus() == null) {
            plan.setPlanStatus(0);
        }
        return treatPlanMapper.insert(plan) > 0 ? R.ok() : R.fail();
    }

    @PutMapping("/plans")
    public R<?> editPlan(@RequestBody BizTreatPlan plan) {
        return treatPlanMapper.updateById(plan) > 0 ? R.ok() : R.fail();
    }

    @DeleteMapping("/plans/{id}")
    public R<?> removePlan(@PathVariable Long id) {
        return treatPlanMapper.deleteById(id) > 0 ? R.ok() : R.fail();
    }

    // ---------- 咨询沟通 ----------
    @GetMapping("/{patientId}/consults")
    public R<List<BizConsultRecord>> consults(@PathVariable Long patientId) {
        return R.ok(consultRecordMapper.selectList(new LambdaQueryWrapper<BizConsultRecord>()
                .eq(BizConsultRecord::getPatientId, patientId)
                .orderByDesc(BizConsultRecord::getConsultTime)));
    }

    @PostMapping("/{patientId}/consults")
    public R<?> addConsult(@PathVariable Long patientId, @RequestBody BizConsultRecord record) {
        record.setPatientId(patientId);
        fillClinic(record);
        if (record.getConsultTime() == null) {
            record.setConsultTime(new Date());
        }
        return consultRecordMapper.insert(record) > 0 ? R.ok() : R.fail();
    }

    @PutMapping("/consults")
    public R<?> editConsult(@RequestBody BizConsultRecord record) {
        return consultRecordMapper.updateById(record) > 0 ? R.ok() : R.fail();
    }

    // ---------- helpers ----------
    private void fillClinic(BizMedicalRecord r) {
        if (r.getClinicId() == null) {
            r.setClinicId(SecurityContextHolder.getClinicId());
        }
    }

    private void fillClinic(BizTreatmentRecord r) {
        if (r.getClinicId() == null) {
            r.setClinicId(SecurityContextHolder.getClinicId());
        }
    }

    private void fillClinic(BizChargeRecord r) {
        if (r.getClinicId() == null) {
            r.setClinicId(SecurityContextHolder.getClinicId());
        }
    }

    private void fillClinic(BizFollowUp r) {
        if (r.getClinicId() == null) {
            r.setClinicId(SecurityContextHolder.getClinicId());
        }
    }

    private void fillClinic(BizTreatPlan r) {
        if (r.getClinicId() == null) {
            r.setClinicId(SecurityContextHolder.getClinicId());
        }
    }

    private void fillClinic(BizConsultRecord r) {
        if (r.getClinicId() == null) {
            r.setClinicId(SecurityContextHolder.getClinicId());
        }
    }

    private void syncLastVisit(Long patientId, Long doctorId, Date time) {
        BizPatient update = new BizPatient();
        update.setId(patientId);
        update.setLastVisitTime(time);
        if (doctorId != null) {
            update.setLastDoctorId(doctorId);
        }
        patientMapper.updateById(update);
    }

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
