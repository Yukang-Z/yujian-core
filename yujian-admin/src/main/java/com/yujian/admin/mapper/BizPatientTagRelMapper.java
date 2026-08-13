package com.yujian.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yujian.common.biz.domain.BizPatientTagRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BizPatientTagRelMapper extends BaseMapper<BizPatientTagRel> {

    int deleteByPatientId(@Param("patientId") Long patientId);

    List<Long> selectTagIdsByPatientId(@Param("patientId") Long patientId);
}
