package com.yujian.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yujian.common.biz.domain.BizPatient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BizPatientMapper extends BaseMapper<BizPatient> {

    IPage<BizPatient> selectPatientPage(Page<BizPatient> page,
                                        @Param("clinicId") Long clinicId,
                                        @Param("keyword") String keyword,
                                        @Param("doctorId") Long doctorId,
                                        @Param("firstDoctorId") Long firstDoctorId,
                                        @Param("tagId") Long tagId);

    String selectMaxMedicalRecordNo(@Param("clinicId") Long clinicId);
}
