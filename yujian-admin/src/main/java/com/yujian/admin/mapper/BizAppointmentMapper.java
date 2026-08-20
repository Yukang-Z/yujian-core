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

/**
 * 预约 Mapper
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Mapper
public interface BizAppointmentMapper extends BaseMapper<BizAppointment> {

    /**
     * 预约分页列表（含患者/医生/诊所名等联表字段）
     *
     * @param page             分页
     * @param clinicId         诊所ID
     * @param keyword          关键字
     * @param doctorId         医生ID
     * @param consultantId     咨询师ID
     * @param visitType        就诊类型
     * @param statusList       状态多选，可空
     * @param appointType      预约类型（含 pending）
     * @param appointSource    预约来源
     * @param patientId        患者ID
     * @param beginTime        预约开始时间起
     * @param endTime          预约开始时间止
     * @param createBeginTime  创建时间起
     * @param createEndTime    创建时间止
     * @return 分页结果
     */
    IPage<BizAppointment> selectAppointmentPage(Page<BizAppointment> page,
                                                @Param("clinicId") Long clinicId,
                                                @Param("keyword") String keyword,
                                                @Param("doctorId") Long doctorId,
                                                @Param("consultantId") Long consultantId,
                                                @Param("visitType") Integer visitType,
                                                @Param("statusList") List<Integer> statusList,
                                                @Param("appointType") String appointType,
                                                @Param("appointSource") String appointSource,
                                                @Param("patientId") Long patientId,
                                                @Param("beginTime") Date beginTime,
                                                @Param("endTime") Date endTime,
                                                @Param("createBeginTime") Date createBeginTime,
                                                @Param("createEndTime") Date createEndTime);

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

    /**
     * 按主键查询预约（含已进回收站），用于还原/彻底删除前的授权校验
     *
     * @param id 预约ID
     * @return 预约实体，不存在返回 null
     */
    BizAppointment selectByIdIgnoreDelete(@Param("id") Long id);

    /**
     * 查询回收站中待清空的预约 ID
     *
     * @param clinicId  诊所ID
     * @param beginTime 预约开始时间起，可空
     * @param endTime   预约开始时间止，可空
     * @return 预约 ID 列表
     */
    List<Long> selectRecycleIds(@Param("clinicId") Long clinicId,
                                @Param("beginTime") Date beginTime,
                                @Param("endTime") Date endTime);

    /**
     * 按 ID 列表物理删除回收站预约
     *
     * @param ids 预约 ID 列表
     * @return 影响行数
     */
    int permanentDeleteByIds(@Param("ids") List<Long> ids);
}
