package com.yujian.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yujian.common.biz.domain.BizAppointment;
import com.yujian.common.biz.domain.BizAppointmentLog;
import com.yujian.common.core.domain.PageResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 预约管理服务
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
public interface IBizAppointmentService extends IService<BizAppointment> {

    /**
     * 预约分页列表；clinicId 授权范围内生效
     *
     * @param keyword         患者关键字
     * @param clinicId        授权诊所，空=会话
     * @param doctorId        医生
     * @param consultantId    咨询师
     * @param visitType       就诊类型
     * @param statusList      状态多选
     * @param appointType     预约类型（pending=待确定）
     * @param appointSource   预约来源
     * @param beginTime       预约开始时间起
     * @param endTime         预约开始时间止
     * @param createBeginTime 创建时间起
     * @param createEndTime   创建时间止
     * @param pageNum         页码
     * @param pageSize        页大小
     * @return 分页结果（含 clinicName、items）
     */
    PageResult<BizAppointment> selectPage(String keyword, Long clinicId, Long doctorId, Long consultantId,
                                          Integer visitType, List<Integer> statusList, String appointType,
                                          String appointSource, Date beginTime, Date endTime,
                                          Date createBeginTime, Date createEndTime,
                                          long pageNum, long pageSize);

    List<BizAppointment> selectCalendar(Long clinicId, Date beginTime, Date endTime,
                                        Long doctorId, List<Integer> statusList);

    /** 天视图：按医生分组；clinicId 授权范围内生效；doctorIds 过滤列 */
    Map<String, Object> selectDayGrid(Long clinicId, Date day, List<Integer> statusList, List<Long> doctorIds);

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

    /**
     * 回收站分页；clinicId 授权范围内生效
     */
    PageResult<BizAppointment> selectRecyclePage(String keyword, Long clinicId, Long doctorId, Long consultantId,
                                                 Date beginTime, Date endTime, long pageNum, long pageSize);

    int restore(Long id);

    int permanentDelete(Long id);

    /**
     * 清空回收站（物理删除）；clinicId 授权生效；可选按预约开始时间范围
     *
     * @param clinicId  授权诊所，空=会话
     * @param beginTime 预约开始时间起，可空
     * @param endTime   预约开始时间止，可空
     * @return 删除条数
     */
    int clearRecycle(Long clinicId, Date beginTime, Date endTime);

    List<BizAppointmentLog> selectLogs(Long appointmentId);
}
