package com.yujian.admin.service;

import com.yujian.admin.mapper.BizPatientLogMapper;
import com.yujian.common.biz.domain.BizPatientLog;
import com.yujian.common.core.context.SecurityContextHolder;
import com.yujian.common.core.domain.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PatientLogHelper {

    @Autowired
    private BizPatientLogMapper patientLogMapper;

    public void write(Long patientId, Long clinicId, String action, String content) {
        BizPatientLog log = new BizPatientLog();
        log.setPatientId(patientId);
        log.setClinicId(clinicId);
        log.setAction(action);
        log.setContent(content);
        LoginUser user = SecurityContextHolder.getLoginUser();
        if (user != null) {
            log.setOperatorId(user.getUserId());
            log.setOperatorName(user.getName());
        }
        log.setCreateTime(new Date());
        patientLogMapper.insert(log);
    }
}
