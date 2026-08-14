package com.yujian.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yujian.common.biz.domain.BizAppointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface BizAppointmentMapper extends BaseMapper<BizAppointment> {

    IPage<BizAppointment> selectAppointmentPage(Page<BizAppointment> page,
                                                @Param("clinicId") Long clinicId,
                                                @Param("keyword") String keyword,
                                                @Param("doctorId") Long doctorId,
                                                @Param("consultantId") Long consultantId,
                                                @Param("visitType") Integer visitType,
                                                @Param("status") Integer status,
                                                @Param("appointSource") String appointSource,
                                                @Param("patientId") Long patientId,
                                                @Param("beginTime") Date beginTime,
                                                @Param("endTime") Date endTime);

    List<BizAppointment> selectCalendarList(@Param("clinicId") Long clinicId,
                                            @Param("beginTime") Date beginTime,
                                            @Param("endTime") Date endTime,
                                            @Param("doctorId") Long doctorId,
                                            @Param("statusList") List<Integer> statusList);

    Map<String, Object> selectTodayStats(@Param("clinicId") Long clinicId,
                                         @Param("dayStart") Date dayStart,
                                         @Param("dayEnd") Date dayEnd);

    /** 左侧状态筛选计数 */
    List<Map<String, Object>> selectStatusCount(@Param("clinicId") Long clinicId,
                                                @Param("beginTime") Date beginTime,
                                                @Param("endTime") Date endTime,
                                                @Param("doctorId") Long doctorId);

    /** 医生时间冲突（时间段重叠） */
    int countDoctorConflict(@Param("clinicId") Long clinicId,
                            @Param("doctorId") Long doctorId,
                            @Param("startTime") Date startTime,
                            @Param("endTime") Date endTime,
                            @Param("excludeId") Long excludeId);

    IPage<BizAppointment> selectRecyclePage(Page<BizAppointment> page,
                                            @Param("clinicId") Long clinicId,
                                            @Param("keyword") String keyword,
                                            @Param("doctorId") Long doctorId,
                                            @Param("consultantId") Long consultantId,
                                            @Param("beginTime") Date beginTime,
                                            @Param("endTime") Date endTime);

    int restoreById(@Param("id") Long id);

    int permanentDeleteById(@Param("id") Long id);
}
