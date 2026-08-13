package com.yujian.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yujian.common.biz.domain.BizDictData;
import com.yujian.common.biz.domain.BizDictType;
import com.yujian.common.biz.domain.BizPatientSource;
import com.yujian.common.biz.domain.BizPatientTag;
import com.yujian.common.biz.domain.BizTreatItem;

import java.util.List;

public interface IBizBasicDataService {

    List<BizDictData> selectDictByType(String dictType);

    List<BizDictType> selectDictTypeList();

    int saveDictData(BizDictData data);

    int deleteDictData(Long id);

    List<BizPatientTag> selectTagList(Long clinicId);

    int saveTag(BizPatientTag tag);

    int deleteTag(Long id);

    List<BizPatientSource> selectSourceTree(Long clinicId);

    int saveSource(BizPatientSource source);

    int deleteSource(Long id);

    List<BizTreatItem> selectTreatItemList(Long clinicId);

    int saveTreatItem(BizTreatItem item);

    int deleteTreatItem(Long id);

    /** 可预约医生列表（日历列） */
    List<?> selectDoctorList(Long clinicId);
}
