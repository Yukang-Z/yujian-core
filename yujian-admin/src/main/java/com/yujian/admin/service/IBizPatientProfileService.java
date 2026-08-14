package com.yujian.admin.service;

import com.yujian.common.biz.domain.BizPatient;
import com.yujian.common.core.domain.PageResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 患者档案（侧栏 / 详情头 / 就诊时间线）
 */
public interface IBizPatientProfileService {

    /**
     * 左侧患者列表
     * type: today | all | recent
     */
    PageResult<BizPatient> sidebar(String type, Long clinicId, String keyword, Date day,
                                   long pageNum, long pageSize);

    /** 患者详情聚合：基本信息 + 价值卡片 + 标签 */
    Map<String, Object> profile(Long patientId);

    /**
     * 就诊信息时间线（预约 + 处置）
     */
    List<Map<String, Object>> visitTimeline(Long patientId, Long clinicId,
                                            Date beginTime, Date endTime);
}
