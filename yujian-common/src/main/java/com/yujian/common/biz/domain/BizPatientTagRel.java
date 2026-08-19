package com.yujian.common.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 患者-标签关联实体，对应表 t_patient_tag_rel。
 * 用于业务接口请求/响应数据传输（患者打标签、标签批量保存等场景）。
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

    /** 患者ID，关联 t_patient.id */
    private Long patientId;

    /** 标签ID，关联 t_patient_tag.id */
    private Long tagId;
}
