package com.yujian.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yujian.common.biz.domain.BizAppointment;
import com.yujian.common.biz.domain.BizAppointmentLog;
import com.yujian.common.core.domain.PageResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IBizAppointmentService extends IService<BizAppointment> {

    PageResult<BizAppointment> selectPage(String keyword, Long clinicId, Long doctorId, Long consultantId,
                                          Integer visitType, Integer status, String appointSource,
                                          Date beginTime, Date endTime,
                                          long pageNum, long pageSize);

    List<BizAppointment> selectCalendar(Long clinicId, Date beginTime, Date endTime,
                                        Long doctorId, List<Integer> statusList);

    /** 天视图：按医生分组 + 各医生预约数 */
    Map<String, Object> selectDayGrid(Long clinicId, Date day, List<Integer> statusList);

    BizAppointment selectById(Long id);

    int insertAppointment(BizAppointment appointment);

    int updateAppointment(BizAppointment appointment);

    /** 软删除进回收站 */
    int deleteAppointment(Long id, String cancelReason);

    int updateStatus(Long id, Integer status, String remark);

    int confirm(Long id);

    int cancel(Long id, String cancelReason);

    int seatPatient(Long id);

    Map<String, Object> todayStats(Long clinicId);

    /** 左侧状态筛选计数（含全部） */
    Map<String, Object> statusCount(Long clinicId, Date beginTime, Date endTime, Long doctorId);

    PageResult<BizAppointment> selectRecyclePage(String keyword, Long clinicId, Long doctorId, Long consultantId,
                                                 Date beginTime, Date endTime, long pageNum, long pageSize);

    int restore(Long id);

    int permanentDelete(Long id);

    List<BizAppointmentLog> selectLogs(Long appointmentId);
}
