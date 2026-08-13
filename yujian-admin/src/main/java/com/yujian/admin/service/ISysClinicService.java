package com.yujian.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yujian.common.system.domain.SysClinic;

import java.util.List;

public interface ISysClinicService extends IService<SysClinic> {

    List<SysClinic> selectClinicList(SysClinic clinic);

    List<SysClinic> selectClinicTree(SysClinic clinic);

    boolean checkClinicCodeUnique(SysClinic clinic);

    int insertClinic(SysClinic clinic);

    int updateClinic(SysClinic clinic);

    int deleteClinicById(Long id);
}
