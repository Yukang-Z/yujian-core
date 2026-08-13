package com.yujian.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yujian.common.biz.domain.BizAppointment;
import com.yujian.common.core.domain.PageResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IBizAppointmentService extends IService<BizAppointment> {

    PageResult<BizAppointment> selectPage(String keyword, Long clinicId, Long doctorId, Long consultantId,
                                          Integer visitType, Integer status,
                                          Date beginTime, Date endTime,
                                          long pageNum, long pageSize);

    /** 日历视图（天/周/月） */
    List<BizAppointment> selectCalendar(Long clinicId, Date beginTime, Date endTime,
                                        Long doctorId, List<Integer> statusList);

    BizAppointment selectById(Long id);

    int insertAppointment(BizAppointment appointment);

    int updateAppointment(BizAppointment appointment);

    int deleteAppointment(Long id);

    /** 变更状态：确认/到达/治疗中/离开/取消等 */
    int updateStatus(Long id, Integer status);

    /** 接诊入位 */
    int seatPatient(Long id);

    /** 首页今日统计 */
    Map<String, Object> todayStats(Long clinicId);
}
