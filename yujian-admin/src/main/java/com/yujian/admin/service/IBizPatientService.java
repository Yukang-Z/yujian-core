package com.yujian.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yujian.common.biz.domain.BizPatient;
import com.yujian.common.core.domain.PageResult;

import java.util.List;

public interface IBizPatientService extends IService<BizPatient> {

    PageResult<BizPatient> selectPage(String keyword, Long clinicId, Long doctorId,
                                      Long firstDoctorId, Long tagId, long pageNum, long pageSize);

    BizPatient selectById(Long id);

    /** 全局搜索：姓名/手机/病历号/拼音 */
    List<BizPatient> search(String keyword, Long clinicId, int limit);

    int insertPatient(BizPatient patient);

    int updatePatient(BizPatient patient);

    int deletePatient(Long id);

    /** 保存并预约：返回患者ID，前端再跳转预约 */
    Long saveAndAppoint(BizPatient patient);

    /** 保存并到达：创建患者 + 当日到诊预约 */
    Long saveAndArrive(BizPatient patient);
}
