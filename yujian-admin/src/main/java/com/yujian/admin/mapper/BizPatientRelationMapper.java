package com.yujian.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yujian.common.biz.domain.BizPatientRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BizPatientRelationMapper extends BaseMapper<BizPatientRelation> {

    List<BizPatientRelation> selectByPatientId(@Param("patientId") Long patientId);
}
