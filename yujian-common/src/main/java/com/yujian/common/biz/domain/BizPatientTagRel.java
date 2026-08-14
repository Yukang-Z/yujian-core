package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 患者-标签关联实体，对应表 t_patient_tag_rel
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Data
@TableName("t_patient_tag_rel")
public class BizPatientTagRel implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 患者ID */
    private Long patientId;

    /** 标签ID */
    private Long tagId;
}
